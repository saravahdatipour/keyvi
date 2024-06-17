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
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        if (this.isSocialLogin(context)) {
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

    private boolean isSocialLogin(AuthenticationFlowContext context) {
    UserModel user = context.getUser();
    if (user != null) {
        Stream<FederatedIdentityModel> stream = context.getSession().users().getFederatedIdentitiesStream(context.getRealm(), user);
        LOG.warnf("Identity stream is: %s", stream);

        List<FederatedIdentityModel> federatedIdentities = stream.collect(Collectors.toList());
        if (federatedIdentities.isEmpty()) {
            LOG.warnf("No federated identities found. Assuming non-social login.");
            return false;
        } else {
            LOG.warnf("Federated identities found. Assuming social login.");
            return true;
        }
    }
    LOG.warnf("User is null. Assuming non-social login.");
    return false;
}
}
