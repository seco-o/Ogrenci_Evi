package com.hk.ogrencievi.Model;

import java.util.Date;

public class MessageAndTime {
    private final String message;
    private final Date time;

    public MessageAndTime(String message, Date time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public Date getTime() {
        return time;
    }
}
