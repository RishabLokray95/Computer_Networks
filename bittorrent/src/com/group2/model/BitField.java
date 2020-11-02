package com.group2.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BitField implements Serializable {

    private AtomicBoolean[] bitFieldMessage;
    private boolean isEmpty = true;

    public BitField(Integer payloadSize, Boolean hasFile) {
        this.bitFieldMessage = new AtomicBoolean[payloadSize];

        if (hasFile) {
            this.isEmpty = false;
            for(int i = 0; i < bitFieldMessage.length; i++)
                bitFieldMessage[i] = new AtomicBoolean(true);
        }else {
            for(int i = 0; i < bitFieldMessage.length; i++)
                bitFieldMessage[i] = new AtomicBoolean();
        }
    }
    //use setter maybe?
    public AtomicBoolean[] getBitFieldMessage() {
        return bitFieldMessage;
    }

    public AtomicBoolean hasBitIndex(Integer index) {
        return bitFieldMessage[index];
    }

    public void setBitField(Integer index){
        this.isEmpty = false;
        bitFieldMessage[index].set(true);
    }

    public void setBitField(AtomicBoolean[] bitField){
        this.isEmpty = false;
        this.bitFieldMessage = bitField;
    }

    public boolean isInteresting(BitField peerBitField){
        for(int i=0 ; i < bitFieldMessage.length ; i++){
            if(bitFieldMessage[i].get() && !peerBitField.bitFieldMessage[i].get()) {
                return true;
            }
        }
        return false;
    }

    public Integer getInterestingFieldIndex(BitField peerBitField){
        for(int i=0 ; i < bitFieldMessage.length ; i++){
            if(bitFieldMessage[i].get() && !peerBitField.bitFieldMessage[i].get()) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
}
