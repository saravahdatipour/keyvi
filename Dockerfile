FROM quay.io/keycloak/keycloak:24.0.4

ARG AUTHENTICATOR_JAR=authenticators/target/authenticators.jar

USER root

# Copy the authenticator JAR
COPY ${AUTHENTICATOR_JAR} /opt/keycloak/providers/

# Theme customization region
ARG CODELENS_THEME_BASE_DIR=/opt/keycloak/themes/keyvi
ARG CODELENS_THEME_LOCAL_ROOT_DIR=themes/keyvi

RUN mkdir -p ${CODELENS_THEME_BASE_DIR} \
    && chown -R root:root ${CODELENS_THEME_BASE_DIR}

COPY ${CODELENS_THEME_LOCAL_ROOT_DIR} ${CODELENS_THEME_BASE_DIR}

# Copy the Keycloak Config CLI JAR, JSON configuration, setup script, entrypoint script, and SSL certificates
COPY keycloak-config-cli.jar /opt/keycloak/
COPY realm-config.json /opt/keycloak/
COPY setup-keycloak.sh /opt/keycloak/
COPY entrypoint.sh /opt/keycloak/
COPY certfile.pem /etc/x509/https/tls.crt
COPY keyfile.pem /etc/x509/https/tls.key

RUN chmod +x /opt/keycloak/setup-keycloak.sh /opt/keycloak/entrypoint.sh

ENTRYPOINT ["/opt/keycloak/entrypoint.sh"]