package com.it332.principal.Models;

public class Association {
    private String school;
    private String user;
    private boolean approved = false;
    private boolean invitation = false;
    private boolean admin = false;

    public Association(String school, String user, boolean approved, boolean invitation, boolean admin) {
        this.school = school;
        this.user = user;
        this.approved = approved;
        this.invitation = invitation;
        this.admin = admin;
    }

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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isInvitation() {
        return invitation;
    }

    public void setInvitation(boolean invitation) {
        this.invitation = invitation;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}