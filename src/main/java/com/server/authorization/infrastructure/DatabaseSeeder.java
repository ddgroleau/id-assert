package com.server.authorization.infrastructure;

import com.server.authorization.application.domain.model.IdentityClient;
import com.server.authorization.application.repository.abstraction.IdentityClientRepository;
import com.server.authorization.application.service.implementation.IdentityClientService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    @Value("${default.client-id}")
    private String defaultClientId;

    @Value("${default.client-name}")
    private String defaultClientName;

    @Value("${default.client-secret}")
    private String defaultClientSecret;

    @Value("${identity.base.uri}")
    private String identityBaseUri;

    @Value("${client.base.uri}")
    private String clientBaseUrl;

    private final IdentityClientService identityClientService;

    public DatabaseSeeder(@Qualifier("registeredClientRepository") IdentityClientService identityClientService) {
        this.identityClientService = identityClientService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedDefaultIdentityClients();
    }

    private void seedDefaultIdentityClients() {
        identityClientService.createIfNotExists(
                IdentityClient.createIdentityClient(
                        defaultClientId,
                        defaultClientName,
                        defaultClientSecret,
                        clientBaseUrl
                )
        );
        return;
    }
}
