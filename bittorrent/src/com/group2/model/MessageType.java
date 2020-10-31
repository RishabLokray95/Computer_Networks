package com.group2.model;

public enum MessageType {
    CHOKE ((byte) 0),
    UNCHOKE ((byte) 1),
    INTERESTED ((byte) 2),
    NOTINTERESTED ((byte) 3),
    HAVE ((byte) 4),
    BITFIELD ((byte)5),
    REQUEST ((byte)6),
    PIECE((byte)7);

    private final byte messageTypeValue;

    MessageType(byte messageType) {
        this.messageTypeValue = messageType;
    }

    public byte getMessageTypeValue(){
        return this.messageTypeValue;
    }

}
