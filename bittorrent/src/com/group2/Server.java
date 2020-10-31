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

import static com.group2.PeerProcess.clientsMap;
import static com.group2.PeerProcess.peerInfoMap;

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
            Log.setInfo("Peer "+myPeerId+" is connected from peer "+connectedPeerId+".");
            if (!clientsMap.containsKey(receivedHsMsg.getPeerId())) {
                System.out.println("Send Handshake from "+ myPeerId + "to" + connectedPeerId);
                //construct a new handshake message and send to the peer to complete handshake
                HandShakeMessage newHsMsg = new HandShakeMessage(receivedHsMsg.getPeerId());
                //send via client
                Client newClient = new Client(myPeerId, peerInfoMap.get(receivedHsMsg.getPeerId()));
                clientsMap.put(this.connectedPeerId, newClient);
                newClient.sendHandshakeRequest();
            }
            //Check if peer has bitfield set
            if(!peerInfoMap.get(myPeerId).getBitField().isEmpty()) {
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
            if(receivedMsgType == MessageType.BITFIELD.getMessageTypeValue())
                bitFieldHandler(receivedMsg);

            if(receivedMsgType == MessageType.INTERESTED.getMessageTypeValue())
                interestedHandler(receivedMsg);

            if(receivedMsgType == MessageType.NOTINTERESTED.getMessageTypeValue())
                notInterestedHandler(receivedMsg);

        }

        private void bitFieldHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(this.connectedPeerId).getBitField().setBitFieldMessage(
                    ((BitField) receivedMsg.getMessagePayload()).getBitFieldMessage());
            System.out.println("Bitfiled has been set");
            //Check with own Bitfield and send interested if peer has any interesting pieces
            if(peerInfoMap.get(connectedPeerId).getBitField().isInteresting(peerInfoMap.get(myPeerId).getBitField())){
                System.out.println(myPeerId + " sending interested to " + connectedPeerId);
                ActualMessage interestedMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType(MessageType.INTERESTED.getMessageTypeValue())
                                .build();
                clientsMap.get(this.connectedPeerId).sendMessage(interestedMessage);
            }
        }

        private void interestedHandler(ActualMessage receiveMsg){
            peerInfoMap.get(connectedPeerId).setInterested(true);
            Log.setInfo("Peer " +myPeerId+ " received the ‘interested’ message from " + connectedPeerId);
        }

        private void notInterestedHandler(ActualMessage receiveMsg){
            peerInfoMap.get(connectedPeerId).setInterested(false);
        }
    }
}
