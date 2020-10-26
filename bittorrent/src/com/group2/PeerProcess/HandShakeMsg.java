package PeerProcess;

public class HandShakeMsg {
    public String hand_shake_header = "P2PFILESHARINGPROJ";
    public byte[] zero_bits = new byte[10];
    public int peer_id;

    HandShakeMsg(int peerid){
        peer_id = peerid;
    }


    public int getPeerId(){
        return peer_id;
    }
}
