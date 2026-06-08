.PHONY: help build up down logs test clean

help:
	@echo "UrbanPulse AI - Available Commands:"
	@echo "  make build    - Build all Docker images"
	@echo "  make up       - Start all services"
	@echo "  make down     - Stop all services"
	@echo "  make logs     - View logs"
	@echo "  make test     - Run all tests"
	@echo "  make clean    - Remove all containers and volumes"

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

test:
	cd backend/auth-service && mvn test
	cd frontend && npm test -- --watch=false
	cd ml-services/prediction-service && pytest

clean:
	docker-compose down -v
	docker system prune -f

k8s-deploy:
	kubectl apply -f infrastructure/kubernetes/base/
	kubectl apply -f infrastructure/kubernetes/

k8s-delete:
	kubectl delete -f infrastructure/kubernetes/
