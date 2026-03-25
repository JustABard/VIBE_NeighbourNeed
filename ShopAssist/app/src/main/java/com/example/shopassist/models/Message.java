package com.example.shopassist.models;

import java.io.Serializable;

public class Message implements Serializable {

    private String requestId;
    private String senderId;
    private String receiverId;
    private String text;
    private String time;

    public Message(String requestId, String senderId, String receiverId, String text, String time) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.time = time;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
