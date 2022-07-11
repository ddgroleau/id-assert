package com.server.authorization.application.domain.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="redirect_uris")
public class RedirectUri {

    //region Properties
    @Id
    @Column(name="redirect_uri_id")
    private UUID redirectUriId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="identity_client_id", nullable=false)
    private IdentityClient identityClient;

    @NotNull
    @Column(name="uri",unique = true)
    private String uri;

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

    public RedirectUri() {
    }

    private RedirectUri(String uri, IdentityClient identityClient) {
        setUri(uri);
        setRedirectUriId(UUID.randomUUID());
        setCreatedBy("root");
        setCreatedOn(LocalDateTime.now());
        setUpdatedBy("root");
        setUpdatedOn(LocalDateTime.now());
        setIdentityClient(identityClient);
    }

    public static RedirectUri createRedirectUri(String uri, IdentityClient identityClient) {
        return new RedirectUri(uri, identityClient);
    }

    //region Getters/Setters
    public String getUri() {
        return uri;
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    public UUID getRedirectUriId() {
        return redirectUriId;
    }

    public void setRedirectUriId(UUID redirectUriId) {
        this.redirectUriId = redirectUriId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    private void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    private String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    public IdentityClient getIdentityClient() {
        return identityClient;
    }

    public void setIdentityClient(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }
    //endregion
}
