package com.urbanpulse.infrastructure.service;

import com.urbanpulse.infrastructure.model.Sensor;
import com.urbanpulse.infrastructure.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LATEST_PREFIX = "sensor:latest:";

    @Transactional(readOnly = true)
    public List<Sensor> getSensorsByAsset(UUID assetId) {
        return sensorRepository.findByAssetId(assetId);
    }

    @Transactional(readOnly = true)
    public Sensor getSensorById(UUID id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found: " + id));
    }

    @Transactional
    public Sensor createSensor(Sensor sensor) {
        if (sensorRepository.findBySensorCode(sensor.getSensorCode()).isPresent()) {
            throw new IllegalArgumentException("Sensor code already exists: " + sensor.getSensorCode());
        }
        return sensorRepository.save(sensor);
    }

    @Transactional
    public void updateLastReading(UUID sensorId, Double value) {
        sensorRepository.updateLastReading(sensorId, value, LocalDateTime.now());

        // Cache latest reading
        String key = LATEST_PREFIX + sensorId;
        redisTemplate.opsForHash().put(key, "value", value.toString());
        redisTemplate.opsForHash().put(key, "timestamp", LocalDateTime.now().toString());
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    @Transactional(readOnly = true)
    public long getActiveSensorCount() {
        return sensorRepository.countByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getSensorTypeDistribution() {
        return sensorRepository.countBySensorType();
    }
}
