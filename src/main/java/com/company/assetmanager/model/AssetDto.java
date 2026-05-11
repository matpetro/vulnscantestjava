package com.company.assetmanager.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetDto {
    private Long id;
    private String hostname;
    private String ipAddress;
    private String assetType;
    private String environment;
    private String osName;
    private String osVersion;
    private LocalDateTime createdAt;
}
