#!/usr/bin/env bash

echo "Running setup-keycloak.sh..."

# Ensure the script exits if any command fails
set -e

##########
#
# This script sets up Keycloak using the Keycloak Config CLI and Keycloak Admin CLI.
#
#########

# Trap any errors to exit the script
trap 'exit' ERR

echo " "
echo " "
echo "--------------- KEYCLOAK SETUP STARTING ----------------"
echo " "
echo " "

# Set BASEDIR to the directory where this script is located
BASEDIR=$(dirname "$0")

runKeycloakConfigCli() {
  echo ""
  echo "--- Running Keycloak Config CLI"
  echo ""

  # Run keycloak-config-cli with appropriate parameters
  java -jar "${BASEDIR}/keycloak-config-cli.jar" \
      --keycloak.url=https://yivisso.com:8443 \
      --keycloak.ssl-verify=false \
      --keycloak.user="${KEYCLOAK_ADMIN}" \
      --keycloak.password="${KEYCLOAK_ADMIN_PASSWORD}" \
      --keycloak.availability-check.enabled=true \
      --keycloak.availability-check.timeout=300s \
      --import.var-substitution.enabled=true \
      --import.managed.client=no-delete \
      --import.managed.client-scope=no-delete \
      --import.managed.client-scope-mapping=no-delete \
      --import.files.locations="${BASEDIR}/realm-config.json"
}

runKeycloakCli() {
  if [ "$KCADM" == "" ]; then
      KCADM="/opt/keycloak/bin/kcadm.sh"
      echo "Using $KCADM as the admin CLI."
  fi

  # Log in to the admin console
  ${KCADM} config credentials --server https://yivisso.com:8443 --user "${KEYCLOAK_ADMIN}" --password "${KEYCLOAK_ADMIN_PASSWORD}" --realm master

  # Project specific configurations
  # Uncomment and update the following lines as needed for your setup
  # source "${BASEDIR}/keycloak-cli-helpers.sh"
  # source "${BASEDIR}/keycloak-cli-custom.sh"
}

echo " "
echo "----------------- KEYCLOAK CONFIG CLI ------------------"
echo " "
runKeycloakConfigCli

echo " "
echo "----------------- KEYCLOAK CLI ------------------"
echo " "
runKeycloakCli

echo " "
echo "--------------- KEYCLOAK SETUP FINISHED ----------------"
echo " "
