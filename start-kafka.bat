@echo off
echo Starting Kafka and related services...
docker-compose up -d

echo Waiting for Kafka to be ready...
timeout /t 30

echo Creating Kafka topics...
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic inventory-changes
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic sales-events
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic replenishment-events

echo Listing Kafka topics...
docker exec kafka kafka-topics --list --bootstrap-server kafka:9092

echo Kafka is ready! You can access Kafka UI at http://localhost:8080
echo Press any key to exit...
pause > nul
