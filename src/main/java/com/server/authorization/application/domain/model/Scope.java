package com.server.authorization.application.domain.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="scopes")
public class Scope {

    //region Properties
    @Id
    @Column(name="scope_id")
    private UUID scopeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="identity_client_id", nullable=false)
    private IdentityClient identityClient;

    @NotNull
    @Column(name="name",unique = true)
    private String name;

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

    public Scope() {

    }

    private Scope(String name, IdentityClient identityClient) {
        setName(name);
        setScopeId(UUID.randomUUID());
        setCreatedBy("root");
        setCreatedOn(LocalDateTime.now());
        setUpdatedBy("root");
        setUpdatedOn(LocalDateTime.now());
        setIdentityClient(identityClient);
    }

    public static Scope createScope(String name, IdentityClient identityClient) {
        return new Scope(name, identityClient);
    }

    //region Getters/Setters
    public UUID getScopeId() {
        return scopeId;
    }

    private void setScopeId(UUID scopeId) {
        this.scopeId = scopeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public IdentityClient getIdentityClient() {
        return identityClient;
    }

    public void setIdentityClient(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }
    //endregion
}
