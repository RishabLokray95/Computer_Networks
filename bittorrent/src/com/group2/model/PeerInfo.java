package com.group2.model;

public class PeerInfo {
    private Integer peerId;
    private String host;
    private Integer port;
    private boolean hasFile;
    private BitField bitField;
    private boolean isInterested;

    public PeerInfo(Integer peerId, String host, Integer port, boolean hasFile, Integer bitFieldSize) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
        this.bitField = new BitField(bitFieldSize, hasFile);
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

    public BitField getBitField() {
        return bitField;
    }

    public boolean isInterested() {
        return isInterested;
    }

    public void setInterested(boolean interested) {
        isInterested = interested;
    }
}
