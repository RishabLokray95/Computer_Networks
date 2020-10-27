package com.group2.model;

public class PeerInfo {
    private Integer peerId;
    private String host;
    private Integer port;
    private boolean hasFile;

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

    public PeerInfo(Integer peerId, String host, Integer port, boolean hasFile) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
    }
}
