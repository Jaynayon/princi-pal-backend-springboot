package com.it332.principal.DTO;

public class ResetPasswordRequest {
    private String newPassword;

    // Constructor (optional)
    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
