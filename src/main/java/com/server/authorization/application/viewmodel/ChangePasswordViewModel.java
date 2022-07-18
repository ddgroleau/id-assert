package com.server.authorization.application.viewmodel;

import javax.validation.constraints.NotEmpty;

public class ChangePasswordViewModel {
    private String userId;

    @NotEmpty(message = "Current Password is required.")
    private String currentPassword;

    @NotEmpty(message = "Password is required.")
    private String newPassword;

    @NotEmpty(message = "Confirm Password is required.")
    private String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public String getUserId() {
        return userId;
    }
}
