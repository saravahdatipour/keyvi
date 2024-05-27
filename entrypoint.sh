#!/bin/bash

# Start Keycloak server in the background
/opt/keycloak/bin/kc.sh start-dev --hostname-strict=false &

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to start..."
while ! (echo > /dev/tcp/localhost/8080) &> /dev/null; do
  echo "Waiting for Keycloak server to be ready..."
  sleep 5
done

# Run the setup script
/opt/keycloak/setup-keycloak.sh

# Keep the container running
tail -f /dev/null
