package com.identity.api.application.domain.model;

import com.sun.istack.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.UUID.randomUUID;

@Entity
@Table(name="users")
public class AppUser implements OAuth2User, UserDetails {
    @Id
    @Column(name="user_id")
    private UUID userId;

    @NotNull
    @Column(name="username",unique = true)
    private String username;

    @Column(name="phone")
    private String phone;

    @NotNull
    @Column(name="email",unique = true)
    private String email;
    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @NotNull
    @Column(name="password_hash")
    private String passwordHash;

    @Column(name="date_of_birth")
    private LocalDateTime dateOfBirth;

    @NotNull
    @Column(name="is_active")
    private boolean isActive;
    @NotNull
    @Column(name="email_confirmed")
    private boolean emailConfirmed;
    @NotNull
    @Column(name="phone_confirmed")
    private boolean phoneConfirmed;
    @NotNull
    @Column(name="multi_factor_enabled")
    private boolean multiFactorEnabled;
    @NotNull
    @Column(name="is_subscriber")
    private boolean isSubscriber;

    @Column(name="last_login")
    private LocalDateTime lastLogin;
    @Column(name="last_login_location")
    private String lastLoginLocation;
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

    @OneToMany
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn( name="user_id"),
            inverseJoinColumns = @JoinColumn( name="role_id")
    )
    private Set<Role> roles;

    public AppUser() {
        setUserId(randomUUID());
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public boolean isPhoneConfirmed() {
        return phoneConfirmed;
    }

    public void setPhoneConfirmed(boolean phoneConfirmed) {
        this.phoneConfirmed = phoneConfirmed;
    }

    public boolean isMultiFactorEnabled() {
        return multiFactorEnabled;
    }

    public void setMultiFactorEnabled(boolean multiFactorEnabled) {
        this.multiFactorEnabled = multiFactorEnabled;
    }

    public boolean isSubscriber() {
        return isSubscriber;
    }

    public void setSubscriber(boolean subscriber) {
        isSubscriber = subscriber;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLoginLocation() {
        return lastLoginLocation;
    }

    public void setLastLoginLocation(String lastLoginLocation) {
        this.lastLoginLocation = lastLoginLocation;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
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

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles()
                .stream()
                .map(role->new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
