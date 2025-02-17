import java.util.concurrent.atomic.AtomicBoolean;

public class PeerInfo {
    private Integer peerId;
    private String host;
    private Integer port;
    private boolean hasFile;
    private BitField bitFieldPayload;
    private AtomicBoolean isInterested = new AtomicBoolean(false);
    private AtomicBoolean hasChoked = new AtomicBoolean(true);
    private Integer requestedBitIndex;

    public PeerInfo(Integer peerId, String host, Integer port, boolean hasFile, Integer bitFieldSize) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
        this.bitFieldPayload = new BitField(bitFieldSize, hasFile);
    }

    public Integer getPeerId() {
        return peerId;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean value){
        hasFile = value;
    }

    public BitField getBitFieldPayload() {
        return bitFieldPayload;
    }

    public boolean isInterested() {
        return isInterested.get();
    }

    public void setInterested(boolean interested) {
        isInterested.set(interested);
    }

    public boolean isHasChoked() {
        return hasChoked.get();
    }

    public void setHasChoked(boolean hasChoked) {
        this.hasChoked.set(hasChoked);
    }

    public Integer getRequestedBitIndex() {
        return requestedBitIndex;
    }

    public void setRequestedBitIndex(Integer requestedBitIndex) {
        this.requestedBitIndex = requestedBitIndex;
    }

}
