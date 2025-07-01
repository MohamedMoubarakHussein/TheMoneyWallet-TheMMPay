#!/bin/bash

# Function to wait for a service to be UP
wait_for_service() {
  local name=$1
  local port=$2

  echo "⏳ Waiting for $name to be UP on port $port..."

  until curl -s "http://localhost:$port/actuator/health" | grep -q '"status":"UP"'; do
    sleep 2
  done

  echo "✅ $name is UP."
}

# Start Service A
echo "🚀 Starting config-service... 1/10"
cd config-service
mvn spring-boot:run &
PID_A=$!
cd ..
wait_for_service "config-service" 8109

# Start Service B
echo "🚀 Starting discovery-service...  2/10"
cd discovery-service
mvn spring-boot:run &
PID_B=$!
cd ..
wait_for_service "discovery-service" 8888

# Start Service C
echo "🚀 Starting gateway...  3/10"
cd gateway
mvn spring-boot:run &
PID_A=$!
cd ..
wait_for_service "gateway" 8080

# Start Service D
echo "🚀 Starting authentication-service...  4/10"
cd authentication-service
mvn spring-boot:run &
PID_D=$!
cd ..
wait_for_service "authentication-service" 8099



# Start Service E
echo "🚀 Starting dashboard-service...  5/10"
cd dashboard-service
mvn spring-boot:run &
PID_E=$!
cd ..
wait_for_service "dashboard-service" 8095




# Start Service F
echo "🚀 Starting user-managment-service...  6/10"
cd user-managment-service
mvn spring-boot:run &
PID_F=$!
cd ..
wait_for_service "user-managment-service" 8089



# Start Service G
echo "🚀 Starting wallet-service...  7/10"
cd wallet-service
mvn spring-boot:run &
PID_G=$!
cd ..
wait_for_service "wallet-service" 8090

# Start Service H
echo "🚀 Starting transaction-service...   8/10"
cd transaction-service
mvn spring-boot:run &
PID_H=$!
cd ..
wait_for_service "transaction-service" 8196


# Start Service I
echo "🚀 Starting notification-service...  9/10"
cd notification-service
mvn spring-boot:run &
PID_I=$!
cd ..
wait_for_service "notification-service" 8194


# Start Service J
echo "🚀 Starting history-service...   10/10"
cd history-service
mvn spring-boot:run &
PID_J=$!
cd ..
wait_for_service "history-service" 8093

echo "🎉 All services are UP and running. 10/10"

# Optional: Wait to keep terminal alive or trap exits
wait $PID_A $PID_B $PID_C $PID_D $PID_E $PID_F $PID_G $PID_H $PID_I $PID_J

