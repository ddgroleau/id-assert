package com.server.authorization.application.viewmodel;

import com.server.authorization.application.validation.FieldsValueMatch;
import com.server.authorization.application.validation.ValidPassword;

import javax.validation.constraints.NotEmpty;

@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "newPassword",
                fieldMatch = "confirmNewPassword",
                message = "Passwords do not match."
        )
})
public class ChangePasswordViewModel {
    private String userId;

    @NotEmpty(message = "Current Password is required.")
    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
