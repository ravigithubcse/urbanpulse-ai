"""Domain entities and DTOs for the prediction service."""
from datetime import datetime
from typing import Dict, List, Optional

from pydantic import BaseModel, Field


class PredictionRequest(BaseModel):
    asset_id: str = Field(..., description="Unique asset identifier")
    asset_type: str = Field(..., description="Type: WATER_PIPE, BRIDGE, ROAD, POWER_LINE")
    window_hours: int = Field(default=72, ge=1, le=168)
    include_explanation: bool = Field(default=True)
    model_version: Optional[str] = Field(default=None)


class PredictionResponse(BaseModel):
    prediction_id: str
    asset_id: str
    asset_type: str
    failure_probability: float = Field(..., ge=0.0, le=1.0)
    confidence_lower: float
    confidence_upper: float
    predicted_failure_window_start: datetime
    predicted_failure_window_end: datetime
    contributing_factors: Dict[str, float]
    shap_values: Dict[str, float]
    feature_importance: Dict[str, float]
    model_version: str
    inference_latency_ms: float
    timestamp: datetime


class BatchPredictionRequest(BaseModel):
    requests: List[PredictionRequest]


class BatchPredictionResponse(BaseModel):
    predictions: List[PredictionResponse]
    total: int
    successful: int
    failed: int
    timestamp: datetime


class ExplanationResponse(BaseModel):
    prediction_id: str
    asset_id: str
    asset_type: str
    top_features: List[Dict[str, any]]
    shap_values: Dict[str, float]
    feature_importance: Dict[str, float]
    summary: str


class HealthCheckResponse(BaseModel):
    status: str
    timestamp: datetime
    version: str
    uptime_seconds: int
    models_loaded: int
    onnx_runtime_version: str
    memory_usage_mb: float


class ModelInfoResponse(BaseModel):
    asset_type: str
    version: str
    algorithm: str
    framework: str
    input_features: int
    last_trained: datetime
    accuracy: float
    precision: float
    recall: float
    f1_score: float
    pr_auc: float
    roc_auc: float
    status: str


class ModelMetrics(BaseModel):
    asset_type: str
    version: str
    total_predictions: int
    avg_latency_ms: float
    accuracy: float
    precision: float
    recall: float
    f1_score: float
    pr_auc: float
    calibration_error: float
    drift_detected: bool
    drift_score: float
    last_evaluated: datetime