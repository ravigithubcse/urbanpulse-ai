-- Create sensor_readings table
CREATE TABLE IF NOT EXISTS sensor_readings (
    id BIGSERIAL PRIMARY KEY,
    sensor_id UUID NOT NULL,
    asset_id UUID NOT NULL,
    value NUMERIC(18,6) NOT NULL,
    unit VARCHAR(20),
    quality_score INTEGER DEFAULT 100,
    is_anomaly BOOLEAN DEFAULT false,
    anomaly_score DOUBLE PRECISION,
    raw_data JSONB,
    recorded_at TIMESTAMP NOT NULL,
    ingested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_readings_sensor_time ON sensor_readings(sensor_id, recorded_at DESC);
CREATE INDEX idx_readings_asset_time ON sensor_readings(asset_id, recorded_at DESC);
CREATE INDEX idx_readings_recorded_at ON sensor_readings(recorded_at DESC);
CREATE INDEX idx_readings_anomaly ON sensor_readings(is_anomaly, recorded_at DESC) WHERE is_anomaly = true;

-- Create partitioned table for high-volume data (optional optimization)
-- This creates a template for monthly partitioning
CREATE TABLE IF NOT EXISTS sensor_readings_template (LIKE sensor_readings INCLUDING ALL);
