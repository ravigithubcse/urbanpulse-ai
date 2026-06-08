-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create asset_types table
CREATE TABLE IF NOT EXISTS asset_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    icon VARCHAR(50),
    schema_definition JSONB,
    default_sensor_types JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create assets table
CREATE TABLE IF NOT EXISTS assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    asset_code VARCHAR(50) NOT NULL UNIQUE,
    asset_type_id UUID NOT NULL REFERENCES asset_types(id),
    category VARCHAR(50) NOT NULL,
    location GEOMETRY(Point, 4326),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    health_score INTEGER DEFAULT 100,
    risk_level VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    installation_date DATE,
    last_maintenance_date DATE,
    next_scheduled_maintenance DATE,
    material VARCHAR(100),
    age_years INTEGER,
    length_meters DOUBLE PRECISION,
    diameter_mm DOUBLE PRECISION,
    capacity DOUBLE PRECISION,
    manufacturer VARCHAR(200),
    model VARCHAR(200),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes
CREATE INDEX idx_assets_category ON assets(category);
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_assets_city ON assets(city);
CREATE INDEX idx_assets_health_score ON assets(health_score);
CREATE INDEX idx_assets_location ON assets USING GIST(location);

-- Create trigger for updated_at
CREATE TRIGGER update_assets_updated_at
    BEFORE UPDATE ON assets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert default asset types
INSERT INTO asset_types (name, category, description, icon) VALUES
    ('Water Main', 'WATER', 'Primary water distribution pipe', 'water-pipe'),
    ('Sewer Line', 'WATER', 'Wastewater collection pipe', 'sewer-pipe'),
    ('Bridge', 'TRANSPORTATION', 'Road or pedestrian bridge structure', 'bridge'),
    ('Road Segment', 'TRANSPORTATION', 'Paved road section', 'road'),
    ('Traffic Signal', 'TRANSPORTATION', 'Intersection traffic control', 'traffic-light'),
    ('Power Line', 'ENERGY', 'Electrical transmission line', 'power-line'),
    ('Transformer', 'ENERGY', 'Electrical voltage transformer', 'transformer'),
    ('Street Light', 'LIGHTING', 'Public street illumination', 'street-light'),
    ('Telecom Tower', 'COMMUNICATION', 'Cellular communication tower', 'tower'),
    ('Fiber Cable', 'COMMUNICATION', 'Fiber optic communication cable', 'fiber-cable')
ON CONFLICT (name) DO NOTHING;
