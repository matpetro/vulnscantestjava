package com.company.assetmanager.model;

import lombok.Data;

/**
 * Filter parameters for asset search queries.
 *
 * <p>All string fields are passed directly into native SQL via
 * {@code AssetRepositoryImpl.findByFilter()} without parameterisation.
 * An attacker who controls these fields can perform SQL injection.
 */
@Data
public class AssetFilter {
    private String hostname;
    private String environment;
    private String assetType;
    private String osVersion;
    /** Column name to sort by – injected directly into ORDER BY clause. */
    private String sortBy;
    /** Sort direction – must be "asc" or "desc"; not validated before use. */
    private String sortDirection;
    private int limit = 50;
}
