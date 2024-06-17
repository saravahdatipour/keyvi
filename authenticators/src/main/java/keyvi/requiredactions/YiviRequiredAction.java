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

public class YiviRequiredAction implements RequiredActionProvider {
    //Yivi as a required action if user chooses to sign up via social providers
    public static final String PROVIDER_ID = "yivi-required-action";
    private static final Logger LOG = Logger.getLogger(YiviRequiredAction.class);

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
        String yiviResult = context.getHttpRequest().getDecodedFormParameters().getFirst("yivi_result");
        // in prod you will need token validation to ensure the yivi server can verify the integrity of token within yiviResult
        if (yiviResult != null) {
            // Yivi authentication successful
            LOG.warnf("Yivi authentication successful. Result: %s", yiviResult);
            context.success();
        } else {
            // Yivi authentication not performed or unsuccessful
            LOG.warnf("Yivi authentication not performed or unsuccessful.");
            Response challenge = context.form().createForm("required-action.ftl");
            context.challenge(challenge);
        }
    }

    @Override
    public void close() {
    }

    private boolean isSocialLogin(RequiredActionContext context) {
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

// private boolean validateYiviToken(String token) {
//     // communicate with the Yivi server to verify the token's authenticity
//     // Return true if the token is valid, false otherwise
//     // For now assume non empty token means valid.
//     return !token.isEmpty();
// }

}
