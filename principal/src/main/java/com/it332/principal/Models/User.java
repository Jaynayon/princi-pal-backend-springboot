package com.it332.principal.Models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.it332.principal.DTO.UserAdminRequest;

@Document(collection = "Users")
public class User {
    @Id
    private String id;

    private String fname;
    private String mname;
    private String lname;
    private String username;
    private String email;
    private String password;
    private String position;
    private String avatar = "Blue"; // Default value

    private String token; // Token for password reset
    private LocalDateTime tokenCreationDate; // Date when the token was created

    public User() {
    }

    public User(UserAdminRequest user) {
        this.fname = user.getFname();
        this.mname = user.getMname();
        this.lname = user.getLname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.position = user.getPosition();
    }

    // Constructor with essential fields
    public User(String fname, String mname, String lname, String username, String email, String password,
                String position) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.position = position;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenCreationDate() {
        return tokenCreationDate;
    }

    public void setTokenCreationDate(LocalDateTime tokenCreationDate) {
        this.tokenCreationDate = tokenCreationDate;
    }
}
