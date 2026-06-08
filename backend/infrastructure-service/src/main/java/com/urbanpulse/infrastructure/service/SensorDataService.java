package com.urbanpulse.infrastructure.service;

import com.urbanpulse.infrastructure.model.SensorReading;
import com.urbanpulse.infrastructure.repository.SensorReadingRepository;
import com.urbanpulse.infrastructure.repository.SensorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorDataService {

    private final SensorReadingRepository readingRepository;
    private final SensorRepository sensorRepository;
    private final SensorService sensorService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public SensorReading ingestReading(SensorReading reading) {
        // Validate sensor exists
        sensorRepository.findById(reading.getSensorId())
                .orElseThrow(() -> new RuntimeException("Sensor not found: " + reading.getSensorId()));

        SensorReading saved = readingRepository.save(reading);

        // Update sensor last reading
        sensorService.updateLastReading(reading.getSensorId(), reading.getValue());

        // Publish to Kafka for downstream processing
        publishToKafka(saved);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<SensorReading> getRecentReadings(UUID sensorId, int limit) {
        return readingRepository.findBySensorIdOrderByRecordedAtDesc(sensorId, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<SensorReading> getReadingsByTimeRange(UUID sensorId, LocalDateTime start, LocalDateTime end) {
        return readingRepository.findBySensorIdAndTimeRange(sensorId, start, end);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSensorStatistics(UUID sensorId, LocalDateTime since) {
        List<Object[]> results = readingRepository.getStatistics(sensorId, since);
        if (results.isEmpty()) return Collections.emptyMap();

        Object[] stats = results.get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("average", stats[0]);
        result.put("minimum", stats[1]);
        result.put("maximum", stats[2]);
        result.put("stdDev", stats[3]);
        result.put("count", stats[4]);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTimeSeries(UUID sensorId, LocalDateTime since) {
        List<Object[]> results = readingRepository.getTimeSeries(sensorId, since);
        List<Map<String, Object>> series = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> point = new HashMap<>();
            point.put("timestamp", row[0].toString());
            point.put("value", row[1]);
            series.add(point);
        }
        return series;
    }

    @Transactional
    public List<SensorReading> batchIngest(List<SensorReading> readings) {
        List<SensorReading> saved = readingRepository.saveAll(readings);
        saved.forEach(this::publishToKafka);
        return saved;
    }

    @SneakyThrows
    private void publishToKafka(SensorReading reading) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "SENSOR_READING");
        event.put("sensorId", reading.getSensorId().toString());
        event.put("assetId", reading.getAssetId().toString());
        event.put("value", reading.getValue());
        event.put("unit", reading.getUnit());
        event.put("recordedAt", reading.getRecordedAt().toString());
        event.put("isAnomaly", reading.getIsAnomaly());
        kafkaTemplate.send("raw.sensor.data", objectMapper.writeValueAsString(event));
    }
}
