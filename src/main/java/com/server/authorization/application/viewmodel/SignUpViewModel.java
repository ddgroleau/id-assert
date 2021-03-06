package com.server.authorization.application.viewmodel;

import com.server.authorization.application.validation.FieldsValueMatch;
import com.server.authorization.application.validation.ValidPassword;

import javax.validation.constraints.NotEmpty;


public class SignUpViewModel {
    @NotEmpty(message = "Email is required.")
    private String email;
    @NotEmpty(message = "First Name is required.")
    private String firstName;

    @NotEmpty(message = "Last Name is required.")
    private String lastName;

    @ValidPassword
    private String password;

    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean passwordAndConfirmPasswordMatch() {
        return confirmPassword.equals(password);
    }

}
