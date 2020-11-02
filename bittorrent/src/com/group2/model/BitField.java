package com.group2.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class BitField implements Serializable {

    private AtomicBoolean[] bitField;
    private boolean isEmpty = true;
    private Integer haveBitsCount = 0;

    BitField(Integer payloadSize, Boolean hasFile) {
        this.bitField = new AtomicBoolean[payloadSize];

        if (hasFile) {
            this.isEmpty = false;
            for(int i = 0; i < bitField.length; i++)
                bitField[i] = new AtomicBoolean(true);
        }else {
            for(int i = 0; i < bitField.length; i++)
                bitField[i] = new AtomicBoolean();
        }
    }
    //use setter maybe?
    public AtomicBoolean[] getBitField() {
        return bitField;
    }

    public AtomicBoolean hasBitIndex(Integer index) {
        return bitField[index];
    }

    public synchronized void setBit(Integer index){
        this.isEmpty = false;
        this.haveBitsCount += 1;
        bitField[index].set(true);
    }

    public synchronized void setBit(AtomicBoolean[] bitField){
        this.isEmpty = false;
        this.haveBitsCount = (int) Arrays.stream(bitField).filter(AtomicBoolean::get).count();
        this.bitField = bitField;
    }

    public boolean isInteresting(BitField peerBitField){
        for(int i = 0; i < bitField.length ; i++){
            if(bitField[i].get() && !peerBitField.bitField[i].get()) {
                return true;
            }
        }
        return false;
    }

    public Integer getInterestingFieldIndex(BitField peerBitField){
        for(int i = 0; i < bitField.length ; i++){
            if(bitField[i].get() && !peerBitField.bitField[i].get()) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }

    public Integer haveBitsCount() {
        return haveBitsCount;
    }
}
