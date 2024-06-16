package keyvi.requiredactions;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

public class UpdateStaffNumberRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "usn-required-action";
    private static final Logger LOG = Logger.getLogger(UpdateStaffNumberRequiredAction.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        UserModel user = context.getUser();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        if (isSocialLogin(authSession)) {
            // Add your custom required action
            user.addRequiredAction(PROVIDER_ID);
            LOG.warnf("Social login was part of the session");
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
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
        String identityProvider = authSession.getAuthenticatedUser().getFederationLink();
        return identityProvider != null && !identityProvider.isEmpty();
    }
}
