package com.company.assetmanager.service;

import com.company.assetmanager.repository.AssetRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds summary and per-asset vulnerability reports.
 *
 * <p>The environment parameter originates from an HTTP query string and is
 * interpolated directly into a native SQL query inside
 * {@link AssetRepository} – see {@code AssetRepositoryImpl.findSummaryByEnvironment()}
 * for the injection vector.
 */
@Service
public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AssetRepository assetRepository;

    /**
     * Build a vulnerability count summary for the given environment.
     *
     * <p>The {@code environment} string is passed to a native query without
     * parameterisation.  Callers who control this value can perform SQL injection.
     */
    public Map<String, Object> buildEnvironmentSummary(String environment) {
        logger.info("Building summary for environment=" + environment);

        // Native query with the environment value interpolated directly
        String sql = "SELECT a.environment, "
                + "COUNT(a.id) AS total_assets, "
                + "SUM(CASE WHEN a.os_version IS NOT NULL THEN 1 ELSE 0 END) AS with_os "
                + "FROM assets a "
                + "WHERE a.environment = '" + environment + "' "
                + "GROUP BY a.environment";

        List<?> rows = entityManager.createNativeQuery(sql).getResultList();

        Map<String, Object> result = new HashMap<>();
        result.put("environment", environment);
        result.put("rows", rows);
        return result;
    }

    public Map<String, Object> buildAssetReport(Long assetId) {
        Map<String, Object> report = new HashMap<>();
        assetRepository.findById(assetId).ifPresent(asset -> {
            report.put("hostname", asset.getHostname());
            report.put("environment", asset.getEnvironment());
            report.put("assetType", asset.getAssetType());
            report.put("osVersion", asset.getOsVersion());
        });
        return report;
    }
}
