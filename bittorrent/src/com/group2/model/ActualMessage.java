package com.group2.model;

public class ActualMessage {
    private Integer messageLength;
    private Byte messageType;
    private Byte[] messagePayload;

    public ActualMessage(Byte messageType, Byte[] messagePayload) {
        this.messageType = messageType;
        this.messagePayload = messagePayload;
        this.messageLength = 1 + messagePayload.length;
    }

    public Integer getMessageLength() {
        return messageLength;
    }

    public Byte getMessageType() {
        return messageType;
    }

    public Byte[] getMessagePayload() {
        return messagePayload;
    }
}
