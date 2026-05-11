package com.company.assetmanager.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hostname;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "asset_type")
    private String assetType;

    private String environment;

    @Column(name = "os_name")
    private String osName;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "agent_version")
    private String agentVersion;

    @Column(name = "last_scan_at")
    private LocalDateTime lastScanAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
