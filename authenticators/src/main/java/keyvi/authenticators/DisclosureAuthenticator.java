package keyvi.authenticators;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import keyvi.attributes.Identifiers;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Errors;
import org.keycloak.models.*;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import keyvi.objects.UserResult; 
import keyvi.utils.PasswordGenerator;
import keyvi.utils.AccountMasker;
import keyvi.attributes.AttributeManager;
import keyvi.utils.FeatureManager;


public class DisclosureAuthenticator implements Authenticator  {
    private static final Logger LOG = Logger.getLogger(DisclosureAuthenticator.class);
    public static final String USERNAME_PASSWORD_LOGIN_FORM = "backup.ftl";
    public static boolean YIVI_TOGGLE = true;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AttributeManager.setRequiredAttributes(context);

        context.challenge(context.form().createLoginUsernamePassword());
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

    @Override
    public void action(AuthenticationFlowContext context) {
        LOG.warnf("action");
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String loginMethod = formData.getFirst("login_method");

        if ("yivi".equals(loginMethod)) {
            handleYiviLogin(context, formData);
        } else {
            handleStandardLogin(context, formData);
        }
    }

    private void handleStandardLogin(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String username = formData.getFirst("username");
        String password = formData.getFirst("password");

        // Validate form
        if (Validation.isBlank(username) || Validation.isBlank(password)) {
            // Form is empty somewhere
            context.getEvent().error("Username is missing");
            AttributeManager.setRequiredAttributes(context);
            Response challenge = context.form().createLoginUsernamePassword();
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        // Find user
        UserModel user = context.getSession().users().getUserByUsername(context.getRealm(), username);
        if (user == null) {
            // No user matched
            context.getEvent().error(Errors.USER_NOT_FOUND);
            AttributeManager.setRequiredAttributes(context);
            Response challenge = context.form().setError(Messages.INVALID_USER).createLoginUsernamePassword();
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
            AttributeManager.setRequiredAttributes(context);
            Response challenge = context.form().setError(Messages.INVALID_PASSWORD).createLoginUsernamePassword();
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
        }
    }

    private void handleYiviLogin(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String claims = formData.getFirst("claims");

        // Process the claims data
        if (claims != null && !claims.isEmpty()) {
            LOG.warnf("Claims are not empty!");
            LOG.warnf("Claims Data: %s", claims);

            // Initialize the Yivi account and set the user in the context
            UserResult result = this.initializeYiviAccount(context, claims);
            UserModel user = result.getUser();
            String errorMessage = result.getErrorMessage();
            if (user != null) {
                context.setUser(user);
                context.success();
            } else {
                LOG.warnf("error message should show up on screen for failed yivi login");
                AttributeManager.setRequiredAttributes(context);
                Response challenge = context.form()
                .setAttribute("login_method", "yivi")  // Keep the same login method
                .setError(errorMessage)  // Pass the error message
                .createLoginUsernamePassword();  // Use the form originally intended for Yivi login

                context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            }

        } else {
            // Handle missing claims data in a similar way
        LOG.warnf("Claims data missing error should show up now on screen.");
        AttributeManager.setRequiredAttributes(context);
         Response challenge = context.form()
            .setAttribute("login_method", "yivi")
            .setError("Claims data is missing.")
            .createLoginUsernamePassword();

        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
        }
    }

private UserResult initializeYiviAccount(AuthenticationFlowContext context, String claims) {
    KeycloakSession session = context.getSession();
    RealmModel realm = context.getRealm();
    UserProvider userProvider = session.users();

    // Parse claims JSON string using Gson
    Gson gson = new Gson();
    JsonObject claimsData = gson.fromJson(claims, JsonObject.class);

    String email = null;
    String country = null;
    String city = null;
    String university = null;
    String ageOver18 = "no";  

    JsonArray disclosedArray = claimsData.getAsJsonArray("disclosed");
    for (JsonElement arrayElement : disclosedArray) {
        JsonArray array = arrayElement.getAsJsonArray();
        for (JsonElement objElement : array) {
            JsonObject obj = objElement.getAsJsonObject();
            String id = obj.get("id").getAsString();
            String rawValue = obj.get("rawvalue").getAsString();
            if (Identifiers.IrmaDemoMijnOverheid.AGE_LOWER_OVER_18.getIdentifier().equals(id)) {
                ageOver18 = rawValue;
            } else if (Identifiers.IrmaDemoMijnOverheid.ADDRESS_COUNTRY.getIdentifier().equals(id)) {
                country = rawValue;
            } else if (Identifiers.IrmaDemoMijnOverheid.ADDRESS_CITY.getIdentifier().equals(id)) {
                city = rawValue;
            } else if (Identifiers.Pbdf.EMAIL_EMAIL.getIdentifier().equals(id)) {
                email = rawValue;
            } else if (Identifiers.IrmaDemoRU.STUDENT_CARD_UNIVERSITY.getIdentifier().equals(id)) {
                university = rawValue;
            }
        }
    }


    Map<String, String> config = context.getAuthenticatorConfig().getConfig();
    LOG.warnf("All config values: %s", config);

    boolean enableAgeLowerOver18 = FeatureManager.isFeatureEnabled("enableAgeLowerOver18", context);

     if (enableAgeLowerOver18 && !ageOver18.equals("yes")) {
        return new UserResult(null, "Age verification failed or is missing. Must be over 18.");
    }

     if (email == null) {
        return new UserResult(null, "Email is missing in the claims data.");
    }

    //checking masking state
    boolean enableMaskedAccount = FeatureManager.isFeatureEnabled("enableMaskedAccount", context);
    String maskedEmailDomain = FeatureManager.getFeatureValue("maskedAccountDomain", context);
    String maskedEmailKey = FeatureManager.getFeatureValue("maskedAccountKey", context);
    if(enableMaskedAccount)
    {
        email = AccountMasker.generateMaskedEmail(email, maskedEmailDomain, maskedEmailKey);
    }

    // Check if the user already exists
    UserModel existingUser = userProvider.getUserByEmail(realm, email);
    if (existingUser != null) {
        LOG.warnf("User with email already exists");
        return new UserResult(existingUser, null);
    }

    //create new account (user does not yet exist)
    isAttributeStorageDisabled = FeatureManager.isFeatureEnabled("disableAttributeStorage", context);
    return this.createNewAccount(userProvider, realm, email, isAttributeStorageDisabled, ageOver18, country, city, university);
}

    private UserResult createNewAccount(UserProvider userProvider , RealmModel realm, String email, Boolean isAttributeStorageDisabled, String ageOver18, String country, String city, String university)
    {
        UserModel user = userProvider.addUser(realm, email);
        user.setUsername(email);
        user.setEmail(email);  
        user.setFirstName("Yivi");
        user.setLastName("User");
        user.setEnabled(true);

        if(!isAttributeStorageDisabled)
        {
          // Set user attributes
            user.setSingleAttribute("ageOver18", ageOver18);
            user.setSingleAttribute("country", country);
            user.setSingleAttribute("city", city);
            user.setSingleAttribute("university", university);
        }

        String temporaryPassword = PasswordGenerator.generateSecurePassword();
        user.credentialManager().updateCredential(UserCredentialModel.password(temporaryPassword));

        return new UserResult(user, null);
    }


    @Override
    public void close() {

    }
}