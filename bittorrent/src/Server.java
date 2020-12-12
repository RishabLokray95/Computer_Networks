import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Server extends Thread {

    private final Integer myPeerId;
    private static boolean fileWritten = false;


    public Server(Integer myId) {
        this.myPeerId = myId;

    }


    public Integer getMyPeerId() {
        return myPeerId;
    }

    public void run() {
        System.out.println("The server on peer " + myPeerId + " is running.");
        try (ServerSocket listener = new ServerSocket(PeerProcess.peerInfoMap.get(myPeerId).getPort())) {
            while (true) {
                new Handler(listener.accept(), myPeerId).start();
            }
        } catch (IOException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }


    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private Object message;    //message received from the client
        private Socket connection;
        private ObjectInputStream in;    //stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private Integer myPeerId;
        private Integer connectedPeerId;

        Handler(Socket connection, Integer myId) {
            this.connection = connection;
            this.myPeerId = myId;

        }

        public void run() {
            System.out.println("Peer ");
            try {

                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                try {
                    while (true) {
                        //receive the message sent from the client
                        message = in.readObject();
                        //show the message to the user
                        handleMessage(message);
                        //Thread.sleep(5000);
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } catch (Exception ioException) {
                System.out.println("Disconnect with Client " + connection.getInetAddress() + " Exception : " + ioException.getMessage());
            } finally {
                //Close connections
                try {
                    in.close();
                    out.close();
                    connection.close();
                } catch (IOException ioException) {
                    System.out.println("Disconnect with Client " + connection.getInetAddress());
                }
            }
        }

        private void handleMessage(Object message) {
            System.out.println("Receive message: " + message + " from client " + connection.getInetAddress());

            if (message instanceof HandShakeMessage) {
                //Check the clientsMap to see if handshake was previously sent
                HandShakeMessage receivedHsMsg = (HandShakeMessage) message;
                handleHandShakeMessage(receivedHsMsg);
            } else if (message instanceof ActualMessage) {
                ActualMessage receivedHsMsg = (ActualMessage) message;
                handleActualMessage(receivedHsMsg);
            }

        }

        private void handleHandShakeMessage(HandShakeMessage receivedHsMsg) {
            this.connectedPeerId = receivedHsMsg.getPeerId();
            Log.setInfo("Peer " + myPeerId + " is connected from peer " + connectedPeerId + ".");
            if (!PeerProcess.clientsMap.containsKey(receivedHsMsg.getPeerId())) {
                System.out.println("Send Handshake from " + myPeerId + "to" + connectedPeerId);
                //construct a new handshake message and send to the peer to complete handshake
                HandShakeMessage newHsMsg = new HandShakeMessage(receivedHsMsg.getPeerId());
                //send via client
                Client newClient = new Client(myPeerId, PeerProcess.peerInfoMap.get(receivedHsMsg.getPeerId()));
                PeerProcess.clientsMap.put(this.connectedPeerId, newClient);
                newClient.sendHandshakeRequest();
            }
            //Check if peer has bitfield set
            if (!PeerProcess.peerInfoMap.get(myPeerId).getBitFieldPayload().isEmpty()) {
                ActualMessage bitFieldMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.BITFIELD.getMessageTypeValue())
                                .withMessagePayload(PeerProcess.peerInfoMap.get(myPeerId).getBitFieldPayload())
                                .build();
                PeerProcess.clientsMap.get(this.connectedPeerId).sendMessage(bitFieldMessage);
            }
        }

        private void handleActualMessage(ActualMessage receivedMsg) {
            byte receivedMsgType = receivedMsg.getMessageType();
            if (receivedMsgType == MessageType.BITFIELD.getMessageTypeValue())
                bitFieldHandler(receivedMsg);

            else if (receivedMsgType == MessageType.INTERESTED.getMessageTypeValue())
                interestedHandler();

            else if (receivedMsgType == MessageType.NOTINTERESTED.getMessageTypeValue())
                notInterestedHandler();

            else if (receivedMsgType == MessageType.UNCHOKE.getMessageTypeValue())
                unchokeHandler(receivedMsg);

            else if (receivedMsgType == MessageType.CHOKE.getMessageTypeValue())
                chokeHandler();

            else if (receivedMsgType == MessageType.REQUEST.getMessageTypeValue())
                requestHandler(receivedMsg);

            else if (receivedMsgType == MessageType.PIECE.getMessageTypeValue())
                pieceHandler(receivedMsg);
            else if (receivedMsgType == MessageType.HAVE.getMessageTypeValue())
                haveHandler(receivedMsg);


        }

        private void bitFieldHandler(ActualMessage receivedMsg) {
            PeerProcess.peerInfoMap.get(this.connectedPeerId).getBitFieldPayload().setBit(
                    ((BitField) receivedMsg.getMessagePayload()).getBitField());
            if(PeerProcess.peerInfoMap.get(connectedPeerId).getBitFieldPayload().allBitSet()) {
                System.out.println("Setting 'has file' to true for peer" + connectedPeerId);
                PeerProcess.peerInfoMap.get(connectedPeerId).setHasFile(true);
                PeerProcess.peerInfoMap.get(connectedPeerId).setInterested(false);
            }
            System.out.println("Bitfiled has been set");
            //Check with own Bitfield and send interested if peer has any interesting pieces
            sendInterestedMessageIfNeeded();
            sendNotInterestedMessageIfNeeded(connectedPeerId);
        }

        private void sendInterestedMessageIfNeeded() {
            if (PeerProcess.peerInfoMap.get(connectedPeerId).getBitFieldPayload().isInteresting(PeerProcess.peerInfoMap.get(myPeerId).getBitFieldPayload())) {
                //peerInfoMap.get(connectedPeerId).setInterested(true);
                System.out.println(myPeerId + " sending interested to " + connectedPeerId);
                ActualMessage interestedMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.INTERESTED.getMessageTypeValue())
                                .build();
                PeerProcess.clientsMap.get(this.connectedPeerId).sendMessage(interestedMessage);
            }
        }

        private void sendNotInterestedMessageIfNeeded(Integer peerId) {
            if (PeerProcess.peerInfoMap.get(peerId).getBitFieldPayload().isNotInteresting(PeerProcess.peerInfoMap.get(myPeerId).getBitFieldPayload())) {
                //peerInfoMap.get(connectedPeerId).setInterested(false);
                System.out.println(myPeerId + " sending not interested to " + peerId);
                ActualMessage interestedMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.NOTINTERESTED.getMessageTypeValue())
                                .build();
                PeerProcess.clientsMap.get(peerId).sendMessage(interestedMessage);
            }
        }

        private void interestedHandler() {
            PeerProcess.peerInfoMap.get(connectedPeerId).setInterested(true);
            Log.setInfo("Peer " + myPeerId + " received the ‘interested’ message from " + connectedPeerId);

        }

        private void notInterestedHandler() {
            PeerProcess.peerInfoMap.get(connectedPeerId).setInterested(false);
            Log.setInfo("Peer " + myPeerId + " received the ‘not interested’ message from " + connectedPeerId);
        }

        private void chokeHandler() {
            PeerProcess.peerInfoMap.get(connectedPeerId).setHasChoked(true);
            Log.setInfo("Peer " + myPeerId + " is choked by " + connectedPeerId);
        }

        private void unchokeHandler(ActualMessage receivedMsg) {
            PeerProcess.peerInfoMap.get(connectedPeerId).setHasChoked(false);
            Log.setInfo("Peer " + myPeerId + " is unchoked by " + connectedPeerId);
            //Check if the peer rehas any interesting
            int interestingField = PeerProcess.peerInfoMap.get(connectedPeerId)
                    .getBitFieldPayload().getInterestingFieldIndex(PeerProcess.peerInfoMap.get(myPeerId).getBitFieldPayload());

            if (!PeerProcess.peerInfoMap.get(myPeerId).isHasFile() && interestingField >= 0) {
                System.out.println("Requesting Field " + interestingField);
                //Create Request
                ActualMessage requestMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.REQUEST.getMessageTypeValue())
                                .withMessagePayload(interestingField)
                                .build();
                PeerProcess.clientsMap.get(this.connectedPeerId).sendMessage(requestMessage);
                PeerProcess.peerInfoMap.get(connectedPeerId).setRequestedBitIndex(interestingField);

            }
        }

        private void requestHandler(ActualMessage receivedMsg) {
            int pieceIndex = (int) receivedMsg.getMessagePayload();
            Log.setInfo("Received request from " + connectedPeerId + " to " + myPeerId + " for piece Index " + pieceIndex);

            //Sending that piece to piece handler
            Integer startIndex = pieceIndex * PeerProcess.commonConfiguration.getPieceSize();
            Integer endIndex = Math.min(startIndex + PeerProcess.commonConfiguration.getPieceSize() - 1, PeerProcess.commonConfiguration.getFileSize() - 1);
            System.out.println("Sending data from " + startIndex + "to" + endIndex);
            byte[] requested_data = Arrays.copyOfRange(PeerProcess.file, startIndex, endIndex + 1);

            ActualMessage pieceMessage =
                    ActualMessage.ActualMessageBuilder.builder()
                            .withMessageType(MessageType.PIECE.getMessageTypeValue())
                            .withMessagePayload(requested_data)
                            .build();

            PeerProcess.clientsMap.get(this.connectedPeerId).sendMessage(pieceMessage);
        }

        private void haveHandler(ActualMessage receivedMsg) {
            // Update the piece the peer has
            PeerProcess.peerInfoMap.get(connectedPeerId).getBitFieldPayload().setBit((Integer) receivedMsg.getMessagePayload());
            if (PeerProcess.peerInfoMap.get(connectedPeerId).getBitFieldPayload().allBitSet()) {
                System.out.println(connectedPeerId  + " has received the complete file");
                PeerProcess.peerInfoMap.get(connectedPeerId).setHasFile(true);
            }
            Log.setInfo("Peer " + myPeerId + " received 'have' by " + connectedPeerId);
            // Update the isInterested field for the peer.
            sendInterestedMessageIfNeeded();
            // Also see if all the peers have received all chunks of the file, if yes, then terminate the process.
            if (PeerProcess.peerInfoMap.get(myPeerId).isHasFile() &&
                    PeerProcess.peerInfoMap.values().stream().map(PeerInfo::isHasFile).reduce(true, (x, y) -> x && y)) {
                terminate();
            }
        }

        private void pieceHandler(ActualMessage receivedMsg) {
            PeerInfo connectedPeerInfo = PeerProcess.peerInfoMap.get(connectedPeerId);
            PeerInfo myInfo = PeerProcess.peerInfoMap.get(myPeerId);
            //Copy the contents of the payload only if not already received from another peer
            if (!myInfo.getBitFieldPayload().hasBitIndex(connectedPeerInfo.getRequestedBitIndex()).get()) {

                byte[] newPayloadArray = (byte[]) receivedMsg.getMessagePayload();
                PeerProcess.downloadRates.addPeerDownloadDetails(connectedPeerId, newPayloadArray.length);
                int destStartIndex = connectedPeerInfo.getRequestedBitIndex() * PeerProcess.commonConfiguration.getPieceSize();
                System.arraycopy(newPayloadArray, 0, PeerProcess.file, destStartIndex, newPayloadArray.length);
                //Update my bitfield.
                myInfo.getBitFieldPayload().setBit(connectedPeerInfo.getRequestedBitIndex());
                //Update no of pieces counter to print in log file.
                //myInfo.setCurrentNumberOfPieces(myInfo.getCurrentNumberOfPieces() + 1);
                Log.setInfo("Peer " + myPeerId + " has downloaded the piece "
                        + connectedPeerInfo.getRequestedBitIndex() + " from "
                        + connectedPeerId + ". Now the number of pieces it has is " + myInfo.getBitFieldPayload().haveBitsCount() + ".");
                // Now the peer will send HAVE message to other peers and not un interested messages if needed
                ActualMessage msg = PeerProcess.HAVE_MESSAGE_UNBUILT.withMessagePayload(connectedPeerInfo.getRequestedBitIndex()).build();
                PeerProcess.peerInfoMap.keySet().stream().filter(x -> !x.equals(myPeerId) && PeerProcess.clientsMap.containsKey(x)).forEach(y -> {
                    PeerProcess.clientsMap.get(y).sendMessage(msg);
                    sendNotInterestedMessageIfNeeded(y);
                });

            }


            if (!fileWritten && myInfo.getBitFieldPayload().haveBitsCount().equals(myInfo.getBitFieldPayload().getBitField().length)) {

                System.out.println("RECEIVED ALL PIECES AND END CONNECTION");
                Log.setInfo("Peer " + myPeerId + " has downloaded the complete file.");

                // Send notInterested message to other peers if needed
//                peerInfoMap.keySet().stream().filter(x -> !x.equals(myPeerId)).forEach(this::sendNotInterestedMessageIfNeeded);

                //Path path = Paths.get("../RECEIVED_FILE_" + myPeerId + ".pdf");
                Path path = Paths.get(PeerProcess.commonConfiguration.getFileName());
                try {
                    Files.write(path, PeerProcess.file);
                    myInfo.setHasFile(true);
                    fileWritten = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Also see if all the peers have received all chunks of the file, if yes, then terminate the process.
                    if (PeerProcess.peerInfoMap.get(myPeerId).isHasFile() &&
                            PeerProcess.peerInfoMap.values().stream().map(PeerInfo::isHasFile).reduce(true, (x, y) -> x && y)) {
                        terminate();
                    }
                }

            } else {
                //CHECK if it is unchoked and send the request for next piece
                int interestingField = connectedPeerInfo.getBitFieldPayload().getInterestingFieldIndex(myInfo.getBitFieldPayload());
                if (!connectedPeerInfo.isHasChoked() && interestingField > -1) {

                    System.out.println("DEBUG (REMOVE LATER) Requesting Field " + interestingField);
                    //Create Request
                    ActualMessage requestMessage =
                            ActualMessage.ActualMessageBuilder.builder()
                                    .withMessageType(MessageType.REQUEST.getMessageTypeValue())
                                    .withMessagePayload(interestingField)
                                    .build();
                    PeerProcess.clientsMap.get(connectedPeerId).sendMessage(requestMessage);
                    connectedPeerInfo.setRequestedBitIndex(interestingField);
                }
            }
        }

        private void terminate() {
            try {
                Thread.sleep(11000);
                System.out.println("Shut down now since all the peers have file");
                System.exit(0);
            } catch (InterruptedException ex) {
                System.out.println("Not able to sleep, during shutdown");
            }

        }
    }
}
