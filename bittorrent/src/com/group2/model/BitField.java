package com.group2.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BitField implements Serializable {

    private AtomicBoolean[] bitFieldMessage;
    private Integer length;
    private boolean isEmpty = true;

    public BitField(Integer payloadSize, Boolean hasFile) {
        this.bitFieldMessage = new AtomicBoolean[payloadSize];
        this.length = payloadSize;
        if (hasFile) {
            this.isEmpty = false;
            for(int i = 0; i < payloadSize; i++)
                bitFieldMessage[i] = new AtomicBoolean(true);
        }else {
            for(int i = 0; i < payloadSize; i++)
                bitFieldMessage[i] = new AtomicBoolean();
        }

    }
    //use setter maybe?
    public AtomicBoolean[] getBitFieldMessage() {
        return bitFieldMessage;
    }

    public void setBitField(Integer index){
        this.isEmpty = false;
        bitFieldMessage[index].set(true);
    }

    public void setBitFieldMessage(AtomicBoolean[] bitField){
        this.isEmpty = false;
        this.bitFieldMessage = bitField;
    }

//    public void setBitFieldRange(Integer startIndex, Integer endIndex){
//        this.isEmpty = false;
//        System.out.println("this is bitfield msg "+ this.bitFieldMessage);
//        for (Integer i = startIndex; i < endIndex; i++){
//            System.out.println(this.bitFieldMessage[0]);
//            this.bitFieldMessage[i].set(true);
//        }
//    }

    public boolean isInteresting(BitField availableBitField){
        boolean interested = false;
        for(int i=0 ; i < length ; i++){
            if(bitFieldMessage[i].get() && !availableBitField.bitFieldMessage[i].get()) {
                interested = true;
                break;
            }
        }
        return interested;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
