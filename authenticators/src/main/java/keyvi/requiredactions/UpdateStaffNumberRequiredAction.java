package keyvi.requiredactions;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.sessions.AuthenticationSessionModel;

public class UpdateStaffNumberRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "usn-required-action";
    private static final Logger LOG = Logger.getLogger(UpdateStaffNumberRequiredAction.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
       //
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LOG.warnf("The logger works");
        if (isSocialLogin(context.getAuthenticationSession())) {
            Response challenge = context.form().createForm("required-action.ftl");
            context.challenge(challenge);
        } else {
            context.ignore();
        }
    }

    @Override
    public void processAction(RequiredActionContext context) {
    }

    @Override
    public void close() {
    }

    private boolean isSocialLogin(AuthenticationSessionModel authSession) {
        String identityProvider = authSession.getAuthNote("identity_provider");
        LOG.warnf("Auth note result: %s", identityProvider);
        return identityProvider != null && !identityProvider.equals("keycloak");
    }
}
