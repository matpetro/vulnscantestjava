package com.company.assetmanager.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for bulk XML import/export of {@link Asset} records.
 */
@Data
@NoArgsConstructor
public class AssetImportList {
    private List<Asset> assets = new ArrayList<>();
}
