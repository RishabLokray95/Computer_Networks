package com.group2;

import com.group2.model.HandShakeMessage;
import com.group2.model.PeerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket

    PeerInfo peerInfo;
    Integer myPeerId;

    //capitalized message read from the server

    public Client(Integer myPeerId, PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
        this.myPeerId = myPeerId;
    }

    public void sendHandshakeRequest() {
        try {
            requestSocket = new Socket(peerInfo.getHost(), peerInfo.getPort());
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            sendMessage(new HandShakeMessage(myPeerId));
        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream
    void sendMessage(Object msg) {
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
