package keyvi.requiredactions;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class YiviRequiredActionFactory implements RequiredActionFactory {

    private static final Logger log = Logger.getLogger(YiviRequiredActionFactory.class);

    private static final YiviRequiredAction SINGLETON = new YiviRequiredAction();

    @Override
    public String getDisplayText() {
        return "Update Staff Number";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return YiviRequiredAction.PROVIDER_ID;
    }
}
