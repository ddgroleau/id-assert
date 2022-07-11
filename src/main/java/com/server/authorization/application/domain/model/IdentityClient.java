package com.server.authorization.application.domain.model;

import com.server.authorization.application.dto.IdentityClientDto;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.server.authorization.web.configuration.AuthorizationServerConfiguration.passwordEncoder;

@Entity
@Table(name="identity_clients")
public class IdentityClient extends RegisteredClient {

    //region Properties
    @Id
    @Column(name="identity_client_id")
    private String id;

    @NotNull
    @Column(name="client_id", unique = true)
    private String clientId;

    @NotNull
    @Column(name="client_id_issued_at")
    private Instant clientIdIssuedAt;

    @NotNull
    @Column(name="client_secret")
    private String clientSecret;

    @NotNull
    @Column(name="client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @NotNull
    @Column(name="client_name")
    private String clientName;

    @Transient
    private Set<ClientAuthenticationMethod> clientAuthenticationMethods;

    @Transient
    private Set<AuthorizationGrantType> authorizationGrantTypes;

    @OneToMany(mappedBy = "identityClient", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RedirectUri> redirectUris;

    @OneToMany(mappedBy = "identityClient", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Scope> scopes;

    @Transient
    private ClientSettings clientSettings;

    @Transient
    private TokenSettings tokenSettings;

    @NotNull
    @Column(name="created_on")
    private LocalDateTime createdOn;

    @Column(name="created_by")
    private String createdBy;

    @NotNull
    @Column(name="updated_on")
    private LocalDateTime updatedOn;

    @Column(name="updated_by")
    private String updatedBy;
    //endregion

    public IdentityClient() {}

    private IdentityClient(String clientId, String clientName, String clientSecret, String baseUri) {
        setClientId(clientId);
        setClientName(clientName);
        setClientSecret(clientSecret);
        setId(UUID.randomUUID().toString());
        setClientIdIssuedAt(Instant.now());
        setClientSecretExpiresAt(null);
        IdentityClient client = this;
        setRedirectUris(new HashSet<>() {{
            add(RedirectUri.createRedirectUri(baseUri + "/login/oauth2/code/id-assert-client-oidc", client));
            add(RedirectUri.createRedirectUri(baseUri + "/authorized", client));
        }});
        setScopes(new HashSet<>(){{add(Scope.createScope(OidcScopes.OPENID, client));}});
        setCreatedBy("root");
        setCreatedOn(LocalDateTime.now());
        setUpdatedBy("root");
        setUpdatedOn(LocalDateTime.now());
    }

    public static IdentityClient createIdentityClient(String clientId, String clientName, String clientSecret, String baseUri) {
        return new IdentityClient(clientId, clientName, clientSecret, baseUri);
    }

    //region Getters/Setters
    @Override
    public String getId() {
        return id.toString();
    }

    private void setId(String id) {
        this.id = id;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Instant getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    private void setClientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    private void setClientSecret(String clientSecret) {
        this.clientSecret = passwordEncoder().encode(clientSecret);
    }

    @Override
    public Instant getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }
    private void setClientSecretExpiresAt(Instant clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }
    @Override
    public String getClientName() {
        return clientName;
    }
    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public Set<ClientAuthenticationMethod> getClientAuthenticationMethods() {
        return new HashSet<>(){{add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);}};
    }

    @Override
    public Set<AuthorizationGrantType> getAuthorizationGrantTypes() {
        return new HashSet<>(){{
            add(AuthorizationGrantType.AUTHORIZATION_CODE);
            add(AuthorizationGrantType.REFRESH_TOKEN);
            add(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }};
    }

    @Override
    public Set<String> getRedirectUris() {
        return redirectUris.stream()
                .map(RedirectUri::getUri)
                .collect(Collectors.toSet());
    }
    public void setRedirectUris(Set<RedirectUri> redirectUris) {
        this.redirectUris = redirectUris;
    }

    @Override
    public Set<String> getScopes() {
        return scopes.stream()
                .map(Scope::getName)
                .collect(Collectors.toSet());
    }
    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }
    @Override
    public ClientSettings getClientSettings() {
        return ClientSettings.builder().build();
    }
    @Override
    public TokenSettings getTokenSettings() {
        return TokenSettings.builder().accessTokenTimeToLive(Duration.ofDays(1)).build();
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
    private void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    private void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    //endregion
}
