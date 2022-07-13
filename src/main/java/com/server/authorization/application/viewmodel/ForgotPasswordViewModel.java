package com.server.authorization.application.viewmodel;

import javax.validation.constraints.NotEmpty;

public class ForgotPasswordViewModel {
    private String email;

    @NotEmpty(message = "Email is required.")
    public String getEmail() {
        return email;
    }

    public ForgotPasswordViewModel(String email) {
        this.email = email;
    }
}
