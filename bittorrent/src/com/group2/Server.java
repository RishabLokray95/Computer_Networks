package com.group2;

import com.group2.model.ActualMessage;
import com.group2.model.BitField;
import com.group2.model.HandShakeMessage;
import com.group2.model.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import static com.group2.PeerProcess.*;

public class Server extends Thread {

    private Integer myPeerId;


    public Server(Integer myId){
        this.myPeerId = myId;
    }


    public Integer getMyPeerId() {
        return myPeerId;
    }

    public void run() {
        System.out.println("The server on peer " + myPeerId + " is running.");
        try (ServerSocket listener = new ServerSocket(peerInfoMap.get(myPeerId).getPort())) {
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
            if (!clientsMap.containsKey(receivedHsMsg.getPeerId())) {
                System.out.println("Send Handshake from " + myPeerId + "to" + connectedPeerId);
                //construct a new handshake message and send to the peer to complete handshake
                HandShakeMessage newHsMsg = new HandShakeMessage(receivedHsMsg.getPeerId());
                //send via client
                Client newClient = new Client(myPeerId, peerInfoMap.get(receivedHsMsg.getPeerId()));
                clientsMap.put(this.connectedPeerId, newClient);
                newClient.sendHandshakeRequest();
            }
            //Check if peer has bitfield set
            if (!peerInfoMap.get(myPeerId).getBitField().isEmpty()) {
                ActualMessage bitFieldMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.BITFIELD.getMessageTypeValue())
                                .withMessagePayload(peerInfoMap.get(myPeerId).getBitField())
                                .build();
                clientsMap.get(this.connectedPeerId).sendMessage(bitFieldMessage);
            }
        }

        private void handleActualMessage(ActualMessage receivedMsg) {
            byte receivedMsgType = receivedMsg.getMessageType();
            if (receivedMsgType == MessageType.BITFIELD.getMessageTypeValue())
                bitFieldHandler(receivedMsg);

            if (receivedMsgType == MessageType.INTERESTED.getMessageTypeValue())
                interestedHandler(receivedMsg);

            if (receivedMsgType == MessageType.NOTINTERESTED.getMessageTypeValue())
                notInterestedHandler(receivedMsg);

            if (receivedMsgType == MessageType.UNCHOKE.getMessageTypeValue())
                unchokeHandler(receivedMsg);

            if (receivedMsgType == MessageType.CHOKE.getMessageTypeValue())
                chokeHandler(receivedMsg);

            if (receivedMsgType == MessageType.REQUEST.getMessageTypeValue())
                requestHandler(receivedMsg);

            if (receivedMsgType == MessageType.PIECE.getMessageTypeValue())
                pieceHandler(receivedMsg);

        }

        private void bitFieldHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(this.connectedPeerId).getBitField().setBitFieldMessage(
                    ((BitField) receivedMsg.getMessagePayload()).getBitFieldMessage());
            System.out.println("Bitfiled has been set");
            //Check with own Bitfield and send interested if peer has any interesting pieces
            if (peerInfoMap.get(connectedPeerId).getBitField().isInteresting(peerInfoMap.get(myPeerId).getBitField())) {
                System.out.println(myPeerId + " sending interested to " + connectedPeerId);
                ActualMessage interestedMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.INTERESTED.getMessageTypeValue())
                                .build();
                clientsMap.get(this.connectedPeerId).sendMessage(interestedMessage);
            }
        }

        private void interestedHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(connectedPeerId).setInterested(true);
            Log.setInfo("Peer " + myPeerId + " received the ‘interested’ message from " + connectedPeerId);
            //Unchoking peer for now. Will later be done by preferred or optimistic way
            //TODO: remove the below message
            ActualMessage unchokeMessage =
                    ActualMessage.ActualMessageBuilder.builder()
                            .withMessageType(MessageType.UNCHOKE.getMessageTypeValue())
                            .build();
            clientsMap.get(this.connectedPeerId).sendMessage(unchokeMessage);
        }

        private void notInterestedHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(connectedPeerId).setInterested(false);
            Log.setInfo("Peer " + myPeerId + " received the ‘interested’ message from " + connectedPeerId);
        }

        private void chokeHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(connectedPeerId).setHasChoked(true);
            Log.setInfo("Peer " + myPeerId + " is choked by " + connectedPeerId);
        }

        private void unchokeHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(connectedPeerId).setHasChoked(false);
            Log.setInfo("Peer " + myPeerId + " is unchoked by " + connectedPeerId);
            //Check if the peer has any interesting
            int interestingField = peerInfoMap.get(connectedPeerId)
                    .getBitField().getInterestingField(peerInfoMap.get(myPeerId).getBitField());

            System.out.println("Interesting Field " + interestingField);
            if (interestingField >= 0) {
                //Create Request
                ActualMessage requestMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.REQUEST.getMessageTypeValue())
                                .withMessagePayload(interestingField)
                                .build();
                clientsMap.get(this.connectedPeerId).sendMessage(requestMessage);
                peerInfoMap.get(connectedPeerId).setRequestedBitIndex(interestingField);

            }
        }

        private void requestHandler(ActualMessage receivedMsg) {
            int pieceIndex = (int) receivedMsg.getMessagePayload();
            Log.setInfo("Received request from " + connectedPeerId + " to " + myPeerId + " for piece Index " + pieceIndex);

            //Sending that piece to piece handler
            Integer startIndex = pieceIndex * commonConfiguration.getPieceSize();
            Integer endIndex = Math.min(startIndex + commonConfiguration.getPieceSize() - 1,commonConfiguration.getFileSize()-1);
            byte[] requested_data = Arrays.copyOfRange(file, startIndex, endIndex);

            ActualMessage pieceMessage =
                    ActualMessage.ActualMessageBuilder.builder()
                            .withMessageType(MessageType.PIECE.getMessageTypeValue())
                            .withMessagePayload(requested_data)
                            .build();

            clientsMap.get(this.connectedPeerId).sendMessage(pieceMessage);
        }

        private void pieceHandler(ActualMessage receivedMsg) {
            byte[] newPayloadArray = (byte[]) receivedMsg.getMessagePayload();

            Integer destStartIndex = peerInfoMap.get(connectedPeerId).getRequestedBitIndex() * commonConfiguration.getPieceSize();
            System.arraycopy(newPayloadArray, 0, file,destStartIndex, newPayloadArray.length);

            //Update my bitfield.
            peerInfoMap.get(myPeerId).getBitField().setBitField(peerInfoMap.get(connectedPeerId).getRequestedBitIndex());

            //Update no of pieces counter to print in log file.
            peerInfoMap.get(myPeerId).setCurrentNumberOfPieces(peerInfoMap.get(myPeerId).getCurrentNumberOfPieces() + 1);

            Log.setInfo("Peer " + myPeerId + " has downloaded the piece " + peerInfoMap
                    .get(connectedPeerId)
                    .getRequestedBitIndex() + " from " + connectedPeerId + ". Now the number of pieces it has is " + peerInfoMap
                    .get(myPeerId).getCurrentNumberOfPieces() + ".");


            if(peerInfoMap.get(myPeerId).getCurrentNumberOfPieces() == 4){
                System.out.print("");
            }
            if (peerInfoMap.get(myPeerId)
                    .getCurrentNumberOfPieces() == peerInfoMap
                    .get(myPeerId).getBitField().getBitFieldMessage().length) {
                //END CONNECTION
                System.out.println("RECEIVED ALL PIECES AND END CONNECTION");
                Path path = Paths.get("./bittorrent/RECEIVED_FILE.pdf");
                try {
                    Files.write(path, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                //CHECK if it is unchoked and then send interested message again.
                int interestingField = peerInfoMap.get(connectedPeerId)
                        .getBitField().getInterestingField(peerInfoMap.get(myPeerId).getBitField());
                if (!peerInfoMap.get(connectedPeerId).isHasChoked() && interestingField > -1) {

                    System.out.println("Interesting Field " + interestingField);
                    //Create Request
                    ActualMessage requestMessage =
                            ActualMessage.ActualMessageBuilder.builder()
                                    .withMessageType(MessageType.REQUEST.getMessageTypeValue())
                                    .withMessagePayload(interestingField)
                                    .build();
                    clientsMap.get(this.connectedPeerId).sendMessage(requestMessage);
                    peerInfoMap.get(connectedPeerId).setRequestedBitIndex(interestingField);
                }
            }
        }
    }
}
