package eu.babkin.vk.bot.messages;

import com.google.common.base.MoreObjects;
import eu.babkin.vk.bot.event.updates.Update;

public class IncomingMessage extends Update {

    private long timestamp;

    private long messageId;

    private String message;

    private String chatName;

    private int peerId;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public int getPeerId() {
        return peerId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("messageId", messageId)
                .add("message", message)
                .add("chatName", chatName)
                .add("peerId", peerId)
                .toString();
    }
}
