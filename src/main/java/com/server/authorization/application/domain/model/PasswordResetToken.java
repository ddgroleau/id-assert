package com.server.authorization.application.domain.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class PasswordResetToken {
    @Id
    @Column(name="password_reset_token_id")
    private UUID passwordResetTokenId;

    @NotNull
    @Column(name="token")
    private UUID token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser appUser;

    @NotNull
    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

    public PasswordResetToken() {}

    private PasswordResetToken(String token, AppUser appUser) {
        this.passwordResetTokenId = UUID.randomUUID();
        this.expiryDate = LocalDateTime.now().plusDays(1);
        this.token = UUID.randomUUID();
        this.appUser = appUser;
    }

    public static PasswordResetToken generateToken(String token, AppUser appUser) {
        return new PasswordResetToken(token,appUser);
    }

}