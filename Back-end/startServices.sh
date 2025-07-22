#!/bin/bash

ACTION=$1  # Accept 'up' or 'down'

SERVICES=(
  "config-service:8109"
  "discovery-service:8888"
  "gateway:8080"
  "authentication-service:8099"
  "dashboard-service:8095"
  "user-managment-service:8089"
  "wallet-service:8090"
  "transaction-service:8196"
  "notification-service:8194"
  "history-service:8093"
)

wait_for_service() {
  local name=$1
  local port=$2
  echo "⏳ Waiting for $name on port $port..."
  until curl -s "http://localhost:$port/actuator/health" | grep -q '"status":"UP"'; do
    sleep 2
  done
  echo "✅ $name is UP."
}

start_services() {
  echo "🔧 Building sharedUtilities..."
  cd sharedUtilities && mvn clean install && cd ..

  for i in "${!SERVICES[@]}"; do
    IFS=":" read -r name port <<< "${SERVICES[i]}"
    echo "🚀 Starting $name... $((i+1))/${#SERVICES[@]}"
    cd "$name"
    mvn spring-boot:run &
    cd ..
    wait_for_service "$name" "$port"
  done

  echo "🎉 All services are UP."
}

shutdown_services() {
  echo "🛑 Killing services by port..."
  for entry in "${SERVICES[@]}"; do
    IFS=":" read -r name port <<< "$entry"
    pid=$(lsof -ti tcp:$port)
    if [[ -n "$pid" ]]; then
      echo "Killing $name on port $port (PID $pid)..."
      kill "$pid"
      sleep 2
      if kill -0 "$pid" 2>/dev/null; then
        echo "Force killing $pid..."
        kill -9 "$pid"
      fi
      echo "✅ $name stopped."
    else
      echo "⚠️ No process found on port $port for $name."
    fi
  done
  echo "🛑 All services stopped."
}

case "$ACTION" in
  up)
    start_services
    ;;
  down)
    shutdown_services
    ;;
  *)
    echo "Usage: $0 {up|down}"
    exit 1
    ;;
esac
