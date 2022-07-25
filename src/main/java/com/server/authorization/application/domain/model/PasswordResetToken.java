package com.server.authorization.application.domain.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="password_reset_tokens")
public class PasswordResetToken {
    @Id
    @Column(name="password_reset_token_id")
    private String passwordResetTokenId;

    @NotNull
    @Column(name="token")
    private String token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser appUser;

    @NotNull
    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

    public PasswordResetToken() {}

    private PasswordResetToken(String token, AppUser appUser) {
        this.passwordResetTokenId = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusDays(1);
        this.token = token;
        this.appUser = appUser;
    }

    public static PasswordResetToken generateToken(String token, AppUser appUser) {
        return new PasswordResetToken(token,appUser);
    }

    public String getToken() {
        return token;
    }

    public String getPasswordResetTokenId() {
        return passwordResetTokenId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}