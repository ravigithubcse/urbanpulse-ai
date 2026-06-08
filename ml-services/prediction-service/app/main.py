"""
UrbanPulse AI - Prediction Service

FastAPI-based ML inference service for infrastructure failure prediction.
Supports multiple infrastructure types with ONNX runtime for optimized inference.

Author: Ravi Kumar
Version: 1.0.0
"""
import os
import time
import uuid
from contextlib import asynccontextmanager
from datetime import datetime, timedelta
from typing import Dict, List, Optional

import joblib
import numpy as np
import onnxruntime as ort
import pandas as pd
from fastapi import FastAPI, HTTPException, Query, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse
from prometheus_client import Counter, Histogram, generate_latest, CONTENT_TYPE_LATEST

from app.core.config import Settings
from app.services.prediction_service import PredictionService
from app.services.feature_service import FeatureService
from app.services.explanation_service import ExplanationService
from app.domain.entities import (
    PredictionRequest, PredictionResponse, BatchPredictionRequest,
    BatchPredictionResponse, ExplanationResponse, HealthCheckResponse,
    ModelInfoResponse, ModelMetrics
)

settings = Settings()

# Prometheus metrics
PREDICTION_COUNTER = Counter(
    'prediction_requests_total',
    'Total prediction requests',
    ['asset_type', 'model_version']
)
PREDICTION_LATENCY = Histogram(
    'prediction_latency_seconds',
    'Prediction latency in seconds',
    ['asset_type']
)
PREDICTION_PROBABILITY = Histogram(
    'prediction_probability',
    'Distribution of prediction probabilities',
    ['asset_type']
)

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager for startup/shutdown."""
    print(f"🚀 Starting UrbanPulse Prediction Service v1.0.0")
    print(f"📊 Loaded models for: WATER_PIPE, BRIDGE, ROAD, POWER_LINE")
    print(f"⚡ ONNX Runtime providers: {ort.get_available_providers()}")
    yield
    print("🛑 Shutting down Prediction Service")

app = FastAPI(
    title="UrbanPulse AI - Prediction Service",
    description="ML-powered infrastructure failure prediction API",
    version="1.0.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc"
)

# Middleware
app.add_middleware(GZipMiddleware, minimum_size=1000)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize services
prediction_service = PredictionService()
feature_service = FeatureService()
explanation_service = ExplanationService()

@app.middleware("http")
async def add_metrics_headers(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    return response


@app.get("/", tags=["Root"])
async def root():
    return {
        "service": "UrbanPulse AI Prediction Service",
        "version": "1.0.0",
        "status": "running",
        "models_loaded": ["WATER_PIPE", "BRIDGE", "ROAD", "POWER_LINE"],
        "docs": "/docs"
    }


@app.get("/health", response_model=HealthCheckResponse, tags=["Health"])
async def health_check():
    return HealthCheckResponse(
        status="healthy",
        timestamp=datetime.utcnow(),
        version="1.0.0",
        uptime_seconds=int(time.time() - settings.start_time),
        models_loaded=len(prediction_service.models),
        onnx_runtime_version=ort.__version__,
        memory_usage_mb=prediction_service.get_memory_usage()
    )


@app.get("/metrics", tags=["Metrics"])
async def metrics():
    from fastapi.responses import Response
    return Response(content=generate_latest(), media_type=CONTENT_TYPE_LATEST)


@app.get("/models", response_model=Dict[str, ModelInfoResponse], tags=["Models"])
async def list_models():
    return prediction_service.get_model_info()


@app.get("/models/{asset_type}/metrics", response_model=ModelMetrics, tags=["Models"])
async def get_model_metrics(asset_type: str):
    return prediction_service.get_model_metrics(asset_type)


@app.post("/predict", response_model=PredictionResponse, tags=["Predictions"])
async def predict(request: PredictionRequest):
    start_time = time.time()

    try:
        # Get features
        features = await feature_service.get_features(
            asset_id=request.asset_id,
            asset_type=request.asset_type,
            window_hours=request.window_hours
        )

        # Run prediction
        probability, confidence_interval = prediction_service.predict(
            asset_type=request.asset_type,
            features=features
        )

        # Generate explanation
        explanation = explanation_service.explain(
            asset_type=request.asset_type,
            features=features,
            prediction=probability
        )

        latency = time.time() - start_time

        # Update metrics
        PREDICTION_COUNTER.labels(
            asset_type=request.asset_type,
            model_version=prediction_service.get_model_version(request.asset_type)
        ).inc()
        PREDICTION_LATENCY.labels(asset_type=request.asset_type).observe(latency)
        PREDICTION_PROBABILITY.labels(asset_type=request.asset_type).observe(probability)

        return PredictionResponse(
            prediction_id=str(uuid.uuid4()),
            asset_id=request.asset_id,
            asset_type=request.asset_type,
            failure_probability=round(probability, 4),
            confidence_lower=round(confidence_interval[0], 4),
            confidence_upper=round(confidence_interval[1], 4),
            predicted_failure_window_start=datetime.utcnow(),
            predicted_failure_window_end=datetime.utcnow() + timedelta(hours=72),
            contributing_factors=explanation.contributing_factors,
            shap_values=explanation.shap_values,
            feature_importance=explanation.feature_importance,
            model_version=prediction_service.get_model_version(request.asset_type),
            inference_latency_ms=round(latency * 1000, 2),
            timestamp=datetime.utcnow()
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")


@app.post("/predict/batch", response_model=BatchPredictionResponse, tags=["Predictions"])
async def predict_batch(request: BatchPredictionRequest):
    results = []
    for pred_request in request.requests:
        try:
            result = await predict(pred_request)
            results.append(result)
        except Exception as e:
            results.append(PredictionResponse(
                prediction_id=str(uuid.uuid4()),
                asset_id=pred_request.asset_id,
                asset_type=pred_request.asset_type,
                failure_probability=-1.0,
                confidence_lower=0.0,
                confidence_upper=0.0,
                contributing_factors={},
                shap_values={},
                feature_importance={},
                model_version="error",
                inference_latency_ms=0.0,
                timestamp=datetime.utcnow()
            ))

    return BatchPredictionResponse(
        predictions=results,
        total=len(request.requests),
        successful=sum(1 for r in results if r.failure_probability >= 0),
        failed=sum(1 for r in results if r.failure_probability < 0),
        timestamp=datetime.utcnow()
    )


@app.get("/predict/{asset_type}/top-risk", response_model=List[PredictionResponse], tags=["Predictions"])
async def get_top_risk_assets(
    asset_type: str,
    limit: int = Query(default=10, ge=1, le=100),
    min_probability: float = Query(default=0.5, ge=0.0, le=1.0)
):
    return prediction_service.get_top_risk_assets(asset_type, limit, min_probability)


@app.get("/explain/{prediction_id}", response_model=ExplanationResponse, tags=["Explainability"])
async def explain_prediction(prediction_id: str):
    return explanation_service.get_explanation(prediction_id)


@app.get("/features/{asset_id}", tags=["Features"])
async def get_features(
    asset_id: str,
    asset_type: Optional[str] = None,
    window_hours: int = Query(default=24, ge=1, le=168)
):
    features = await feature_service.get_features(
        asset_id=asset_id,
        asset_type=asset_type,
        window_hours=window_hours
    )
    return {"asset_id": asset_id, "features": features.to_dict()}


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={
            "error": "Internal Server Error",
            "message": str(exc),
            "path": str(request.url),
            "timestamp": datetime.utcnow().isoformat()
        }
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)