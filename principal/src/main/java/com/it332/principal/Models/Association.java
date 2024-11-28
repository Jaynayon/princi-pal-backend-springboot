package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Association")
public class Association {
    @Id
    private String id;
    private String userId;
    private String schoolId;
    private boolean approved = false;
    private boolean invitation = false;
    private boolean admin = false;

    public Association() {
    }

    public Association(String id, String userId, String schoolId, boolean approved, boolean invitation, boolean admin) {
        this.id = id;
        this.userId = userId;
        this.schoolId = schoolId;
        this.approved = approved;
        this.invitation = invitation;
        this.admin = admin;
    }

    public Association(String userId, String schoolId, boolean approved, boolean invitation, boolean admin) {
        this.userId = userId;
        this.schoolId = schoolId;
        this.approved = approved;
        this.invitation = invitation;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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