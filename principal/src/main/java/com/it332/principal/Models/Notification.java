package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String assocId;
    private String details;
    private boolean isRead;
    private boolean isAccepted;
    private boolean isRejected;
    private Double balance;
    private Double budget;

    public Notification() {
    }

    public Notification(String userId, String assocId, String details, boolean isRead, Double balance, Double budget) {
        this.userId = userId;
        this.assocId = assocId;
        this.details = details;
        this.isRead = isRead;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
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

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", assocId='" + assocId + '\'' +
                ", details='" + details + '\'' +
                ", isRead=" + isRead +
                ", isAccepted=" + isAccepted +
                ", isRejected=" + isRejected +
                ", balance=" + balance +
                ", budget=" + budget +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (isRead != that.isRead) return false;
        if (isAccepted != that.isAccepted) return false;
        if (isRejected != that.isRejected) return false;
        if (!id.equals(that.id)) return false;
        if (!userId.equals(that.userId)) return false;
        if (!assocId.equals(that.assocId)) return false;
        if (!details.equals(that.details)) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        return budget != null ? budget.equals(that.budget) : that.budget == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + assocId.hashCode();
        result = 31 * result + details.hashCode();
        result = 31 * result + (isRead ? 1 : 0);
        result = 31 * result + (isAccepted ? 1 : 0);
        result = 31 * result + (isRejected ? 1 : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (budget != null ? budget.hashCode() : 0);
        return result;
    }
}
