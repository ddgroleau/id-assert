package com.server.authorization.application.viewmodel;

import com.server.authorization.application.dto.EventResponseDto;

import javax.validation.constraints.NotEmpty;

public class ResetPasswordViewModel {
    @NotEmpty(message = "Password is required.")
    private String newPassword;

    @NotEmpty(message = "Confirm Password is required.")
    private String confirmNewPassword;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
