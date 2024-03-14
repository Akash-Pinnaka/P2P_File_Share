import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The ActualMessageManager class is responsible for managing the creation and reading of actual messages
 * in a peer-to-peer network.
 */
public class ActualMessageManager {

    /**
     * Enumeration representing different types of messages.
     */
    public enum MessageType {
        CHOKE((byte) 0),
        UNCHOKE((byte) 1),
        INTERESTED((byte) 2),
        NOT_INTERESTED((byte) 3),
        HAVE((byte) 4),
        BITFIELD((byte) 5),
        REQUEST((byte) 6),
        PIECE((byte) 7);

        private final byte value;

        MessageType(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    int messageLength;
    MessageType messageType;
    byte[] messagePayload;

    /**
     * Generates an actual message byte array from the message length, type, and payload.
     *
     * @return The byte array representing the actual message.
     */
    public byte[] generateActualMessage() {
        /*
         * message length = 1 byte message type + length of message payload
         */
        messageLength = 1 + messagePayload.length;

        int bufferLength = messageLength + 4;

        ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
        buffer.putInt(messageLength);
        buffer.put(messageType.getValue());
        buffer.put(messagePayload);

        return buffer.array();
    }

    /**
     * Reads an actual message from a ByteBuffer and returns a string representation of the message.
     *
     * @param buffer The ByteBuffer containing the actual message.
     * @return A string representation of the actual message.
     */
    public String readActualMessage(ByteBuffer buffer) {

        // Read message length
        int length = buffer.getInt();

        // Read message type
        byte messageType = buffer.get();

        // Read payload
        byte[] payload = new byte[length - 5];
        buffer.get(payload);

        StringBuilder result = new StringBuilder();

        result.append(messageType)
                .append(new String(payload));

        return result.toString();

        // // Print message information (for testing)
        // System.out.println("::Actual Message::");
        // System.out.println("Type: " + messageType);
        // System.out.println("Payload: " + new String(payload));
    }

    /**
     * Gets the length of the current actual message.
     *
     * @return The length of the current actual message.
     */
    public int getMessageLength() {
        return messageLength;
    }

    /**
     * Sets the length of the current actual message.
     *
     * @param messageLength The length to set for the current actual message.
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    /**
     * Gets the type of the current actual message.
     *
     * @return The type of the current actual message.
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Sets the type of the current actual message.
     *
     * @param messageType The type to set for the current actual message.
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Gets the payload of the current actual message.
     *
     * @return The payload of the current actual message.
     */
    public byte[] getMessagePayload() {
        return messagePayload;
    }

    /**
     * Sets the payload of the current actual message.
     *
     * @param messagePayload The payload to set for the current actual message.
     */
    public void setMessagePayload(byte[] messagePayload) {
        this.messagePayload = messagePayload;
    }
}