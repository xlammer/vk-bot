package eu.babkin.vk.bot.event.updates;

import com.google.common.base.MoreObjects;

public class MessageUpdate extends Update {

    private long timestamp;

    private long messageId;

    private String message;

    private String chatName;

    private int chatId;


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


    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getChatId() {
        return chatId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("messageId", messageId)
                .add("message", message)
                .add("chatName", chatName)
                .add("chatId", chatId)
                .toString();
    }
}
