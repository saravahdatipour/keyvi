FROM quay.io/keycloak/keycloak:24.0.4

ARG AUTHENTICATOR_JAR=authenticators/target/authenticators.jar

USER root

# Copy the authenticator JAR
COPY ${AUTHENTICATOR_JAR} /opt/keycloak/providers/

# Theme customization
ARG CODELENS_THEME_BASE_DIR=/opt/keycloak/themes/keyvi
ARG CODELENS_THEME_LOCAL_ROOT_DIR=themes/keyvi

RUN mkdir -p ${CODELENS_THEME_BASE_DIR} \
    && chown -R root:root ${CODELENS_THEME_BASE_DIR}

COPY ${CODELENS_THEME_LOCAL_ROOT_DIR} ${CODELENS_THEME_BASE_DIR}

# Copy the Keycloak Config CLI JAR, JSON configuration, setup script, and entrypoint script
COPY keycloak-config-cli.jar /opt/keycloak/
COPY realm-config.json /opt/keycloak/
COPY setup-keycloak.sh /opt/keycloak/
COPY entrypoint.sh /opt/keycloak/

# Copy Let's Encrypt certificates into the Docker image
COPY yivisso.com /opt/keycloak/certs

RUN chmod 644 /opt/keycloak/certs/*

RUN chmod +x /opt/keycloak/setup-keycloak.sh /opt/keycloak/entrypoint.sh

ENTRYPOINT ["/opt/keycloak/entrypoint.sh"]
