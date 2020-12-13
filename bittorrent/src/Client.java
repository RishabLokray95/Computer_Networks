import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    PeerInfo peerInfo;
    PeerInfo myPeerInfo;

    //capitalized message read from the server

    public Client(PeerInfo myPeerId, PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
        this.myPeerInfo = myPeerId;
    }

    public Client(PeerInfo myPeerId, PeerInfo peerInfo, Socket requestSocket) {
        this.peerInfo = peerInfo;
        this.myPeerInfo = myPeerId;
        this.requestSocket = requestSocket;
    }

    public void sendHandshakeRequest() {
        try {
            //if (requestSocket == null) {
                requestSocket = new Socket(peerInfo.getHost(), peerInfo.getPort());
            //}
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            Log.setInfo("Peer " + myPeerInfo.getPeerId() + " makes a connection to Peer " + peerInfo.getPeerId() + ".");
            sendMessage(new HandShakeMessage(myPeerInfo.getPeerId()));
        } catch (ConnectException e) {
            Log.setInfo("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            Log.setInfo("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            in.close();
            out.close();
            requestSocket.shutdownInput();
            requestSocket.shutdownOutput();
            requestSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //send a message to the output stream
    void sendMessage(Object msg) {
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            Log.setInfo("Peer" + myPeerInfo.getPeerId() + " could not make a connection to Peer" + peerInfo.getPeerId() + ".");
            ioException.printStackTrace();
        }
    }

}
