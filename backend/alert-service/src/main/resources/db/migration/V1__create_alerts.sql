CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prediction_id UUID,
    asset_id UUID NOT NULL,
    asset_name VARCHAR(200),
    severity VARCHAR(20) NOT NULL,
    alert_type VARCHAR(30) NOT NULL,
    title VARCHAR(300) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    confidence_score DOUBLE PRECISION,
    predicted_failure_at TIMESTAMP,
    acknowledged_by UUID,
    acknowledged_at TIMESTAMP,
    acknowledgment_notes VARCHAR(1000),
    resolved_by UUID,
    resolved_at TIMESTAMP,
    resolution_notes VARCHAR(1000),
    escalation_level INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_asset ON alerts(asset_id);
CREATE INDEX idx_alerts_created ON alerts(created_at DESC);
CREATE INDEX idx_alerts_status_created ON alerts(status, created_at DESC);

CREATE TRIGGER update_alerts_updated_at
    BEFORE UPDATE ON alerts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();