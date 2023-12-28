package com.categorytree.models;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name="usersDataTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Boolean isAdmin;
    private Long telegramChatId;

    public Long getId() {
        return id;
    }

    public void getId(Long Id) {
        this.id = Id;
    }

    public String getUsername() {
        return username;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long Id) {
        this.telegramChatId = Id;
    }



     public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "ChatId=" + id +
                ", Username='" + username + '\'' +
                ", IsAdmin='" + isAdmin + '\'' +
                ", TelegramChatId='" + telegramChatId + '\'' +
                '}';
    }
}
