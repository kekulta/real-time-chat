package chat;

import java.time.LocalDateTime;

public class Message {
    private String receiver;
    private String message;
    private String sender;
    private LocalDateTime date;

    public Message() {

    }

    public Message(String m, String sender) {
        this.message = m;
        this.sender = sender;
        this.date = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
