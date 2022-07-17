package com.server.authorization.application.viewmodel;

public class AccountViewModel {
    private String firstName;
    private String lastName;
    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
    private String phone;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public String getPhone() {
        return phone;
    }
}
