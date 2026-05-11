package com.company.assetmanager.service;

import com.company.assetmanager.model.Asset;
import com.company.assetmanager.model.AssetCreateRequest;
import com.company.assetmanager.model.AssetDto;
import com.company.assetmanager.model.AssetFilter;
import com.company.assetmanager.repository.AssetRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private static final Logger logger = LogManager.getLogger(AssetService.class);

    @Autowired
    private AssetRepository assetRepository;

    public List<AssetDto> searchAssets(AssetFilter filter) {
        List<Asset> assets = assetRepository.findByFilter(filter);
        return assets.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<AssetDto> findById(Long id) {
        return assetRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public AssetDto createAsset(AssetCreateRequest request) {
        Asset asset = new Asset();
        asset.setHostname(request.getHostname());
        asset.setIpAddress(request.getIpAddress());
        asset.setAssetType(request.getAssetType());
        asset.setEnvironment(request.getEnvironment());
        asset.setOsName(request.getOsName());
        asset.setOsVersion(request.getOsVersion());
        asset.setCreatedAt(LocalDateTime.now());
        Asset saved = assetRepository.save(asset);
        logger.info("Asset created with id=" + saved.getId());
        return toDto(saved);
    }

    @Transactional
    public Optional<AssetDto> updateAsset(Long id, AssetCreateRequest request) {
        return assetRepository.findById(id).map(asset -> {
            asset.setHostname(request.getHostname());
            asset.setIpAddress(request.getIpAddress());
            asset.setAssetType(request.getAssetType());
            asset.setEnvironment(request.getEnvironment());
            asset.setOsName(request.getOsName());
            asset.setOsVersion(request.getOsVersion());
            asset.setUpdatedAt(LocalDateTime.now());
            return toDto(assetRepository.save(asset));
        });
    }

    @Transactional
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }

    @Transactional
    public void saveRaw(Asset asset) {
        if (asset.getCreatedAt() == null) {
            asset.setCreatedAt(LocalDateTime.now());
        }
        assetRepository.save(asset);
    }

    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    public List<String> listEnvironments() {
        return assetRepository.findDistinctEnvironments();
    }

    private AssetDto toDto(Asset asset) {
        AssetDto dto = new AssetDto();
        dto.setId(asset.getId());
        dto.setHostname(asset.getHostname());
        dto.setIpAddress(asset.getIpAddress());
        dto.setAssetType(asset.getAssetType());
        dto.setEnvironment(asset.getEnvironment());
        dto.setOsName(asset.getOsName());
        dto.setOsVersion(asset.getOsVersion());
        dto.setCreatedAt(asset.getCreatedAt());
        return dto;
    }
}
