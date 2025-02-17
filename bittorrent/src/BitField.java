import java.io.Serializable;
import java.util.*;
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

    public boolean isNotInteresting(BitField peerBitField) {
        return !isInteresting(peerBitField);
    }

    public Integer getInterestingFieldIndex(BitField peerBitField){
        List<Integer> interestingPieces = new ArrayList<>();
        for(int i = 0; i < bitField.length ; i++){
            if(bitField[i].get() && !peerBitField.bitField[i].get()) {
                interestingPieces.add(i);
            }
        }
        if (interestingPieces.isEmpty()) {
            return  -1;
        } else {
            Collections.shuffle(interestingPieces);
            return interestingPieces.get(0);
        }
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }

    public Integer haveBitsCount() {
        return haveBitsCount;
    }

    public boolean allBitSet() {
        return Arrays.stream(bitField).map(AtomicBoolean::get).reduce(true, (x, y) -> x && y);
    }
}
