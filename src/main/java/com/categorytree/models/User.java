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
    private Long ChatId;

    private String Username;
    private Boolean IsAdmin;

    public Long geChatId() {
        return ChatId;
    }

    public void seChatId(Long Id) {
        this.ChatId = Id;
    }

    public String getUsername() {
        return Username;
    }

     public void setUsername(String username) {
        this.Username = username;
    }

    public boolean getIsAdmin() {
        return IsAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.IsAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "ChatId=" + ChatId +
                ", Username='" + Username + '\'' +
                ", IsAdmin='" + IsAdmin + '\'' +
                '}';
    }
}
