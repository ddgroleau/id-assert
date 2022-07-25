package com.server.authorization.application.viewmodel;

import javax.validation.constraints.NotEmpty;

public class ForgotPasswordViewModel {
    @NotEmpty(message = "Email is required.")
    private String email;

    public String getEmail() {
        return email;
    }

    public ForgotPasswordViewModel(String email) {
        this.email = email;
    }
}
