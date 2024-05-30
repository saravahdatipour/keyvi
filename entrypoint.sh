#!/bin/bash

# Start Keycloak server in the background with SSL configuration
/opt/keycloak/bin/kc.sh start --hostname=${KC_HOSTNAME} --https-certificate-file=${KC_HTTPS_CERTIFICATE_FILE} --https-certificate-key-file=${KC_HTTPS_CERTIFICATE_KEY_FILE} --hostname-strict=${KC_HOSTNAME_STRICT} --hostname-strict-https=${KC_HOSTNAME_STRICT_HTTPS} &

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
