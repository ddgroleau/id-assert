package com.identity.api.application.domain.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="domains")
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="domain_id")
    private long domainId;

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
}
