package com.company.assetmanager.model;

import lombok.Data;

/**
 * Form / multipart binding target for asset creation and update requests.
 *
 * <p>Using {@code @ModelAttribute} binding with this class (see
 * {@code AssetController.createAsset()}) contributes to the Spring4Shell
 * (CVE-2022-22965) attack surface on JDK 9+ + Tomcat deployments running
 * Spring Framework &lt; 5.3.18.  The exploit traverses nested property paths
 * via the data binder to overwrite Tomcat class-loader state.
 */
@Data
public class AssetCreateRequest {
    private String hostname;
    private String ipAddress;
    private String assetType;
    private String environment;
    private String osName;
    private String osVersion;
    private String agentVersion;
}
