package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String schoolId;
    private String assocId;
    private String details;
    private boolean isAccepted;
    private boolean isRejected;
    private Double balance;
    private Double budget;
    private Boolean hasButtons;

    public Notification() {
    }

    public Notification(String userId, String assocId, String schoolId, String details, Double balance, Double budget) {
        this.userId = userId;
        this.assocId = assocId;
        this.schoolId = schoolId;
        this.details = details;
        this.balance = balance;
        this.budget = budget;
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

    public String getAssocId() {
        return assocId;
    }

    public void setAssocId(String assocId) {
        this.assocId = assocId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Boolean getHasButtons() {
        return hasButtons;
    }

    public void setHasButtons(Boolean hasButtons) {
        this.hasButtons = hasButtons;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", assocId='" + assocId + '\'' +
                ", details='" + details + '\'' +
                ", isAccepted=" + isAccepted +
                ", isRejected=" + isRejected +
                ", balance=" + balance +
                ", budget=" + budget +
                '}';
    }
}
