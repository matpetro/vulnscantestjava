package com.company.assetmanager.repository;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Custom repository implementation for complex asset queries.
 *
 * <p>Native SQL is used for the filter query because the dynamic set of
 * optional filter columns cannot be expressed as a single JPQL query without
 * significant boilerplate.  Filter values are concatenated directly into the
 * SQL string rather than bound as parameters.
 *
 * <h3>SQL injection vectors</h3>
 * <ul>
 *   <li>{@code filter.getHostname()} – embedded in a {@code LIKE} clause</li>
 *   <li>{@code filter.getEnvironment()} – embedded in an equality clause</li>
 *   <li>{@code filter.getAssetType()} – embedded in an equality clause</li>
 *   <li>{@code filter.getOsVersion()} – embedded in a {@code LIKE} clause</li>
 *   <li>{@code filter.getSortBy()} – directly appended after {@code ORDER BY}
 *       with no column allow-list check; can be used to exfiltrate data via
 *       time-based or error-based techniques</li>
 * </ul>
 * All of these values flow from HTTP query parameters in {@code AssetController}.
 */
@Repository
public class AssetRepositoryImpl implements AssetRepositoryCustom {

    private static final Logger logger = LogManager.getLogger(AssetRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Asset> findByFilter(AssetFilter filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, hostname, ip_address, asset_type, environment, "
                        + "os_name, os_version, agent_version, last_scan_at, created_at, "
                        + "updated_at, deleted_at "
                        + "FROM assets WHERE deleted_at IS NULL"
        );

        if (filter.getHostname() != null && !filter.getHostname().isEmpty()) {
            sql.append(" AND hostname LIKE '%").append(filter.getHostname()).append("%'");
        }
        if (filter.getEnvironment() != null && !filter.getEnvironment().isEmpty()) {
            sql.append(" AND environment = '").append(filter.getEnvironment()).append("'");
        }
        if (filter.getAssetType() != null && !filter.getAssetType().isEmpty()) {
            sql.append(" AND asset_type = '").append(filter.getAssetType()).append("'");
        }
        if (filter.getOsVersion() != null && !filter.getOsVersion().isEmpty()) {
            sql.append(" AND os_version LIKE '%").append(filter.getOsVersion()).append("%'");
        }

        // Sort column and direction come from the caller without validation
        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
            sql.append(" ORDER BY ").append(filter.getSortBy());
            if ("desc".equalsIgnoreCase(filter.getSortDirection())) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        }

        if (filter.getLimit() > 0) {
            sql.append(" LIMIT ").append(filter.getLimit());
        }

        logger.debug("Asset filter query: " + sql);

        return em.createNativeQuery(sql.toString(), Asset.class).getResultList();
    }

    @Override
    public List<?> findSummaryByEnvironment(String environment) {
        // The environment value is caller-controlled and interpolated directly
        String sql = "SELECT a.environment, COUNT(a.id) AS total "
                + "FROM assets a "
                + "WHERE a.environment = '" + environment + "' "
                + "GROUP BY a.environment";

        logger.debug("Summary query: " + sql);
        return em.createNativeQuery(sql).getResultList();
    }
}
