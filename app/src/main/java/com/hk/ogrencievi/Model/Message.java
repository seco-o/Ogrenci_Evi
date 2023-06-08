package com.hk.ogrencievi.Model;

import java.util.Date;

public class Message {
    private final String id;
    private final String text;
    private final String senderEmail;
    private final Date time;


    public Message(String id, String text, String senderEmail, Date time) {
        this.id = id;
        this.text = text;
        this.senderEmail = senderEmail;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public Date getTime() {
        return time;
    }
}
