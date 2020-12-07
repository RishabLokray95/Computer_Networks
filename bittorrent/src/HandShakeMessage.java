import java.io.Serializable;

public class HandShakeMessage implements Serializable {
    private String handShakeHeader = "P2PFILESHARINGPROJ";
    private byte[] zeroBits = new byte[10];
    private int peerId;

    public HandShakeMessage(int peerId) {
        this.peerId = peerId;
    }

    public String getHandShakeHeader() {
        return handShakeHeader;
    }

    public byte[] getZeroBits() {
        return zeroBits;
    }

    public void setZeroBits(byte[] zeroBits) {
        this.zeroBits = zeroBits;
    }

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }
}
