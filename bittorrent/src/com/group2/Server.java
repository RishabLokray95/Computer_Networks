package com.group2;

import java.net.*;
import java.io.*;

import static com.group2.PeerProcess.peerInfoMap;

public class Server {

    private Integer myPeerId;

    public Server(Integer myId) {
        this.myPeerId = myId;
    }

    public void run() throws Exception {
        System.out.println("The server on peer "+ myPeerId + " is running.");
        try (ServerSocket listener = new ServerSocket(peerInfoMap.get(myPeerId).getPort())) {
            while (true) {
                new Handler(listener.accept()).start();
            }
        }
    }

    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private   message;    //message received from the client
        private Socket connection;
        private ObjectInputStream in;    //stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket



        Handler(Socket connection) {
            this.connection = connection;
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
                        System.out.println("Receive message: " + message + " from client " + no);
//                        //Capitalize all letters in the message
//                        MESSAGE = message.toUpperCase();
//                        //send MESSAGE back to the client
//                        sendMessage(MESSAGE);
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } catch (IOException ioException) {
                System.out.println("Disconnect with Client " + no);
            } finally {
                //Close connections
                try {
                    in.close();
                    out.close();
                    connection.close();
                } catch (IOException ioException) {
                    System.out.println("Disconnect with Client " + no);
                }
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg) {
            try {
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client " + no);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
