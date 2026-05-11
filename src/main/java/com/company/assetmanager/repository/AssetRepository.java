package com.company.assetmanager.repository;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, AssetRepositoryCustom {

    @Query("SELECT DISTINCT a.environment FROM Asset a WHERE a.environment IS NOT NULL ORDER BY a.environment")
    List<String> findDistinctEnvironments();
}
