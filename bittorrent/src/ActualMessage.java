import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActualMessage implements Serializable {
    private Integer messageLength;
    private Byte messageType;
    private Object messagePayload;

    private ActualMessage(Byte messageType, Object messagePayload) {
        this.messageType = messageType;
        this.messagePayload = messagePayload;
        this.messageLength = 1 + getMessagePayloadLength(messageType, messagePayload);
    }

    private static Integer getMessagePayloadLength(Byte messageType, Object messagePayload) {
        switch (messageType) {
            case 0: // CHOKE
            case 1: // UNCHOKE
            case 2: // INTERESTED
            case 3: // NOT_INTERESTED
                return 0;
            case 6: // REQUEST
            case 4: // HAVE
                return Integer.BYTES;
            case 5: // BITFIELD
                return ((BitField) messagePayload).getBitField().length;
            case 7: // PIECE
                return ((byte[]) messagePayload).length;
            default:
                return 0;
        }
    }

    public Integer getMessageLength() {
        return messageLength;
    }

    public Byte getMessageType() {
        return messageType;
    }

    public Object getMessagePayload() {
        return messagePayload;
    }

    public static final class ActualMessageBuilder {
        private Integer messageLength;
        private Byte messageType;
        private Object messagePayload;

        private ActualMessageBuilder() {
        }

        public static ActualMessageBuilder builder() {
            return new ActualMessageBuilder();
        }

        public ActualMessageBuilder withMessageLength(Integer messageLength) {
            this.messageLength = messageLength;
            return this;
        }

        public ActualMessageBuilder withMessageType(Byte messageType) {
            this.messageType = messageType;
            return this;
        }

        public ActualMessageBuilder withMessagePayload(Object messagePayload) {
            this.messagePayload = messagePayload;
            return this;
        }

        public ActualMessage build() {
            ActualMessage actualMessage = new ActualMessage(messageType, messagePayload);
            actualMessage.messageLength = this.messageLength;
            return actualMessage;
        }
    }
}
