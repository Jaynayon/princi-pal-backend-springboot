package com.it332.principal.DTO;

import com.it332.principal.Models.User;

public class AssociationRequest {
    private String school;
    private String user;

    // Getters and setters
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
