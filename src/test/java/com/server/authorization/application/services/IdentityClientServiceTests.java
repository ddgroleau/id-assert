package com.server.authorization.application.services;


import com.server.authorization.application.domain.model.IdentityClient;
import com.server.authorization.application.repository.abstraction.IdentityClientRepository;
import com.server.authorization.application.service.implementation.IdentityClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.security.InvalidParameterException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IdentityClientServiceTests {
    private final IdentityClientRepository identityClientRepository = mock(IdentityClientRepository.class);
    private IdentityClientService identityClientService;

    @BeforeAll
    void setup() {
        identityClientService = new IdentityClientService(identityClientRepository);
    }

    @Test
    void save_withNewClient_SavesClient() {
        RegisteredClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findById(client.getId())).thenReturn(Optional.empty());
        when(identityClientRepository.saveAndFlush(any(IdentityClient.class))).thenReturn((IdentityClient) client);

        identityClientService.save(client);
        verify(identityClientRepository,times(1)).saveAndFlush(any(IdentityClient.class));
    }

    @Test
    void save_withExistingClient_throwsException() {
        RegisteredClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findById(client.getId())).thenReturn(Optional.of((IdentityClient)client));

        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->identityClientService.save(client));
        assertEquals("Client already exists", exception.getMessage());
    }


    @Test
    void save_withNullClient_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->identityClientService.save(null));
        assertEquals("Client is required.", exception.getMessage());
    }

    @Test
    void createIfNotExists_withNewClient_SavesClient() {
        IdentityClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findById(client.getId())).thenReturn(Optional.empty());
        when(identityClientRepository.saveAndFlush(any(IdentityClient.class))).thenReturn(client);

        identityClientService.createIfNotExists(client);
        verify(identityClientRepository,times(2)).saveAndFlush(any(IdentityClient.class));
    }

    @Test
    void createIfNotExists_withExistingClient_returnsSilently() {
        IdentityClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        identityClientService.createIfNotExists(client);
        verify(identityClientRepository,times(1)).findById(client.getId());
    }


    @Test
    void createIfNotExists_withNullClient_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->identityClientService.createIfNotExists(null));
        assertEquals("Client is required.", exception.getMessage());
    }

    @Test
    void findById_withExistingId_returnsClient() {
        IdentityClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        assertEquals(client,identityClientService.findById(client.getId()));
    }

    @Test
    void findById_withNonExistentId_returnsNull() {
        String id = "testId";
        when(identityClientRepository.findById(id)).thenReturn(Optional.empty());

        assertNull(identityClientService.findById(id));
    }


    @Test
    void findById_withNullId_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->identityClientService.findById(null));
        assertEquals("Id is required.", exception.getMessage());
    }

    @Test
    void findByClientId_withExistingId_returnsClient() {
        IdentityClient client = IdentityClient.createIdentityClient(
                "testId",
                "testName",
                "testSecret",
                "http://test");

        when(identityClientRepository.findByClientId(client.getClientId())).thenReturn(Optional.of(client));

        assertEquals(client,identityClientService.findByClientId(client.getClientId()));
    }

    @Test
    void findByClientId_withNonExistentId_returnsNull() {
        String clientId = "testId";
        when(identityClientRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        assertNull(identityClientService.findByClientId(clientId));
    }


    @Test
    void findByClientId_withNullId_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->identityClientService.findByClientId(null));
        assertEquals("ClientId is required.", exception.getMessage());
    }

}
