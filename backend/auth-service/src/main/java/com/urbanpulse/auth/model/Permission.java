package com.urbanpulse.auth.model;

/**
 * Fine-grained permissions for RBAC.
 * Permissions are grouped by domain and action.
 */
public enum Permission {
    // Asset permissions
    ASSET_READ,
    ASSET_CREATE,
    ASSET_UPDATE,
    ASSET_DELETE,
    ASSET_IMPORT,
    ASSET_EXPORT,

    // Sensor permissions
    SENSOR_READ,
    SENSOR_CREATE,
    SENSOR_UPDATE,
    SENSOR_DELETE,
    SENSOR_DATA_READ,

    // Prediction permissions
    PREDICTION_READ,
    PREDICTION_CREATE,
    PREDICTION_EXPLAIN,
    PREDICTION_RETRAIN,

    // Alert permissions
    ALERT_READ,
    ALERT_ACKNOWLEDGE,
    ALERT_RESOLVE,
    ALERT_CONFIGURE,

    // Analytics permissions
    ANALYTICS_READ,
    ANALYTICS_EXPORT,
    ANALYTICS_CONFIGURE,

    // User management permissions
    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,
    USER_MANAGE_ROLES,

    // System permissions
    SYSTEM_CONFIGURE,
    SYSTEM_MONITOR,
    SYSTEM_ADMIN
}
