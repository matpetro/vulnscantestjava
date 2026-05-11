package com.company.assetmanager.repository;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetFilter;

import java.util.List;

public interface AssetRepositoryCustom {
    List<Asset> findByFilter(AssetFilter filter);
    List<?> findSummaryByEnvironment(String environment);
}
