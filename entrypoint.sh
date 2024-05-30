#!/bin/bash

# Start Keycloak server in the background with SSL configuration
/opt/keycloak/bin/kc.sh start-dev --hostname-strict=false --https-certificate-file=/etc/letsencrypt/live/yivisso.com/fullchain.pem --https-certificate-key-file=/etc/letsencrypt/live/yivisso.com/privkey.pem &

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to start..."
while ! (echo > /dev/tcp/localhost/8443) &> /dev/null; do
  echo "Waiting for Keycloak server to be ready..."
  sleep 5
done

# Run the setup script
/opt/keycloak/setup-keycloak.sh

# Keep the container running
tail -f /dev/null
