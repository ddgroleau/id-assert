package com.server.authorization.application.repository.abstraction;

import com.server.authorization.application.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String any);

    Set<PasswordResetToken> findAllByAppUser_UserId(String userId);
}
