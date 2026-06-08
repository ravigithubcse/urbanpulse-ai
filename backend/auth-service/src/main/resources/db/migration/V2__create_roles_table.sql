-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create role_permissions table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_id, permission)
);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Full system access - can manage users, configure system, and access all features'),
    ('OPERATOR', 'Can manage assets, sensors, view predictions, and acknowledge alerts'),
    ('VIEWER', 'Read-only access to dashboards, maps, and reports'),
    ('FIELD_ENGINEER', 'Can view assigned work orders, update maintenance records, and use mobile features')
ON CONFLICT (name) DO NOTHING;

-- Insert permissions for ADMIN role
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (
    VALUES
        ('ASSET_READ'), ('ASSET_CREATE'), ('ASSET_UPDATE'), ('ASSET_DELETE'), ('ASSET_IMPORT'), ('ASSET_EXPORT'),
        ('SENSOR_READ'), ('SENSOR_CREATE'), ('SENSOR_UPDATE'), ('SENSOR_DELETE'), ('SENSOR_DATA_READ'),
        ('PREDICTION_READ'), ('PREDICTION_CREATE'), ('PREDICTION_EXPLAIN'), ('PREDICTION_RETRAIN'),
        ('ALERT_READ'), ('ALERT_ACKNOWLEDGE'), ('ALERT_RESOLVE'), ('ALERT_CONFIGURE'),
        ('ANALYTICS_READ'), ('ANALYTICS_EXPORT'), ('ANALYTICS_CONFIGURE'),
        ('USER_READ'), ('USER_CREATE'), ('USER_UPDATE'), ('USER_DELETE'), ('USER_MANAGE_ROLES'),
        ('SYSTEM_CONFIGURE'), ('SYSTEM_MONITOR'), ('SYSTEM_ADMIN')
) AS p(permission)
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insert permissions for OPERATOR role
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (
    VALUES
        ('ASSET_READ'), ('ASSET_CREATE'), ('ASSET_UPDATE'), ('ASSET_IMPORT'),
        ('SENSOR_READ'), ('SENSOR_CREATE'), ('SENSOR_UPDATE'), ('SENSOR_DATA_READ'),
        ('PREDICTION_READ'), ('PREDICTION_EXPLAIN'),
        ('ALERT_READ'), ('ALERT_ACKNOWLEDGE'), ('ALERT_RESOLVE'),
        ('ANALYTICS_READ'), ('ANALYTICS_EXPORT')
) AS p(permission)
WHERE r.name = 'OPERATOR'
ON CONFLICT DO NOTHING;

-- Insert permissions for VIEWER role
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (
    VALUES
        ('ASSET_READ'), ('SENSOR_READ'), ('SENSOR_DATA_READ'),
        ('PREDICTION_READ'), ('ALERT_READ'), ('ANALYTICS_READ')
) AS p(permission)
WHERE r.name = 'VIEWER'
ON CONFLICT DO NOTHING;

-- Insert permissions for FIELD_ENGINEER role
INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission
FROM roles r
CROSS JOIN (
    VALUES
        ('ASSET_READ'), ('SENSOR_READ'), ('SENSOR_DATA_READ'),
        ('PREDICTION_READ'), ('ALERT_READ'), ('ALERT_ACKNOWLEDGE'), ('ALERT_RESOLVE')
) AS p(permission)
WHERE r.name = 'FIELD_ENGINEER'
ON CONFLICT DO NOTHING;

-- Assign ADMIN role to default admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@urbanpulse.ai' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;
