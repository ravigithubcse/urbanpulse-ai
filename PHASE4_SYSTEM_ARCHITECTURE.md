# UrbanPulse AI - Phase 4: System Architecture

## 1. High-Level Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        WEB[Angular Web App]
        MOB[Mobile Web / PWA]
    end

    subgraph "Edge Layer"
        CDN[CloudFront CDN]
        WAF[AWS WAF]
    end

    subgraph "Gateway Layer"
        AG[API Gateway<br/>Spring Cloud Gateway]
        LB[Application Load Balancer]
    end

    subgraph "Core Microservices"
        AUTH[Auth Service<br/>Spring Boot + JWT]
        INFRA[Infrastructure Service<br/>Spring Boot + PostgreSQL]
        ALERT[Alert Service<br/>Spring Boot + Kafka]
        ANALYTICS[Analytics Service<br/>Spring Boot + Elasticsearch]
        PREDICTION[Prediction Service<br/>Python + FastAPI + ML]
    end

    subgraph "Event Streaming"
        KAFKA[Apache Kafka Cluster]
        KS[Kafka Streams]
        KC[Kafka Connect]
    end

    subgraph "Data Layer"
        PG[(PostgreSQL<br/>Primary + Replicas)]
        REDIS[(Redis Cluster<br/>Cache + Sessions)]
        ES[(Elasticsearch<br/>Search + Analytics)]
        S3[(AWS S3<br/>Data Lake)]
    end

    subgraph "ML Infrastructure"
        MLFLOW[MLflow<br/>Model Registry]
        NOTEBOOK[Jupyter<br/>Notebooks]
        FEATURE[Feature Store<br/>Redis + PostgreSQL]
    end

    subgraph "External"
        IOT[IoT Sensors<br/>MQTT / HTTP]
        WEATHER[Weather APIs]
        MAPS[Map Services]
    end

    WEB --> CDN
    MOB --> CDN
    CDN --> WAF
    WAF --> LB
    LB --> AG

    AG --> AUTH
    AG --> INFRA
    AG --> ALERT
    AG --> ANALYTICS
    AG --> PREDICTION

    IOT --> KAFKA
    WEATHER --> KAFKA
    INFRA --> KAFKA
    ALERT --> KAFKA
    PREDICTION --> KAFKA

    KAFKA --> KS
    KAFKA --> KC
    KS --> REDIS
    KC --> S3

    AUTH --> REDIS
    AUTH --> PG

    INFRA --> PG
    INFRA --> REDIS
    INFRA --> ES

    ALERT --> PG
    ALERT --> REDIS

    ANALYTICS --> ES
    ANALYTICS --> PG

    PREDICTION --> PG
    PREDICTION --> REDIS
    PREDICTION --> FEATURE
    PREDICTION --> MLFLOW

    NOTEBOOK --> S3
    NOTEBOOK --> MLFLOW

    WEB --> MAPS
```

---

## 2. Low-Level Architecture

```mermaid
graph TB
    subgraph "API Gateway Service"
        GC[Gateway Controller]
        GR[Gateway Router]
        GF[Gateway Filter<br/>Auth/JWT/RateLimit]
        GLB[Load Balancer<br/>Round Robin]
    end

    subgraph "Auth Service"
        AC[Auth Controller]
        AS[Auth Service]
        AR[Auth Repository]
        AJ[JWT Provider]
        ARL[Redis Session Store]
    end

    subgraph "Infrastructure Service"
        IC[Infra Controller]
        IS[Infra Service]
        IR[Infra Repository]
        IGEO[Geo Service<br/>PostGIS]
        ICSV[CSV/GeoJSON Parser]
    end

    subgraph "Alert Service"
        ALC[Alert Controller]
        ALS[Alert Service]
        ALR[Alert Repository]
        ALN[Notification Service<br/>Email/SMS/Webhook]
        ALE[Alert Engine<br/>Rules/ML-based]
    end

    subgraph "Analytics Service"
        ANC[Analytics Controller]
        ANS[Analytics Service]
        ANQ[ES Query Builder]
        ANA[Aggregation Engine]
        ANR[Report Generator]
    end

    subgraph "Prediction Service (Python)"
        PC[FastAPI Controller]
        PI[Inference Engine<br/>ONNX Runtime]
        PP[Prediction Pipeline<br/>Pre/Post Processing]
        PM[Model Manager<br/>Version/A/B]
        PD[Drift Detector]
        PS[SHAP Explainer]
    end

    subgraph "Kafka Streams Processing"
        KS1[Sensor Data Normalizer]
        KS2[Anomaly Detector<br/>Statistical Rules]
        KS3[Feature Aggregator<br/>Windowed]
        KS4[Alert Enricher]
    end

    subgraph "Data Access Layer"
        DAL1[JPA / Hibernate]
        DAL2[Redis Template]
        DAL3[ES Client]
        DAL4[SQLAlchemy / Async]
    end

    GC --> GF
    GF --> GLB
    GLB --> AC
    GLB --> IC
    GLB --> ALC
    GLB --> ANC
    GLB --> PC

    AC --> AS
    AS --> AJ
    AS --> AR
    AR --> PG
    AS --> ARL

    IC --> IS
    IS --> IR
    IR --> DAL1
    IS --> IGEO
    IGEO --> PG
    IC --> ICSV

    ALC --> ALS
    ALS --> ALR
    ALS --> ALE
    ALE --> ALN
    ALN --> KAFKA

    ANC --> ANS
    ANS --> ANQ
    ANQ --> ES
    ANS --> ANA
    ANS --> ANR

    PC --> PP
    PP --> PI
    PI --> PM
    PM --> MLFLOW
    PP --> PD
    PP --> PS

    KAFKA --> KS1
    KS1 --> KS2
    KS2 --> KS3
    KS3 --> KS4
    KS4 --> REDIS

    DAL1 --> PG
    DAL2 --> REDIS
    DAL3 --> ES
```

---

## 3. Component Diagram

```mermaid
graph LR
    subgraph "Frontend (Angular)"
        APP[App Module]
        AUTHM[Auth Module]
        DASH[Dashboard Module]
        MAP[Map Module]
        ALERTM[Alerts Module]
        ANALYTICSM[Analytics Module]
        PRED[Predictions Module]
        SHARED[Shared Module]
    end

    subgraph "Shared Components"
        NAV[Navigation]
        CHART[Chart Components<br/>D3.js/Chart.js]
        TABLE[Data Tables]
        FORM[Forms]
        THEME[Theme Service<br/>Dark/Light]
    end

    subgraph "Services"
        HTTP[HTTP Client<br/>Interceptors]
        WS[WebSocket Service<br/>Real-time]
        AUTHS[Auth Service]
        LOCAL[Local Storage]
    end

    APP --> AUTHM
    APP --> DASH
    APP --> MAP
    APP --> ALERTM
    APP --> ANALYTICSM
    APP --> PRED
    APP --> SHARED

    SHARED --> NAV
    SHARED --> CHART
    SHARED --> TABLE
    SHARED --> FORM
    SHARED --> THEME

    DASH --> HTTP
    DASH --> WS
    ALERTM --> WS
    PRED --> WS
    AUTHM --> AUTHS
    AUTHM --> LOCAL
    AUTHS --> HTTP
```

---

## 4. Sequence Diagrams

### 4.1: Sensor Data Ingestion Flow

```mermaid
sequenceDiagram
    participant IOT as IoT Sensor
    participant KAFKA as Kafka
    participant INFRA as Infrastructure Service
    participant KS as Kafka Streams
    participant REDIS as Redis
    participant PRED as Prediction Service
    participant ALERT as Alert Service
    participant PG as PostgreSQL

    IOT->>KAFKA: Publish sensor reading (MQTT)
    KAFKA->>INFRA: Consume raw data
    INFRA->>PG: Store normalized reading
    INFRA->>KAFKA: Publish validated event
    KAFKA->>KS: Stream processing
    KS->>KS: Window aggregation (5min)
    KS->>REDIS: Store aggregated features
    KS->>KAFKA: Publish feature vector
    KAFKA->>PRED: Consume features
    PRED->>PRED: Run ML inference
    alt Anomaly Detected
        PRED->>KAFKA: Publish prediction event
        KAFKA->>ALERT: Consume prediction
        ALERT->>ALERT: Severity classification
        ALERT->>PG: Store alert
        ALERT->>REDIS: Cache active alert
        ALERT->>IOT: Send notification
    end
```

### 4.2: User Authentication Flow

```mermaid
sequenceDiagram
    participant UI as Angular App
    participant AG as API Gateway
    participant AUTH as Auth Service
    participant REDIS as Redis
    participant PG as PostgreSQL

    UI->>AG: POST /api/auth/login
    AG->>AUTH: Forward request
    AUTH->>PG: Validate credentials
    PG-->>AUTH: User record
    AUTH->>AUTH: Generate JWT
    AUTH->>REDIS: Store session
    AUTH-->>AG: JWT Token + User
    AG-->>UI: 200 OK + Token
    UI->>UI: Store token in localStorage

    Note over UI,PG: Subsequent Requests
    UI->>AG: GET /api/infrastructure<br/>Authorization: Bearer JWT
    AG->>AG: Validate JWT
    AG->>REDIS: Check session
    REDIS-->>AG: Session valid
    AG->>PG: Fetch data
    PG-->>AG: Results
    AG-->>UI: 200 OK + Data
```

### 4.3: Prediction Request Flow

```mermaid
sequenceDiagram
    participant UI as Angular Dashboard
    participant AG as API Gateway
    participant INFRA as Infrastructure Service
    participant PRED as Prediction Service
    participant REDIS as Redis
    participant PG as PostgreSQL
    participant MLFLOW as MLflow

    UI->>AG: GET /api/predictions/{assetId}
    AG->>PRED: Forward request
    PRED->>PG: Fetch asset history
    PRED->>REDIS: Fetch real-time features
    PRED->>PG: Fetch weather data
    PRED->>PRED: Feature engineering
    PRED->>MLFLOW: Load model (version)
    MLFLOW-->>PRED: ONNX model
    PRED->>PRED: Run inference
    PRED->>PRED: SHAP explanation
    PRED->>PG: Store prediction
    PRED->>REDIS: Cache result
    PRED-->>AG: Prediction + Explanation
    AG-->>UI: 200 OK + Result
```

### 4.4: Real-Time Alert Flow

```mermaid
sequenceDiagram
    participant WS as WebSocket
    participant ALERT as Alert Service
    participant KAFKA as Kafka
    participant REDIS as Redis
    participant UI as Angular Dashboard

    UI->>WS: Connect /ws/alerts
    WS->>REDIS: Subscribe to alert channel

    KAFKA->>ALERT: New prediction event
    ALERT->>ALERT: Classify severity
    ALERT->>REDIS: Publish to channel
    ALERT->>REDIS: Store alert state

    REDIS-->>WS: Push alert
    WS-->>UI: Real-time alert notification
    UI->>UI: Show toast + Play sound

    UI->>WS: Acknowledge alert
    WS->>ALERT: Update status
    ALERT->>REDIS: Update state
    ALERT->>PG: Persist acknowledgment
```

---

## 5. Deployment Diagram

```mermaid
graph TB
    subgraph "AWS Cloud"
        subgraph "VPC"
            subgraph "Public Subnets"
                ALB[Application Load Balancer]
                NAT[NAT Gateway]
            end

            subgraph "Private Subnets - Application"
                subgraph "EKS Cluster"
                    subgraph "API Gateway Pod"
                        AG_POD[Gateway Service]
                    end

                    subgraph "Auth Service Pods"
                        AUTH_POD1[Auth Pod 1]
                        AUTH_POD2[Auth Pod 2]
                    end

                    subgraph "Infra Service Pods"
                        INFRA_POD1[Infra Pod 1]
                        INFRA_POD2[Infra Pod 2]
                    end

                    subgraph "Alert Service Pods"
                        ALERT_POD1[Alert Pod 1]
                        ALERT_POD2[Alert Pod 2]
                    end

                    subgraph "Analytics Pods"
                        ANAL_POD1[Analytics Pod 1]
                        ANAL_POD2[Analytics Pod 2]
                    end

                    subgraph "Prediction Pods"
                        PRED_POD1[Prediction Pod 1]
                        PRED_POD2[Prediction Pod 2]
                        PRED_POD3[Prediction Pod 3]
                    end

                    subgraph "Kafka Cluster"
                        KAFKA_POD1[Kafka Broker 1]
                        KAFKA_POD2[Kafka Broker 2]
                        KAFKA_POD3[Kafka Broker 3]
                        ZK[Zookeeper]
                    end
                end
            end

            subgraph "Private Subnets - Data"
                subgraph "PostgreSQL"
                    PG_PRIMARY[(Primary)]
                    PG_REPLICA1[(Replica 1)]
                    PG_REPLICA2[(Replica 2)]
                end

                subgraph "Redis"
                    REDIS_MASTER[(Master)]
                    REDIS_SLAVE1[(Slave 1)]
                    REDIS_SLAVE2[(Slave 2)]
                end

                subgraph "Elasticsearch"
                    ES_NODE1[Node 1]
                    ES_NODE2[Node 2]
                    ES_NODE3[Node 3]
                end
            end
        end

        subgraph "S3"
            S3_DATA[Data Lake]
            S3_MODELS[Model Artifacts]
            S3_FRONTEND[Static Frontend]
        end

        subgraph "CloudFront"
            CF[CDN Distribution]
        end

        subgraph "Route53"
            DNS[DNS Records]
        end

        subgraph "CloudWatch"
            CW[Monitoring & Logs]
        end
    end

    DNS --> CF
    CF --> S3_FRONTEND
    DNS --> ALB

    ALB --> AG_POD
    AG_POD --> AUTH_POD1
    AG_POD --> INFRA_POD1
    AG_POD --> ALERT_POD1
    AG_POD --> ANAL_POD1
    AG_POD --> PRED_POD1

    AUTH_POD1 --> REDIS_MASTER
    AUTH_POD1 --> PG_PRIMARY
    INFRA_POD1 --> PG_PRIMARY
    INFRA_POD1 --> REDIS_MASTER
    INFRA_POD1 --> ES_NODE1
    ALERT_POD1 --> REDIS_MASTER
    ALERT_POD1 --> PG_PRIMARY
    ALERT_POD1 --> KAFKA_POD1
    ANAL_POD1 --> ES_NODE1
    ANAL_POD1 --> PG_REPLICA1
    PRED_POD1 --> PG_REPLICA1
    PRED_POD1 --> REDIS_MASTER
    PRED_POD1 --> S3_MODELS

    KAFKA_POD1 --> ZK
    INFRA_POD1 --> KAFKA_POD1
    ALERT_POD1 --> KAFKA_POD1
    PRED_POD1 --> KAFKA_POD1

    NAT --> CW
```

---

## 6. Database Diagram

### 6.1: PostgreSQL Schema

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : has
    USERS ||--o{ ASSETS : creates
    USERS ||--o{ ALERTS : acknowledges
    ASSET_TYPES ||--o{ ASSETS : categorizes
    ASSETS ||--o{ SENSORS : has
    ASSETS ||--o{ ASSET_RELATIONSHIPS : relates_to
    SENSORS ||--o{ SENSOR_READINGS : generates
    SENSORS ||--o{ SENSOR_TYPES : typed_as
    ASSETS ||--o{ PREDICTIONS : predicted_for
    PREDICTIONS ||--o{ ALERTS : generates
    ALERTS ||--o{ ALERT_NOTIFICATIONS : sends
    MAINTENANCE_RECORDS ||--o{ ASSETS : services
    WEATHER_DATA ||--o{ ASSETS : affects

    USERS {
        uuid id PK
        string email UK
        string password_hash
        string first_name
        string last_name
        string phone
        boolean is_active
        timestamp created_at
        timestamp updated_at
        timestamp last_login
    }

    ROLES {
        uuid id PK
        string name UK
        string description
        json permissions
        timestamp created_at
    }

    USER_ROLES {
        uuid user_id FK
        uuid role_id FK
        timestamp assigned_at
    }

    ASSET_TYPES {
        uuid id PK
        string name UK
        string category
        string icon
        json schema
        timestamp created_at
    }

    ASSETS {
        uuid id PK
        string name
        string asset_code UK
        uuid asset_type_id FK
        uuid created_by FK
        geometry location
        json boundary
        int health_score
        string status
        date installation_date
        date last_maintenance
        string material
        int age_years
        json metadata
        timestamp created_at
        timestamp updated_at
    }

    ASSET_RELATIONSHIPS {
        uuid id PK
        uuid parent_asset_id FK
        uuid child_asset_id FK
        string relationship_type
        timestamp created_at
    }

    SENSORS {
        uuid id PK
        string sensor_code UK
        uuid asset_id FK
        uuid sensor_type_id FK
        string name
        string protocol
        string mqtt_topic
        string http_endpoint
        json config
        point location
        boolean is_active
        timestamp last_reading_at
        timestamp created_at
    }

    SENSOR_TYPES {
        uuid id PK
        string name UK
        string unit
        string data_type
        json schema
        float min_value
        float max_value
        timestamp created_at
    }

    SENSOR_READINGS {
        bigint id PK
        uuid sensor_id FK
        float value
        json raw_data
        timestamp recorded_at
        timestamp ingested_at
    }

    PREDICTIONS {
        uuid id PK
        uuid asset_id FK
        string asset_type
        string prediction_type
        float probability
        float confidence_lower
        float confidence_upper
        timestamp predicted_failure_window_start
        timestamp predicted_failure_window_end
        json contributing_factors
        json shap_values
        string model_version
        string status
        boolean was_accurate
        timestamp created_at
    }

    ALERTS {
        uuid id PK
        uuid prediction_id FK
        uuid asset_id FK
        string severity
        string title
        string description
        string status
        uuid acknowledged_by FK
        timestamp acknowledged_at
        string resolution_notes
        timestamp resolved_at
        timestamp created_at
    }

    ALERT_NOTIFICATIONS {
        uuid id PK
        uuid alert_id FK
        string channel
        string recipient
        string status
        timestamp sent_at
        timestamp created_at
    }

    MAINTENANCE_RECORDS {
        uuid id PK
        uuid asset_id FK
        string type
        string description
        float cost
        timestamp scheduled_date
        timestamp completed_date
        string status
        string performed_by
        json metadata
        timestamp created_at
    }

    WEATHER_DATA {
        bigint id PK
        geometry location
        float temperature
        float humidity
        float precipitation
        float wind_speed
        float pressure
        string conditions
        timestamp recorded_at
    }
```

### 6.2: Redis Key Structure

```mermaid
erDiagram
    SESSIONS {
        string key "session:{jwt_id}"
        hash value "user_id, role, expires"
        int TTL 3600
    }

    SENSOR_LATEST {
        string key "sensor:latest:{sensor_id}"
        hash value "value, timestamp, status"
        int TTL 86400
    }

    ASSET_HEALTH {
        string key "asset:health:{asset_id}"
        hash value "score, last_updated, factors"
        int TTL 3600
    }

    PREDICTION_CACHE {
        string key "prediction:{asset_id}:{window}"
        string value "json_prediction"
        int TTL 1800
    }

    ACTIVE_ALERTS {
        string key "alerts:active"
        sortedset value "alert_id -> score"
        int TTL 0
    }

    FEATURE_VECTOR {
        string key "features:{asset_id}:{window}"
        hash value "aggregated_features"
        int TTL 7200
    }

    RATE_LIMIT {
        string key "ratelimit:{client_id}"
        counter value "requests_count"
        int TTL 60
    }
```

---

## 7. Kafka Event Flow Diagram

### 7.1: Topic Architecture

```mermaid
graph LR
    subgraph "Producers"
        IOT[IoT Sensors]
        INFRA[Infrastructure Service]
        PRED[Prediction Service]
        WEATHER[Weather API]
    end

    subgraph "Kafka Topics"
        T1[raw.sensor.data<br/>Partitions: 12, RF: 3]
        T2[validated.sensor.data<br/>Partitions: 12, RF: 3]
        T3[aggregated.features<br/>Partitions: 6, RF: 3]
        T4[infrastructure.events<br/>Partitions: 6, RF: 3]
        T5[predictions.generated<br/>Partitions: 6, RF: 3]
        T6[alerts.created<br/>Partitions: 6, RF: 3]
        T7[weather.updates<br/>Partitions: 3, RF: 3]
        T8[notifications.send<br/>Partitions: 6, RF: 3]
        T9[audit.events<br/>Partitions: 3, RF: 3]
    end

    subgraph "Consumers"
        KS1[Kafka Streams<br/>Normalizer]
        KS2[Kafka Streams<br/>Aggregator]
        KS3[Kafka Streams<br/>Enricher]
        PRED_CONS[Prediction Service]
        ALERT[Alert Service]
        ANALYTICS[Analytics Service]
        NOTIF[Notification Service]
        AUDIT[Audit Logger]
    end

    IOT --> T1
    WEATHER --> T7

    T1 --> KS1
    KS1 --> T2

    T2 --> KS2
    T7 --> KS2
    KS2 --> T3

    T3 --> PRED_CONS
    PRED_CONS --> T5

    T5 --> KS3
    KS3 --> T6

    T6 --> ALERT
    T6 --> NOTIF

    INFRA --> T4
    T4 --> ANALYTICS

    ALERT --> T8
    NOTIF --> T8

    T1 --> AUDIT
    T9 --> AUDIT
```

### 7.2: Event Schema Examples

```mermaid
erDiagram
    SENSOR_DATA_EVENT {
        string event_id PK
        timestamp event_time
        string sensor_id
        string asset_id
        string sensor_type
        float value
        string unit
        json raw_payload
        point geohash
    }

    FEATURE_VECTOR_EVENT {
        string event_id PK
        timestamp window_start
        timestamp window_end
        string asset_id
        string asset_type
        json statistical_features
        json temporal_features
        json weather_features
        json correlation_features
        float anomaly_score
    }

    PREDICTION_EVENT {
        string event_id PK
        timestamp prediction_time
        string asset_id
        string asset_type
        float failure_probability
        timestamp predicted_window_start
        timestamp predicted_window_end
        json contributing_factors
        string model_version
        float inference_latency_ms
    }

    ALERT_EVENT {
        string event_id PK
        timestamp alert_time
        string prediction_id
        string asset_id
        string severity
        string alert_type
        string title
        string description
        json recommended_actions
        float confidence_score
    }
```

---

## 8. API Gateway Routes

```mermaid
graph LR
    AG[API Gateway<br/>Port 8080]

    AG -->|/api/v1/auth/**| AUTH[Auth Service<br/>Port 8081]
    AG -->|/api/v1/infrastructure/**| INFRA[Infra Service<br/>Port 8082]
    AG -->|/api/v1/alerts/**| ALERT[Alert Service<br/>Port 8083]
    AG -->|/api/v1/analytics/**| ANALYTICS[Analytics Service<br/>Port 8084]
    AG -->|/api/v1/predictions/**| PRED[Prediction Service<br/>Port 8085]
    AG -->|/api/v1/health| HEALTH[Health Check]
    AG -->|/ws/**| WS[WebSocket Handler]
```

---

## 9. Technology Stack Summary

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| Frontend | Angular | 17+ | SPA Web Application |
| Frontend | TypeScript | 5.2+ | Type Safety |
| Frontend | Tailwind CSS | 3.4+ | Styling |
| Frontend | RxJS | 7.8+ | Reactive Programming |
| Frontend | Leaflet | 1.9+ | Interactive Maps |
| API Gateway | Spring Cloud Gateway | 4.x | Routing, Filters |
| Auth Service | Spring Boot | 3.2+ | Authentication |
| Infra Service | Spring Boot | 3.2+ | Asset Management |
| Alert Service | Spring Boot | 3.2+ | Alert Processing |
| Analytics | Spring Boot | 3.2+ | Reporting |
| Prediction | FastAPI | 0.104+ | ML Inference |
| Prediction | ONNX Runtime | 1.16+ | Model Serving |
| Prediction | scikit-learn | 1.3+ | ML Pipeline |
| Messaging | Apache Kafka | 3.6+ | Event Streaming |
| Database | PostgreSQL | 16+ | Primary Data Store |
| Database | PostGIS | 3.4+ | Geospatial |
| Cache | Redis | 7.2+ | Caching, Sessions |
| Search | Elasticsearch | 8.11+ | Search, Analytics |
| ML Ops | MLflow | 2.9+ | Model Registry |
| Container | Docker | 24+ | Containerization |
| Orchestration | Kubernetes | 1.28+ | Container Orchestration |
| CI/CD | GitHub Actions | - | Build, Test, Deploy |
| Cloud | AWS | - | Infrastructure |
| Monitoring | Prometheus + Grafana | - | Observability |
| Tracing | OpenTelemetry | - | Distributed Tracing |

---

## 10. Architectural Decisions (ADRs)

### ADR-001: Microservices over Monolith
**Decision**: Use microservices architecture
**Rationale**: Independent scaling of prediction service (GPU-heavy) vs. other services; team autonomy; technology diversity (Java + Python)
**Trade-offs**: Operational complexity mitigated by Kubernetes and Istio

### ADR-002: Event-Driven Architecture with Kafka
**Decision**: Use Kafka for all inter-service communication
**Rationale**: Decouples producers from consumers; enables replay; handles backpressure; supports stream processing
**Trade-offs**: Additional infrastructure; eventual consistency acceptable for use case

### ADR-003: Separate Prediction Service in Python
**Decision**: Build ML inference service in Python/FastAPI, separate from Java services
**Rationale**: Python ecosystem for ML (scikit-learn, ONNX); different scaling characteristics; independent deployment cycle
**Trade-offs**: Polyglot complexity; separate CI/CD pipeline

### ADR-004: API Gateway Pattern
**Decision**: Use Spring Cloud Gateway as single entry point
**Rationale**: Centralized cross-cutting concerns (auth, rate limiting, CORS); client simplicity; load balancing
**Trade-offs**: Single point of failure (mitigated by clustering)

### ADR-005: PostgreSQL + PostGIS for Geospatial
**Decision**: Use PostgreSQL with PostGIS extension over specialized geospatial DB
**Rationale**: ACID compliance; familiar technology; PostGIS handles geospatial queries; single database for relational + spatial
**Trade-offs**: Horizontal scaling limits (mitigated by read replicas)

### ADR-006: Redis for Caching and Sessions
**Decision**: Use Redis for multiple purposes (cache, sessions, real-time features)
**Rationale**: Sub-millisecond latency; supports data structures needed; cluster mode for high availability
**Trade-offs**: Memory cost; cache invalidation complexity

### ADR-007: ONNX Runtime for Model Serving
**Decision**: Convert models to ONNX format and use ONNX Runtime
**Rationale**: Framework-agnostic; optimized inference; language interoperability (Python training, any language inference)
**Trade-offs**: Some advanced features may not convert

### ADR-008: Angular over React
**Decision**: Use Angular for frontend
**Rationale**: Enterprise-grade framework; TypeScript-first; dependency injection; strong tooling; suits complex dashboard applications
**Trade-offs**: Steeper learning curve; more opinionated
