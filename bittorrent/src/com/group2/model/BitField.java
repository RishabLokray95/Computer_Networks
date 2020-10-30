package com.group2.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BitField implements Serializable {

    private AtomicBoolean[] bitFieldMessage;

    public BitField(Integer payloadSize, Boolean hasFile) {
        this.bitFieldMessage = new AtomicBoolean[payloadSize];
        if (hasFile) {
            setBitFieldRange(0, bitFieldMessage.length);
        }
    }
    //use setter maybe?
    public AtomicBoolean[] getBitFieldMessage() {
        return bitFieldMessage;
    }

    public void setBitField(Integer index){
        bitFieldMessage[index].set(true);
    }

    public void setBitFieldMessage(AtomicBoolean[] bitField){
        this.bitFieldMessage = bitField;
    }

    public void setBitFieldRange(Integer startIndex, Integer endIndex){
        for (Integer i = startIndex; i < endIndex; i++){
            bitFieldMessage[i].set(true);
        }
    }
}
