package com.group2.model;

import java.io.Serializable;

public class ActualMessage implements Serializable {
    private Integer messageLength;
    private Byte messageType;
    private Object messagePayload;

    private ActualMessage(Byte messageType, Object messagePayload) {
        this.messageType = messageType;
        this.messagePayload = messagePayload;
        this.messageLength = 1 + ObjectSizeFetcher.getObjectSize(messagePayload);
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
