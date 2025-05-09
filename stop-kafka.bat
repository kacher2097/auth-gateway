@echo off
echo Stopping Kafka and related services...
docker-compose down

echo Services stopped.
echo Press any key to exit...
pause > nul
