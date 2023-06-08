package com.hk.ogrencievi.Model;

import java.util.Date;

public class MesajlarItem {
    private final String userId;
    private final String userName;
    private final String userImage;
    private final String lastMessage;
    private final Date time;
    private final String email;

    public MesajlarItem(String userId, String userName, String userImage, String lastMessage, Date time, String email) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.lastMessage = lastMessage;
        this.time = time;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Date getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
