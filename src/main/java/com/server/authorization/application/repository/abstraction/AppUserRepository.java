package com.server.authorization.application.repository.abstraction;

import com.server.authorization.application.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);
}
