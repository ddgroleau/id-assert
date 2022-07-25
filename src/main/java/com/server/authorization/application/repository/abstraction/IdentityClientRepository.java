package com.server.authorization.application.repository.abstraction;

import com.server.authorization.application.domain.model.IdentityClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityClientRepository extends JpaRepository<IdentityClient, String> {
    Optional<IdentityClient> findByClientId(String clientId);
}
