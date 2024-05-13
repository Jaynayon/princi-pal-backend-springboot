package com.it332.principal.Models;



public class Association {
    @Id
    private String school;
    private String user;
    private String schoolId;
    private String userId;
    private boolean approved = false;
    private boolean invitation = false;
    private boolean admin = false;

    public Association() {
        /*this.id = id;
        this.sid = sid;*/
        this.school = school;
        this.user = user;
        this.schoolId = schoolId;
        this.userId = userId;
        this.approved = approved;
        this.invitation = invitation;
        this.admin = admin;
    }
    /*public String getId () {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSid () {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }*/
    public String getSchool() {
        return school;
    }
    public void setSchool(String string) {
        this.school = string;
    }
    public String getUser() {
        return user;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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