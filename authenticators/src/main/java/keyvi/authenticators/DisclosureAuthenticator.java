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


public class DisclosureAuthenticator implements Authenticator  {
    private static final Logger LOG = Logger.getLogger(DisclosureAuthenticator.class);
    public static final String USERNAME_PASSWORD_LOGIN_FORM = "backup.ftl";
    public static boolean YIVI_TOGGLE = true;


    // TODO: write authenticate logic
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOG.debugf("authenticate");
        // Pass the configuration value to the form
        this.setRequiredAttributes(context);

        // Proceed with challenge
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

    public void authenticateWithYivi(AuthenticationFlowContext context){

        //has logic for redirecting to yivi and translating a response to credentials keycloak understands
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
            this.setRequiredAttributes(context);
            Response challenge = context.form().createLoginUsernamePassword();
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        // Find user
        UserModel user = context.getSession().users().getUserByUsername(context.getRealm(), username);
        if (user == null) {
            // No user matched
            context.getEvent().error(Errors.USER_NOT_FOUND);
            this.setRequiredAttributes(context);
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
            this.setRequiredAttributes(context);
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
                this.setRequiredAttributes(context);
                Response challenge = context.form()
                .setAttribute("login_method", "yivi")  // Keep the same login method
                .setError(errorMessage)  // Pass the error message
                .createLoginUsernamePassword();  // Use the form originally intended for Yivi login

                context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            }

        } else {
            // Handle missing claims data in a similar way
        LOG.warnf("Claims data missing error should show up now on screen.");
        this.setRequiredAttributes(context);
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
    String firstName = "Yivi";
    String lastName = "User";
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


    LOG.warnf("Logging function is working");  
    Map<String, String> config = context.getAuthenticatorConfig().getConfig();
    LOG.warnf("All config values: %s", config);
    String enableAgeLowerOver18Config = config.get("enableAgeLowerOver18");
    LOG.warnf("Enable Age Lower Over 18 config string: '%s'", enableAgeLowerOver18Config);
    boolean enableAgeLowerOver18 = Boolean.parseBoolean(enableAgeLowerOver18Config);
    // Check if age over 18 verification is enabled and failed
    //boolean enableAgeLowerOver18 = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableAgeLowerOver18"));
    LOG.warnf("Enable Age Lower Over 18: %s", enableAgeLowerOver18);
    LOG.warnf("Age Over 18 value: %s", ageOver18);


     if (enableAgeLowerOver18 && !ageOver18.equals("yes")) {
        return new UserResult(null, "Age verification failed or is missing. Must be over 18.");
    }

     if (email == null) {
        return new UserResult(null, "Email is missing in the claims data.");
    }

    // Check if the user already exists
    UserModel existingUser = userProvider.getUserByEmail(realm, email);
    if (existingUser != null) {
        LOG.warnf("User with email already exists");
        return new UserResult(existingUser, null);
    }

    //masking
    boolean enableMaskedAccount = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableMaskedAccount"));
    String maskedEmailDomain = context.getAuthenticatorConfig().getConfig().get("maskedAccountDomain");
    String maskedEmailKey = context.getAuthenticatorConfig().getConfig().get("maskedAccountKey");
    if(enableMaskedAccount)
    {
        email = AccountMasker.generateMaskedEmail(email, maskedEmailDomain, maskedEmailKey);
        String username = AccountMasker.generateHashedUsername(email); 
        user.setUsername(username);
        user.setEmail(email);  // Setting the masked email
    }
    else
    {
        //dont mask them store them as they are
        user.setUsername(email);  
        user.setEmail(email);
    }


    // Create a new user
    UserModel user = userProvider.addUser(realm, email);
    user.setEnabled(true);
    user.setEmail(email);
    user.setUsername(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);

    boolean isStorageDisabled = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("disableAttributeStorage"));
    if(!isStorageDisabled)
    {
        // Set user attributes
        user.setSingleAttribute("ageOver18", ageOver18);
        user.setSingleAttribute("country", country);
        user.setSingleAttribute("city", city);
        user.setSingleAttribute("university", university);
    }

    // Set a temporary password for the user
    String temporaryPassword = PasswordGenerator.generateSecurePassword();
    user.credentialManager().updateCredential(UserCredentialModel.password(temporaryPassword));

    return new UserResult(user, null);
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