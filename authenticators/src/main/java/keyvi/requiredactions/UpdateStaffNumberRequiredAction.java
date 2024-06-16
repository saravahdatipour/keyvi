package keyvi.requiredactions;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

public class UpdateStaffNumberRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "usn-required-action";
    private static final String UPDATE_STAFF_NUMBER_FORM = "update-staff-number.ftl";

    @Override
    public void evaluateTriggers(RequiredActionContext requiredActionContext) {
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
    }

    @Override
    public void processAction(RequiredActionContext context) {
    }

    @Override
    public void close() {
    }
}
