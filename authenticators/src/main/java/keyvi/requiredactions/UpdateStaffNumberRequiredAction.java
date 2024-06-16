package keyvi.requiredactions;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        UserModel user = context.getUser();

        Stream<FederatedIdentityModel> stream =  context.getSession().users().getFederatedIdentitiesStream(context.getRealm(), user);
        LOG.warnf("Identity stream is: %s", stream);

        // Collect the stream elements into a list
        List<FederatedIdentityModel> federatedIdentities = stream.collect(Collectors.toList());

        // Log the federated identities
        for (FederatedIdentityModel federatedIdentity : federatedIdentities) {
            LOG.warnf("Federated Identity Provider: %s, User ID: %s, User Name: %s",
                    federatedIdentity.getIdentityProvider(),
                    federatedIdentity.getUserId(),
                    federatedIdentity.getUserName());
        }

        String serviceAccountLink = user.getServiceAccountClientLink();
        String identityProvider = user.getFederationLink();
        LOG.warnf("federation link is: %s", identityProvider);
        LOG.warnf("service account link is: %s", serviceAccountLink);

        String ssoAuth = AuthenticationManager.SSO_AUTH;
        LOG.warnf("SSO AUTH is %s", ssoAuth);

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
