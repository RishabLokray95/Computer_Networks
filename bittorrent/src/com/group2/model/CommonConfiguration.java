package com.group2.model;

public class CommonConfiguration {
    private String numberOfPreferredNeighbors;
    private String unchokingInterval;
    private String optimisticUnchokingInterval;
    private String fileName;
    private Integer fileSize;
    private Integer pieceSize;

    public CommonConfiguration(String numberOfPreferredNeighbors, String unchokingInterval, String optimisticUnchokingInterval, String fileName, Integer fileSize, Integer pieceSize) {
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public String getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }

    public String getUnchokingInterval() {
        return unchokingInterval;
    }

    public String getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public Integer getPieceSize() {
        return pieceSize;
    }
}
