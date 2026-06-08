# UrbanPulse AI - Phase 6: Enterprise Folder Structure

```
urbanpulse-ai/
├── 📁 .github/
│   ├── workflows/
│   │   ├── ci-backend.yml              # Java microservices CI
│   │   ├── ci-frontend.yml             # Angular CI
│   │   ├── ci-ml.yml                   # Python/ML CI
│   │   ├── cd-deploy.yml               # Deployment pipeline
│   │   └── security-scan.yml           # SAST/DAST scanning
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── CODEOWNERS
│
├── 📁 docs/
│   ├── ARCHITECTURE.md                 # System architecture documentation
│   ├── API_REFERENCE.md                # OpenAPI/Swagger documentation
│   ├── DEPLOYMENT.md                   # Deployment guide
│   ├── DEVELOPMENT.md                  # Development setup guide
│   ├── ML_OPERATIONS.md               # ML pipeline documentation
│   ├── SECURITY.md                     # Security policies
│   ├── RECRUITER_SHOWCASE.md           # Guide for recruiters
│   ├── PHASE1-2_MARKET_RESEARCH.md
│   ├── PHASE3_PRODUCT_DESIGN.md
│   ├── PHASE4_SYSTEM_ARCHITECTURE.md
│   ├── PHASE5_AI_ARCHITECTURE.md
│   └── diagrams/
│       ├── high-level-architecture.png
│       ├── data-flow-diagram.png
│       └── deployment-architecture.png
│
├── 📁 infrastructure/
│   ├── 📁 docker/
│   │   ├── docker-compose.yml          # Local development stack
│   │   ├── docker-compose.prod.yml     # Production stack
│   │   ├── .env.example
│   │   └── 📁 services/
│   │       ├── kafka.yml
│   │       ├── postgres.yml
│   │       ├── redis.yml
│   │       └── elasticsearch.yml
│   │
│   ├── 📁 kubernetes/
│   │   ├── 📁 base/
│   │   │   ├── namespace.yml
│   │   │   ├── configmap.yml
│   │   │   └── secrets.yml
│   │   ├── 📁 api-gateway/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   ├── hpa.yml
│   │   │   └── ingress.yml
│   │   ├── 📁 auth-service/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   └── hpa.yml
│   │   ├── 📁 infrastructure-service/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   └── hpa.yml
│   │   ├── 📁 alert-service/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   └── hpa.yml
│   │   ├── 📁 analytics-service/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   └── hpa.yml
│   │   ├── 📁 prediction-service/
│   │   │   ├── deployment.yml
│   │   │   ├── service.yml
│   │   │   └── hpa.yml
│   │   ├── 📁 kafka/
│   │   │   ├── statefulset.yml
│   │   │   ├── service.yml
│   │   │   └── topics-config.yml
│   │   ├── 📁 databases/
│   │   │   ├── postgres-statefulset.yml
│   │   │   ├── redis-statefulset.yml
│   │   │   └── elasticsearch-statefulset.yml
│   │   └── 📁 monitoring/
│   │       ├── prometheus.yml
│   │       ├── grafana.yml
│   │       └── jaeger.yml
│   │
│   ├── 📁 terraform/
│   │   ├── 📁 modules/
│   │   │   ├── vpc/
│   │   │   ├── eks/
│   │   │   ├── rds/
│   │   │   ├── elasticache/
│   │   │   ├── msk/
│   │   │   └── s3/
│   │   ├── 📁 environments/
│   │   │   ├── dev/
│   │   │   │   ├── main.tf
│   │   │   │   ├── variables.tf
│   │   │   │   └── terraform.tfvars
│   │   │   ├── staging/
│   │   │   └── production/
│   │   └── backend.tf
│   │
│   └── 📁 scripts/
│       ├── setup-local.sh
│       ├── deploy-k8s.sh
│       ├── run-migrations.sh
│       └── generate-certs.sh
│
├── 📁 backend/
│   ├── 📁 api-gateway/
│   │   ├── 📁 src/
│   │   │   ├── 📁 main/
│   │   │   │   ├── 📁 java/com/urbanpulse/gateway/
│   │   │   │   │   ├── ApiGatewayApplication.java
│   │   │   │   │   ├── 📁 config/
│   │   │   │   │   │   ├── GatewayConfig.java
│   │   │   │   │   │   ├── RateLimiterConfig.java
│   │   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   │   └── CircuitBreakerConfig.java
│   │   │   │   │   ├── 📁 filter/
│   │   │   │   │   │   ├── AuthenticationFilter.java
│   │   │   │   │   │   ├── LoggingFilter.java
│   │   │   │   │   │   ├── RateLimitingFilter.java
│   │   │   │   │   │   └── RequestTracingFilter.java
│   │   │   │   │   └── 📁 handler/
│   │   │   │   │       ├── FallbackHandler.java
│   │   │   │   │       └── GlobalExceptionHandler.java
│   │   │   │   └── 📁 resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       └── logback-spring.xml
│   │   │   └── 📁 test/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   ├── 📁 auth-service/
│   │   ├── 📁 src/
│   │   │   ├── 📁 main/
│   │   │   │   ├── 📁 java/com/urbanpulse/auth/
│   │   │   │   │   ├── AuthServiceApplication.java
│   │   │   │   │   ├── 📁 config/
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   ├── JwtConfig.java
│   │   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   │   └── KafkaConfig.java
│   │   │   │   │   ├── 📁 controller/
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   ├── UserController.java
│   │   │   │   │   │   └── RoleController.java
│   │   │   │   │   ├── 📁 service/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── JwtService.java
│   │   │   │   │   │   ├── PasswordService.java
│   │   │   │   │   │   └── KafkaProducerService.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   │   └── RoleRepository.java
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Role.java
│   │   │   │   │   │   ├── Permission.java
│   │   │   │   │   │   └── Session.java
│   │   │   │   │   ├── 📁 dto/
│   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   ├── LoginResponse.java
│   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   ├── UserDto.java
│   │   │   │   │   │   └── TokenRefreshRequest.java
│   │   │   │   │   ├── 📁 exception/
│   │   │   │   │   │   ├── AuthException.java
│   │   │   │   │   │   ├── TokenExpiredException.java
│   │   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   │   └── 📁 security/
│   │   │   │   │       ├── JwtAuthenticationFilter.java
│   │   │   │   │       ├── JwtTokenProvider.java
│   │   │   │   │       ├── UserDetailsServiceImpl.java
│   │   │   │   │       └── PasswordEncoderConfig.java
│   │   │   │   └── 📁 resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       ├── db/migration/
│   │   │   │       │   ├── V1__create_users_table.sql
│   │   │   │       │   └── V2__create_roles_table.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── 📁 test/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   ├── 📁 infrastructure-service/
│   │   ├── 📁 src/
│   │   │   ├── 📁 main/
│   │   │   │   ├── 📁 java/com/urbanpulse/infrastructure/
│   │   │   │   │   ├── InfrastructureServiceApplication.java
│   │   │   │   │   ├── 📁 config/
│   │   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   │   ├── KafkaConfig.java
│   │   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   │   ├── ElasticsearchConfig.java
│   │   │   │   │   │   └── WebSocketConfig.java
│   │   │   │   │   ├── 📁 controller/
│   │   │   │   │   │   ├── AssetController.java
│   │   │   │   │   │   ├── SensorController.java
│   │   │   │   │   │   ├── SensorDataController.java
│   │   │   │   │   │   ├── AssetTypeController.java
│   │   │   │   │   │   └── ImportExportController.java
│   │   │   │   │   ├── 📁 service/
│   │   │   │   │   │   ├── AssetService.java
│   │   │   │   │   │   ├── SensorService.java
│   │   │   │   │   │   ├── SensorDataService.java
│   │   │   │   │   │   ├── GeoSpatialService.java
│   │   │   │   │   │   ├── ImportExportService.java
│   │   │   │   │   │   ├── KafkaProducerService.java
│   │   │   │   │   │   └── CacheService.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   │   ├── AssetRepository.java
│   │   │   │   │   │   ├── SensorRepository.java
│   │   │   │   │   │   ├── SensorDataRepository.java
│   │   │   │   │   │   └── AssetTypeRepository.java
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   │   ├── Asset.java
│   │   │   │   │   │   ├── Sensor.java
│   │   │   │   │   │   ├── SensorReading.java
│   │   │   │   │   │   ├── AssetType.java
│   │   │   │   │   │   ├── AssetRelationship.java
│   │   │   │   │   │   └── MaintenanceRecord.java
│   │   │   │   │   ├── 📁 dto/
│   │   │   │   │   │   ├── AssetDto.java
│   │   │   │   │   │   ├── SensorDto.java
│   │   │   │   │   │   ├── SensorReadingDto.java
│   │   │   │   │   │   ├── AssetHealthDto.java
│   │   │   │   │   │   └── GeoSearchRequest.java
│   │   │   │   │   ├── 📁 event/
│   │   │   │   │   │   ├── SensorDataEvent.java
│   │   │   │   │   │   ├── AssetHealthEvent.java
│   │   │   │   │   │   └── FeatureVectorEvent.java
│   │   │   │   │   ├── 📁 mapper/
│   │   │   │   │   │   ├── AssetMapper.java
│   │   │   │   │   │   └── SensorMapper.java
│   │   │   │   │   └── 📁 websocket/
│   │   │   │   │       ├── SensorWebSocketHandler.java
│   │   │   │   │       └── AlertWebSocketHandler.java
│   │   │   │   └── 📁 resources/
│   │   │   │       ├── application.yml
│   │   │   │       ├── application-dev.yml
│   │   │   │       ├── application-prod.yml
│   │   │   │       ├── db/migration/
│   │   │   │       │   ├── V1__create_assets.sql
│   │   │   │       │   ├── V2__create_sensors.sql
│   │   │   │       │   ├── V3__create_sensor_readings.sql
│   │   │   │       │   └── V4__enable_postgis.sql
│   │   │   │       └── logback-spring.xml
│   │   │   └── 📁 test/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   ├── 📁 alert-service/
│   │   ├── 📁 src/
│   │   │   ├── 📁 main/
│   │   │   │   ├── 📁 java/com/urbanpulse/alert/
│   │   │   │   │   ├── AlertServiceApplication.java
│   │   │   │   │   ├── 📁 config/
│   │   │   │   │   ├── 📁 controller/
│   │   │   │   │   ├── 📁 service/
│   │   │   │   │   │   ├── AlertEngine.java
│   │   │   │   │   │   ├── NotificationService.java
│   │   │   │   │   │   ├── EscalationService.java
│   │   │   │   │   │   └── KafkaConsumerService.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   │   ├── Alert.java
│   │   │   │   │   │   ├── Notification.java
│   │   │   │   │   │   ├── EscalationPolicy.java
│   │   │   │   │   │   └── AlertRule.java
│   │   │   │   │   ├── 📁 dto/
│   │   │   │   │   └── 📁 websocket/
│   │   │   │   └── 📁 resources/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   ├── 📁 analytics-service/
│   │   ├── 📁 src/
│   │   │   ├── 📁 main/
│   │   │   │   ├── 📁 java/com/urbanpulse/analytics/
│   │   │   │   │   ├── AnalyticsServiceApplication.java
│   │   │   │   │   ├── 📁 config/
│   │   │   │   │   ├── 📁 controller/
│   │   │   │   │   ├── 📁 service/
│   │   │   │   │   │   ├── AnalyticsEngine.java
│   │   │   │   │   │   ├── ReportService.java
│   │   │   │   │   │   ├── ElasticsearchQueryBuilder.java
│   │   │   │   │   │   └── CostAvoidanceCalculator.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── 📁 dto/
│   │   │   │   │   └── 📁 scheduler/
│   │   │   │   │       ├── DailyAggregationJob.java
│   │   │   │   │       └── WeeklyReportJob.java
│   │   │   │   └── 📁 resources/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   └── 📁 shared/
│       ├── 📁 src/
│       │   ├── 📁 main/
│       │   │   ├── 📁 java/com/urbanpulse/shared/
│       │   │   │   ├── 📁 events/
│       │   │   │   │   ├── SensorDataEvent.java
│       │   │   │   │   ├── PredictionEvent.java
│       │   │   │   │   ├── AlertEvent.java
│       │   │   │   │   └── InfrastructureEvent.java
│       │   │   │   ├── 📁 dto/
│       │   │   │   │   ├── ApiResponse.java
│       │   │   │   │   ├── PageResponse.java
│       │   │   │   │   └── ErrorResponse.java
│       │   │   │   ├── 📁 security/
│       │   │   │   │   ├── JwtClaims.java
│       │   │   │   │   └── Permission.java
│       │   │   │   └── 📁 exception/
│       │   │   │       ├── UrbanPulseException.java
│       │   │   │       └── ErrorCode.java
│       │   └── 📁 test/
│       └── pom.xml
│
├── 📁 ml-services/
│   ├── 📁 prediction-service/
│   │   ├── 📁 app/
│   │   │   ├── 📁 api/
│   │   │   │   ├── 📁 routes/
│   │   │   │   │   ├── predictions.py
│   │   │   │   │   ├── health.py
│   │   │   │   │   ├── models.py
│   │   │   │   │   └── explanations.py
│   │   │   │   ├── 📁 middleware/
│   │   │   │   │   ├── auth.py
│   │   │   │   │   ├── rate_limit.py
│   │   │   │   │   ├── logging.py
│   │   │   │   │   └── error_handler.py
│   │   │   │   └── router.py
│   │   │   ├── 📁 core/
│   │   │   │   ├── config.py
│   │   │   │   ├── security.py
│   │   │   │   ├── logging_config.py
│   │   │   │   └── events.py
│   │   │   ├── 📁 models/
│   │   │   │   ├── 📁 water_pipes/
│   │   │   │   │   ├── model.pkl
│   │   │   │   │   ├── model.onnx
│   │   │   │   │   ├── preprocessor.pkl
│   │   │   │   │   └── metadata.json
│   │   │   │   ├── 📁 bridges/
│   │   │   │   ├── 📁 roads/
│   │   │   │   └── 📁 power_lines/
│   │   │   ├── 📁 services/
│   │   │   │   ├── prediction_service.py
│   │   │   │   ├── feature_service.py
│   │   │   │   ├── model_service.py
│   │   │   │   ├── explanation_service.py
│   │   │   │   ├── drift_detector.py
│   │   │   │   └── kafka_service.py
│   │   │   ├── 📁 infrastructure/
│   │   │   │   ├── 📁 database/
│   │   │   │   │   ├── connection.py
│   │   │   │   │   ├── repositories.py
│   │   │   │   │   └── models.py
│   │   │   │   ├── 📁 cache/
│   │   │   │   │   └── redis_client.py
│   │   │   │   ├── 📁 messaging/
│   │   │   │   │   └── kafka_producer.py
│   │   │   │   └── 📁 search/
│   │   │   │       └── elasticsearch_client.py
│   │   │   ├── 📁 domain/
│   │   │   │   ├── entities.py
│   │   │   │   ├── value_objects.py
│   │   │   │   └── enums.py
│   │   │   └── main.py
│   │   ├── 📁 notebooks/
│   │   │   ├── 01_data_exploration.ipynb
│   │   │   ├── 02_feature_engineering.ipynb
│   │   │   ├── 03_model_training_water_pipes.ipynb
│   │   │   ├── 04_model_training_bridges.ipynb
│   │   │   ├── 05_model_evaluation.ipynb
│   │   │   ├── 06_synthetic_data_generation.ipynb
│   │   │   └── 07_explainability.ipynb
│   │   ├── 📁 training/
│   │   │   ├── 📁 pipelines/
│   │   │   │   ├── data_preparation.py
│   │   │   │   ├── feature_engineering.py
│   │   │   │   ├── model_training.py
│   │   │   │   ├── model_evaluation.py
│   │   │   │   └── model_registration.py
│   │   │   ├── 📁 scripts/
│   │   │   │   ├── train_water_pipe_model.py
│   │   │   │   ├── train_bridge_model.py
│   │   │   │   ├── train_road_model.py
│   │   │   │   ├── train_power_line_model.py
│   │   │   │   └── generate_synthetic_data.py
│   │   │   └── 📁 configs/
│   │   │       ├── water_pipe_config.yml
│   │   │       ├── bridge_config.yml
│   │   │       └── global_config.yml
│   │   ├── 📁 tests/
│   │   │   ├── test_predictions.py
│   │   │   ├── test_explanations.py
│   │   │   ├── test_drift_detection.py
│   │   │   └── conftest.py
│   │   ├── Dockerfile
│   │   ├── requirements.txt
│   │   ├── requirements-dev.txt
│   │   ├── pyproject.toml
│   │   └── README.md
│   │
│   └── 📁 feature-store/
│       ├── 📁 app/
│       │   ├── feature_store.py
│       │   ├── online_store.py
│       │   └── offline_store.py
│       ├── Dockerfile
│       └── requirements.txt
│
├── 📁 frontend/
│   ├── 📁 src/
│   │   ├── 📁 app/
│   │   │   ├── 📁 core/
│   │   │   │   ├── 📁 services/
│   │   │   │   │   ├── api.service.ts
│   │   │   │   │   ├── auth.service.ts
│   │   │   │   │   ├── websocket.service.ts
│   │   │   │   │   ├── notification.service.ts
│   │   │   │   │   ├── theme.service.ts
│   │   │   │   │   └── http-interceptor.service.ts
│   │   │   │   ├── 📁 guards/
│   │   │   │   │   ├── auth.guard.ts
│   │   │   │   │   ├── role.guard.ts
│   │   │   │   │   └── unsaved-changes.guard.ts
│   │   │   │   ├── 📁 models/
│   │   │   │   │   ├── user.model.ts
│   │   │   │   │   ├── asset.model.ts
│   │   │   │   │   ├── sensor.model.ts
│   │   │   │   │   ├── prediction.model.ts
│   │   │   │   │   ├── alert.model.ts
│   │   │   │   │   └── api-response.model.ts
│   │   │   │   └── 📁 interceptors/
│   │   │   │       ├── auth.interceptor.ts
│   │   │   │       ├── error.interceptor.ts
│   │   │   │       └── loading.interceptor.ts
│   │   │   │
│   │   │   ├── 📁 features/
│   │   │   │   ├── 📁 auth/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── login/
│   │   │   │   │   │   │   ├── login.component.ts
│   │   │   │   │   │   │   ├── login.component.html
│   │   │   │   │   │   │   └── login.component.scss
│   │   │   │   │   │   └── register/
│   │   │   │   │   ├── auth.module.ts
│   │   │   │   │   └── auth-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 dashboard/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   │   ├── dashboard.component.ts
│   │   │   │   │   │   │   ├── dashboard.component.html
│   │   │   │   │   │   │   └── dashboard.component.scss
│   │   │   │   │   │   ├── kpi-cards/
│   │   │   │   │   │   ├── health-chart/
│   │   │   │   │   │   ├── prediction-timeline/
│   │   │   │   │   │   └── recent-alerts/
│   │   │   │   │   ├── dashboard.module.ts
│   │   │   │   │   └── dashboard-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 map/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── interactive-map/
│   │   │   │   │   │   │   ├── interactive-map.component.ts
│   │   │   │   │   │   │   ├── interactive-map.component.html
│   │   │   │   │   │   │   └── interactive-map.component.scss
│   │   │   │   │   │   ├── asset-overlay/
│   │   │   │   │   │   ├── heatmap-layer/
│   │   │   │   │   │   └── prediction-layer/
│   │   │   │   │   ├── map.module.ts
│   │   │   │   │   └── map-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 assets/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── asset-list/
│   │   │   │   │   │   ├── asset-detail/
│   │   │   │   │   │   ├── asset-form/
│   │   │   │   │   │   ├── sensor-list/
│   │   │   │   │   │   └── sensor-data/
│   │   │   │   │   ├── assets.module.ts
│   │   │   │   │   └── assets-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 predictions/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── predictions-list/
│   │   │   │   │   │   ├── prediction-detail/
│   │   │   │   │   │   ├── shap-explanation/
│   │   │   │   │   │   └── confidence-chart/
│   │   │   │   │   ├── predictions.module.ts
│   │   │   │   │   └── predictions-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 alerts/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── alerts-feed/
│   │   │   │   │   │   ├── alert-detail/
│   │   │   │   │   │   ├── alert-rules/
│   │   │   │   │   │   └── alert-history/
│   │   │   │   │   ├── alerts.module.ts
│   │   │   │   │   └── alerts-routing.module.ts
│   │   │   │   │
│   │   │   │   ├── 📁 analytics/
│   │   │   │   │   ├── 📁 components/
│   │   │   │   │   │   ├── analytics-dashboard/
│   │   │   │   │   │   ├── cost-avoidance/
│   │   │   │   │   │   ├── model-performance/
│   │   │   │   │   │   └── reports/
│   │   │   │   │   ├── analytics.module.ts
│   │   │   │   │   └── analytics-routing.module.ts
│   │   │   │   │
│   │   │   │   └── 📁 settings/
│   │   │   │       ├── 📁 components/
│   │   │   │       │   ├── profile/
│   │   │   │       │   ├── notifications/
│   │   │   │       │   └── users-management/
│   │   │   │       ├── settings.module.ts
│   │   │   │       └── settings-routing.module.ts
│   │   │   │
│   │   │   ├── 📁 shared/
│   │   │   │   ├── 📁 components/
│   │   │   │   │   ├── navbar/
│   │   │   │   │   ├── sidebar/
│   │   │   │   │   ├── footer/
│   │   │   │   │   ├── loading-spinner/
│   │   │   │   │   ├── confirm-dialog/
│   │   │   │   │   ├── data-table/
│   │   │   │   │   ├── search-filter/
│   │   │   │   │   ├── pagination/
│   │   │   │   │   ├── toast-notification/
│   │   │   │   │   └── chart-widget/
│   │   │   │   ├── 📁 directives/
│   │   │   │   │   ├── highlight.directive.ts
│   │   │   │   │   └── permission.directive.ts
│   │   │   │   ├── 📁 pipes/
│   │   │   │   │   ├── severity-color.pipe.ts
│   │   │   │   │   ├── time-ago.pipe.ts
│   │   │   │   │   └── health-score.pipe.ts
│   │   │   │   └── shared.module.ts
│   │   │   │
│   │   │   ├── app.component.ts
│   │   │   ├── app.component.html
│   │   │   ├── app.component.scss
│   │   │   ├── app.module.ts
│   │   │   └── app-routing.module.ts
│   │   │
│   │   ├── 📁 assets/
│   │   │   ├── 📁 images/
│   │   │   ├── 📁 icons/
│   │   │   └── 📁 data/
│   │   │
│   │   ├── 📁 environments/
│   │   │   ├── environment.ts
│   │   │   ├── environment.development.ts
│   │   │   └── environment.production.ts
│   │   │
│   │   ├── index.html
│   │   ├── main.ts
│   │   ├── styles.scss
│   │   └── variables.scss
│   │
│   ├── 📁 e2e/
│   ├── angular.json
│   ├── package.json
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   ├── tailwind.config.js
│   └── Dockerfile
│
├── 📁 data/
│   ├── 📁 seeds/
│   │   ├── sample_assets.json
│   │   ├── sample_sensors.json
│   │   ├── sample_users.sql
│   │   └── sample_predictions.json
│   ├── 📁 synthetic/
│   │   ├── generate_water_pipe_data.py
│   │   ├── generate_bridge_data.py
│   │   └── generate_road_data.py
│   └── 📁 migrations/
│
├── 📁 monitoring/
│   ├── 📁 prometheus/
│   │   └── prometheus.yml
│   ├── 📁 grafana/
│   │   └── 📁 dashboards/
│   ├── 📁 jaeger/
│   │   └── jaeger.yml
│   └── 📁 alerts/
│       └── alertmanager.yml
│
├── .gitignore
├── .dockerignore
├── Makefile
├── docker-compose.yml
├── skaffold.yaml
├── README.md
├── LICENSE
└── CONTRIBUTING.md
```

---

## File Count Summary

| Component | Files | Languages |
|-----------|-------|-----------|
| API Gateway | 12 | Java |
| Auth Service | 25 | Java |
| Infrastructure Service | 35 | Java |
| Alert Service | 20 | Java |
| Analytics Service | 18 | Java |
| Shared Library | 10 | Java |
| Prediction Service (ML) | 30 | Python |
| Angular Frontend | 80+ | TypeScript |
| Infrastructure Configs | 40+ | YAML, HCL |
| Documentation | 15 | Markdown |
| **Total** | **285+** | **5 Languages** |
