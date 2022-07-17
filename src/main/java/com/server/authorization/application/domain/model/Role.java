package com.server.authorization.application.domain.model;

import com.sun.istack.NotNull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="roles")
public class Role {

    //region Properties
    @Id
    @Column(name="role_id")
    private String roleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false)
    private AppUser appUser;

    @NotNull
    @Column(name="name")
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

    public Role(){}

    private Role(String name, AppUser appUser) {
        setName(name);
        setRoleId(UUID.randomUUID().toString());
        setCreatedBy("root");
        setCreatedOn(LocalDateTime.now());
        setUpdatedBy("root");
        setUpdatedOn(LocalDateTime.now());
        setAppUser(appUser);
    }

    public static Role createRole(String name, AppUser appUser) {
        return new Role(name, appUser);
    }

    //region Getters/Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleId() {
        return roleId;
    }

    private void setRoleId(String roleId) {
        this.roleId = roleId;
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

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    //endregion
}
