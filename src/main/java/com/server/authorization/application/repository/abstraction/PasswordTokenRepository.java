package com.server.authorization.application.repository.abstraction;

import com.server.authorization.application.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, String> {
}
