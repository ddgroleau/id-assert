package com.server.authorization.application.viewmodel;

import com.server.authorization.application.validation.FieldsValueMatch;
import com.server.authorization.application.validation.ValidPassword;

@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "newPassword",
                fieldMatch = "confirmNewPassword",
                message = "Passwords do not match."
        )
})
public class ResetPasswordViewModel {
    @ValidPassword
    private String newPassword;

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
