package com.urbanpulse.infrastructure.service;

import com.urbanpulse.infrastructure.model.Asset;
import com.urbanpulse.infrastructure.model.Sensor;
import com.urbanpulse.infrastructure.repository.AssetRepository;
import com.urbanpulse.infrastructure.repository.SensorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final SensorRepository sensorRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "asset:";
    private static final String HEALTH_PREFIX = "asset:health:";

    @Transactional(readOnly = true)
    public Page<Asset> getAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Asset getAssetById(UUID id) {
        // Try cache first
        String cacheKey = CACHE_PREFIX + id;
        Asset cached = (Asset) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));

        // Cache for 5 minutes
        redisTemplate.opsForValue().set(cacheKey, asset, 5, TimeUnit.MINUTES);
        return asset;
    }

    @Transactional
    public Asset createAsset(Asset asset) {
        if (assetRepository.findByAssetCode(asset.getAssetCode()).isPresent()) {
            throw new IllegalArgumentException("Asset code already exists: " + asset.getAssetCode());
        }
        Asset saved = assetRepository.save(asset);
        publishEvent("ASSET_CREATED", saved);
        return saved;
    }

    @Transactional
    public Asset updateAsset(UUID id, Asset updates) {
        Asset asset = getAssetById(id);
        if (updates.getName() != null) asset.setName(updates.getName());
        if (updates.getStatus() != null) asset.setStatus(updates.getStatus());
        if (updates.getHealthScore() != null) asset.setHealthScore(updates.getHealthScore());
        if (updates.getRiskLevel() != null) asset.setRiskLevel(updates.getRiskLevel());
        if (updates.getLastMaintenanceDate() != null) asset.setLastMaintenanceDate(updates.getLastMaintenanceDate());
        if (updates.getMetadata() != null) asset.setMetadata(updates.getMetadata());

        Asset saved = assetRepository.save(asset);
        invalidateCache(id);
        publishEvent("ASSET_UPDATED", saved);
        return saved;
    }

    @Transactional
    public void deleteAsset(UUID id) {
        Asset asset = getAssetById(id);
        asset.setStatus(Asset.AssetStatus.DECOMMISSIONED);
        assetRepository.save(asset);
        invalidateCache(id);
        publishEvent("ASSET_DECOMMISSIONED", asset);
    }

    @Transactional(readOnly = true)
    public List<Sensor> getAssetSensors(UUID assetId) {
        return sensorRepository.findByAssetId(assetId);
    }

    @Transactional(readOnly = true)
    public List<Asset> getNearbyAssets(double lat, double lon, double radiusMeters) {
        return assetRepository.findNearby(lat, lon, radiusMeters);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAssets", assetRepository.count());
        stats.put("activeAssets", assetRepository.countByStatus(Asset.AssetStatus.ACTIVE));
        stats.put("underMaintenance", assetRepository.countByStatus(Asset.AssetStatus.UNDER_MAINTENANCE));
        stats.put("failedAssets", assetRepository.countByStatus(Asset.AssetStatus.FAILED));
        stats.put("averageHealthScore", assetRepository.getAverageHealthScore());
        stats.put("categoryDistribution", assetRepository.countByCategory());
        stats.put("statusDistribution", assetRepository.countByStatus());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }

    @Transactional
    public void updateHealthScore(UUID assetId, Integer healthScore, String riskLevel) {
        Asset asset = getAssetById(assetId);
        asset.setHealthScore(healthScore);
        asset.setRiskLevel(riskLevel);
        assetRepository.save(asset);

        // Cache health score
        String healthKey = HEALTH_PREFIX + assetId;
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("score", healthScore);
        healthData.put("riskLevel", riskLevel);
        healthData.put("timestamp", LocalDateTime.now().toString());
        redisTemplate.opsForValue().set(healthKey, healthData, 1, TimeUnit.HOURS);

        invalidateCache(assetId);
    }

    private void invalidateCache(UUID assetId) {
        redisTemplate.delete(CACHE_PREFIX + assetId);
    }

    @SneakyThrows
    private void publishEvent(String eventType, Asset asset) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", eventType);
        event.put("assetId", asset.getId().toString());
        event.put("assetCode", asset.getAssetCode());
        event.put("timestamp", LocalDateTime.now().toString());
        kafkaTemplate.send("infrastructure.events", objectMapper.writeValueAsString(event));
    }
}
