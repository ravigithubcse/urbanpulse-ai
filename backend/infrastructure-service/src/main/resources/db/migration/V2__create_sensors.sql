-- Create sensors table
CREATE TABLE IF NOT EXISTS sensors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_code VARCHAR(50) NOT NULL UNIQUE,
    asset_id UUID NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    sensor_type VARCHAR(50) NOT NULL,
    protocol VARCHAR(20) NOT NULL,
    mqtt_topic VARCHAR(200),
    http_endpoint VARCHAR(500),
    unit VARCHAR(20),
    min_value DOUBLE PRECISION,
    max_value DOUBLE PRECISION,
    threshold_warning DOUBLE PRECISION,
    threshold_critical DOUBLE PRECISION,
    location GEOMETRY(Point, 4326),
    is_active BOOLEAN NOT NULL DEFAULT true,
    firmware_version VARCHAR(50),
    battery_level INTEGER,
    last_reading_at TIMESTAMP,
    last_reading_value DOUBLE PRECISION,
    reading_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_sensors_asset_id ON sensors(asset_id);
CREATE INDEX idx_sensors_type ON sensors(sensor_type);
CREATE INDEX idx_sensors_active ON sensors(is_active);
CREATE INDEX idx_sensors_protocol ON sensors(protocol);

-- Create trigger
CREATE TRIGGER update_sensors_updated_at
    BEFORE UPDATE ON sensors
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
