package com.urbanpulse.infrastructure.controller;

import com.urbanpulse.infrastructure.model.Asset;
import com.urbanpulse.infrastructure.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for infrastructure asset management.
 * Provides CRUD operations, geospatial queries, and dashboard statistics.
 */
@RestController
@RequestMapping("/api/v1/infrastructure/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Infrastructure asset management APIs")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @Operation(summary = "Get all assets with pagination")
    public ResponseEntity<Page<Asset>> getAllAssets(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assetService.getAllAssets(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID")
    public ResponseEntity<Asset> getAssetById(@PathVariable UUID id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping
    @Operation(summary = "Create new asset")
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.createAsset(asset));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update asset")
    public ResponseEntity<Asset> updateAsset(@PathVariable UUID id, @RequestBody Asset asset) {
        return ResponseEntity.ok(assetService.updateAsset(id, asset));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Decommission asset")
    public ResponseEntity<Map<String, String>> deleteAsset(@PathVariable UUID id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(Map.of("message", "Asset decommissioned"));
    }

    @GetMapping("/{id}/sensors")
    @Operation(summary = "Get sensors for an asset")
    public ResponseEntity<List<Asset>> getAssetSensors(@PathVariable UUID id) {
        return ResponseEntity.ok(assetService.getAssetSensors(id).stream()
                .map(s -> {
                    Asset a = new Asset();
                    a.setId(id);
                    return a;
                }).toList());
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find assets near a location")
    public ResponseEntity<List<Asset>> getNearbyAssets(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") double radiusMeters) {
        return ResponseEntity.ok(assetService.getNearbyAssets(lat, lon, radiusMeters));
    }

    @GetMapping("/stats/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(assetService.getDashboardStats());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get assets by category")
    public ResponseEntity<Page<Asset>> getAssetsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(assetService.getAllAssets(pageable));
    }
}
