package keyvi.authenticators;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import keyvi.attributes.Identifiers;
import org.keycloak.authentication.*;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Errors;
import org.keycloak.models.*;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;


public class DisclosureAuthenticator implements Authenticator  {
    private static final Logger LOG = Logger.getLogger(DisclosureAuthenticator.class);
    public static final String USERNAME_PASSWORD_LOGIN_FORM = "backup.ftl";
    public static boolean YIVI_TOGGLE = true;


    // TODO: write authenticate logic
    @Override
//    public void authenticate(AuthenticationFlowContext context) {
//        // 1. This method is called first.
//        // It checks if the current request meets the requirements of the authenticator.
//        // If not, it may prompt the user for additional information or actions.
//        LOG.warnf("authenticate");
//        boolean enableSecondOption = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableSecondOption"));
//        boolean over18 = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("over18property"));
//        boolean yiviEmailEnabled = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("yiviemail"));
//        context.getAuthenticationSession().setAuthNote("yiviEmailEnabled", String.valueOf(yiviEmailEnabled));
//
//
//        LOG.warnf(String.valueOf(yiviEmailEnabled));
//        LOG.warnf("configuration working");
//
//
//        Response challengeusernamepassword = context.form().createLoginUsernamePassword()
//        context.challenge(challengeusernamepassword);
//
//    }
    public void authenticate(AuthenticationFlowContext context) {
        LOG.debugf("authenticate");
//        final LoginFormsProvider formsProvider = context.form();
//        formsProvider.setAttribute("formModel", new DisclosureFormModel());
//        // Example boolean variable
//        boolean enableSecondOption = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableSecondOption"));
//
//        formsProvider.setAttribute("enableSecondOption", enableSecondOption);
//        context.challenge(formsProvider.createLoginUsernamePassword());

        // Pass the configuration value to the form
        this.setRequiredAttributes(context);

        // Proceed with challenge
        context.challenge(context.form().createLoginUsernamePassword());

        this.createSampleAccount(context);
    }

    private void createSampleAccount(AuthenticationFlowContext context) {
        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();
        UserProvider userProvider = session.users();

        // Check if the user already exists
        UserModel existingUser = userProvider.getUserByEmail(realm, "test@gmail.com");
        if (existingUser != null) {
            LOG.info("Sample user already exists");
            return;
        }

        // Create a new user
        UserModel user = userProvider.addUser(realm, "test-user");
        user.setEnabled(true);
        user.setEmail("test@gmail.com");
        user.setFirstName("Jason");
        user.setLastName("Bro");

        // Set user attributes
        user.setSingleAttribute("name", "Jason");
        user.setSingleAttribute("age", "19");

        // Set a temporary password for the user
        user.credentialManager().updateCredential(UserCredentialModel.password("temporaryPassword"));
    }


    @Override
    public boolean requiresUser() {
        // 2. requiresUser(): After the initial authentication check,
        // this method determines if the authenticator requires an identified user to proceed.
        // This helps the authentication flow decide if user identification steps are needed.
        return false;
    }




    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        //3.configuredFor(KeycloakSession session, RealmModel realm, UserModel user):
        // If the authenticator requires a user, this method checks whether the authenticator is configured correctly
        // for the specific user in question.
        return true;
    }


    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    public void authenticateWithYivi(AuthenticationFlowContext context){

        //has logic for redirecting to yivi and translating a response to credentials keycloak understands
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        LOG.warnf("action");
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst("username");
        String password = formData.getFirst("password");

        //Validate form
        if (Validation.isBlank(username) || Validation.isBlank((password))) {
            //Form is empty somewhere
            context.getEvent().error("Username is missing");
            this.setRequiredAttributes(context);
            Response challenge = context.form()
                    .createLoginUsernamePassword();

            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        //find user
        UserModel user = context.getSession().users().getUserByUsername(context.getRealm(), username);
        if (user == null) {
            // no user matched
            context.getEvent().error(Errors.USER_NOT_FOUND);
            this.setRequiredAttributes(context);
            Response challenge = context.form()
                    .setError(Messages.INVALID_USER)
                    .createLoginUsernamePassword();
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }
        CredentialInput input = UserCredentialModel.password(password);
        boolean isValid = user.credentialManager().isValid(input);

        if (isValid) {
            // Password is valid
            context.setUser(user);
            context.success();
        } else {
            // Password is invalid
            context.getEvent().user(user).error(Errors.INVALID_USER_CREDENTIALS);
            this.setRequiredAttributes(context);
            Response challenge = context.form()
                    .setError(Messages.INVALID_PASSWORD)
                    .createLoginUsernamePassword();
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
        }

    }

    private void setRequiredAttributes(AuthenticationFlowContext context)
    {
        boolean enableYivi = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableYivi"));
        context.form().setAttribute("enableYivi", enableYivi);

        boolean enableCountry = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableCountry"));
        boolean enableAgeLowerOver18 = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableAgeLowerOver18"));
        boolean enableEmailEmail = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableEmailEmail"));
        boolean enableAddressCity = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableAddressCity"));
        boolean enableStudentCardUniversity = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableStudentCardUniversity"));

        String identifiersStringified = prepareIdentifiersForFTL(enableCountry, enableAgeLowerOver18, enableEmailEmail, enableAddressCity, enableStudentCardUniversity);
        context.form().setAttribute("identifiersStringified", identifiersStringified);
    }

    // Private function to prepare the identifiers for FTL
    private String prepareIdentifiersForFTL(boolean enableCountry, boolean enableAgeLowerOver18, boolean enableEmailEmail, boolean enableAddressCity, boolean enableStudentCardUniversity) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        boolean isFirstGroup = true;

        // Conditionally add identifiers based on enabled flags
        if (enableAgeLowerOver18) {
            if (!isFirstGroup) jsonBuilder.append(",");
            jsonBuilder.append("[[\"");
            jsonBuilder.append(Identifiers.IrmaDemoMijnOverheid.AGE_LOWER_OVER_18.getIdentifier());
            jsonBuilder.append("\"]]");
            isFirstGroup = false;
        }

        if (enableCountry || enableAddressCity) {
            if (!isFirstGroup) jsonBuilder.append(",");
            jsonBuilder.append("[[");
            if (enableCountry) {
                jsonBuilder.append("\"");
                jsonBuilder.append(Identifiers.IrmaDemoMijnOverheid.ADDRESS_COUNTRY.getIdentifier());
                jsonBuilder.append("\"");
                if (enableAddressCity) jsonBuilder.append(",");
            }
            if (enableAddressCity) {
                jsonBuilder.append("\"");
                jsonBuilder.append(Identifiers.IrmaDemoMijnOverheid.ADDRESS_CITY.getIdentifier());
                jsonBuilder.append("\"");
            }
            jsonBuilder.append("]]");
            isFirstGroup = false;
        }

        if (enableEmailEmail || enableStudentCardUniversity) {
            if (!isFirstGroup) jsonBuilder.append(",");
            jsonBuilder.append("[[");
            if (enableEmailEmail) {
                jsonBuilder.append("\"");
                jsonBuilder.append(Identifiers.Pbdf.EMAIL_EMAIL.getIdentifier());
                jsonBuilder.append("\"");
                if (enableStudentCardUniversity) jsonBuilder.append(",");
            }
            if (enableStudentCardUniversity) {
                jsonBuilder.append("\"");
                jsonBuilder.append(Identifiers.IrmaDemoRU.STUDENT_CARD_UNIVERSITY.getIdentifier());
                jsonBuilder.append("\"");
            }
            jsonBuilder.append("]]");
        }

        jsonBuilder.append("]");

        // Convert the StringBuilder content to a string
        return jsonBuilder.toString();
    }


    @Override
    public void close() {

    }
}