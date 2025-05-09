#!/bin/bash

echo "Waiting for Kafka to be ready..."
sleep 30

echo "Creating Kafka topics..."
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic inventory-changes
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic sales-events
docker exec kafka kafka-topics --create --if-not-exists --bootstrap-server kafka:9092 --partitions 3 --replication-factor 1 --topic replenishment-events

echo "Listing Kafka topics..."
docker exec kafka kafka-topics --list --bootstrap-server kafka:9092

echo "Topics created successfully!"
