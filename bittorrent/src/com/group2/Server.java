package com.group2;

import com.group2.model.ActualMessage;
import com.group2.model.BitField;
import com.group2.model.HandShakeMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static com.group2.PeerProcess.clientsMap;
import static com.group2.PeerProcess.peerInfoMap;

public class Server extends Thread {

    private Integer myPeerId;


    public Server(Integer myId) {
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

            if (clientsMap.containsKey(receivedHsMsg.getPeerId())) {
                //send bitfield msg if data available
                //else update the state : state needs to be defined yet
                System.out.println("Send bitfield message to peer");
                //create bitfield message and send
                ActualMessage bitFieldMessage =
                        ActualMessage.ActualMessageBuilder.builder()
                                .withMessageType((byte) 5)
                                .withMessagePayload(peerInfoMap.get(myPeerId).getBitField())
                                .build();
                clientsMap.get(this.connectedPeerId).sendMessage(bitFieldMessage);

            } else {
                //construct a new handshake message and send to the peer to complete handshake
                HandShakeMessage newHsMsg = new HandShakeMessage(receivedHsMsg.getPeerId());
                //send via client
                Client newClient = new Client(myPeerId, peerInfoMap.get(receivedHsMsg.getPeerId()));
                clientsMap.put(this.connectedPeerId, newClient);
                newClient.sendHandshakeRequest();
            }
        }

        private void handleActualMessage(ActualMessage receivedMsg) {
            switch (receivedMsg.getMessageType()) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    bitFieldHandler(receivedMsg);
                    break;
                case 6:
                    break;
                case 7:
                    break;
                default:
                    System.err.println("Error in message type");
            }
        }

        private void bitFieldHandler(ActualMessage receivedMsg) {
            peerInfoMap.get(this.connectedPeerId).getBitField().setBitFieldMessage(
                    ((BitField) receivedMsg.getMessagePayload()).getBitFieldMessage());
            System.out.println("Bitfiled has been set");

        }


    }

}
