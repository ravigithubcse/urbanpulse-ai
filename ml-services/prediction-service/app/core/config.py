"""Application configuration using Pydantic Settings."""
import time
from typing import List, Optional

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    APP_NAME: str = "UrbanPulse Prediction Service"
    DEBUG: bool = False
    ENVIRONMENT: str = "development"

    # Database
    DB_HOST: str = "localhost"
    DB_PORT: int = 5432
    DB_NAME: str = "urbanpulse"
    DB_USER: str = "urbanpulse"
    DB_PASSWORD: str = "urbanpulse_secret"

    # Redis
    REDIS_HOST: str = "localhost"
    REDIS_PORT: int = 6379

    # Kafka
    KAFKA_BOOTSTRAP_SERVERS: str = "localhost:9092"
    KAFKA_PREDICTION_TOPIC: str = "predictions.generated"

    # Elasticsearch
    ES_HOST: str = "localhost"
    ES_PORT: int = 9200

    # ML
    MODEL_PATH: str = "/app/models"
    DEFAULT_MODEL_VERSION: str = "v1.0.0"
    PREDICTION_THRESHOLD: float = 0.5
    BATCH_SIZE: int = 100

    # Performance
    MAX_WORKERS: int = 4
    REQUEST_TIMEOUT: int = 30

    # Startup
    start_time: float = time.time()

    class Config:
        env_file = ".env"
        case_sensitive = False