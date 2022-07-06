package com.identity.api.application.domain.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resource_id")
    private long resourceId;

    @NotNull
    @Column(name="name",unique = true)
    private String name;

    @NotNull
    @Column(name="display_name",unique = true)
    private String displayName;

    @NotNull
    @Column(name="theme_dark")
    private String themeDark;

    @NotNull
    @Column(name="theme_light")
    private String themeLight;

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
            name="resource_domains",
            joinColumns = @JoinColumn( name="resource_id"),
            inverseJoinColumns = @JoinColumn( name="domain_id")
    )
    private Set<Domain> domains;
}
