package com.it332.principal.DTO;

import java.util.List;

import com.it332.principal.Models.School;
import com.it332.principal.Models.User;

public class UserResponse {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String username;
    private String email;
    private String password;
    private String position;
    private String avatar = "Blue"; // default value
    private List<School> schools;

    public UserResponse() {
    }

    public UserResponse(String id, String fname, String mname, String lname, String username, String email,
            String password, String position, String avatar) {
        this.id = id;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.position = position;
        this.avatar = avatar;
    }

    public UserResponse(User user, List<School> schools) {
        this.id = user.getId();
        this.fname = user.getFname();
        this.mname = user.getMname();
        this.lname = user.getLname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.position = user.getPosition();
        this.avatar = user.getAvatar();
        this.schools = schools;
    }

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

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

}