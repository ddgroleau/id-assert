package com.server.authorization.application.service.implementation;

import com.server.authorization.application.domain.model.IdentityClient;
import com.server.authorization.application.repository.abstraction.IdentityClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service("registeredClientRepository")
public class IdentityClientService implements RegisteredClientRepository {

    private final IdentityClientRepository identityClientRepository;

    public IdentityClientService(IdentityClientRepository identityClientRepository) {
        this.identityClientRepository = identityClientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Optional<IdentityClient> client = identityClientRepository.findById(registeredClient.getId());
        if(client.isPresent()) throw new InvalidParameterException("Client already exists");
        identityClientRepository.saveAndFlush((IdentityClient) registeredClient);
        return;
    }

    public void createIfNotExists(IdentityClient identityClient) {
        Optional<IdentityClient> client = identityClientRepository.findById(identityClient.getId());
        if(client.isPresent()) return;
        identityClientRepository.saveAndFlush(identityClient);
        return;
    }

    @Override
    public RegisteredClient findById(String id) {
        Optional<IdentityClient> client = identityClientRepository.findById(id);
        if(client.isEmpty()) return null;
        return client.get();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Optional<IdentityClient> client = identityClientRepository.findByClientId(clientId);
        if(client.isEmpty()) return null;
        return client.get();
    }
}
