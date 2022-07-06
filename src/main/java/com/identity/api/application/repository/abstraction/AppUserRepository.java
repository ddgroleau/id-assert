package com.identity.api.application.repository.abstraction;

import com.identity.api.application.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    AppUser findByUsername(String username);
}
