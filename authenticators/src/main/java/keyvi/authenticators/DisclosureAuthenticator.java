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
import java.util.List;
import java.util.ArrayList;
import com.google.gson.JsonSyntaxException; 

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

    if (claims == null || claims.isEmpty()) {
    LOG.warnf("No claims data provided.");
    context.form().setError("Authentication failed: No claims data provided.");
    // Create a response that will be shown to the user
    Response challengeResponse = context.form().createErrorPage(Response.Status.BAD_REQUEST);
    // Now pass both the error and the response to the failureChallenge method
    context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
    return;
}

    LOG.warnf("Processing claims: %s", claims);
    Object result = this.initializeYiviAccount(context, claims);
    if (result instanceof UserModel) {
        UserModel user = (UserModel) result;
        context.setUser(user);
        context.success();
    } else if (result instanceof List) {
        List<String> errors = (List<String>) result;
        if (!errors.isEmpty()) {
            LOG.warnf("Errors occurred during Yivi account initialization: %s", String.join(", ", errors));
            context.form().setError(String.join("\n", errors));
            Response response = context.form().createErrorPage(Response.Status.BAD_REQUEST);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, response);
        }
    }
}

private void showErrorsOnPage(AuthenticationFlowContext context, List<String> errors) {
    context.form().setAttribute("errors", errors);
    Response challenge = context.form().createForm("yiviLoginError.ftl");
    context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
}

    private Object initializeYiviAccount(AuthenticationFlowContext context, String claims) {
    List<String> errors = new ArrayList<>();
    KeycloakSession session = context.getSession();
    RealmModel realm = context.getRealm();
    UserProvider userProvider = session.users();

    Gson gson = new Gson();
    JsonObject claimsData;
    try {
        claimsData = gson.fromJson(claims, JsonObject.class);
    } catch (JsonSyntaxException e) {
        errors.add("Failed to parse claims data: " + e.getMessage());
        return errors;  // Return the list of errors directly.
    }

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
            switch (id) {
                case Identifiers.IrmaDemoMijnOverheid.AGE_LOWER_OVER_18:
                    ageOver18 = rawValue;
                    break;
                case Identifiers.IrmaDemoMijnOverheid.ADDRESS_COUNTRY:
                    country = rawValue;
                    break;
                case Identifiers.IrmaDemoMijnOverheid.ADDRESS_CITY:
                    city = rawValue;
                    break;
                case Identifiers.Pbdf.EMAIL_EMAIL:
                    email = rawValue;
                    break;
                case Identifiers.IrmaDemoRU.STUDENT_CARD_UNIVERSITY:
                    university = rawValue;
                    break;
            }
        }
    }

    boolean enableAgeLowerOver18 = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().getOrDefault("enableAgeLowerOver18", "false"));
    if (enableAgeLowerOver18 && !ageOver18.equals("yes")) {
        errors.add("Age verification failed: User is not over 18.");
    }
    if (email == null) {
        errors.add("Email is missing in the claims data.");
    }

    if (!errors.isEmpty()) {
        return errors;  // Return the list of errors instead of null.
    }

    UserModel existingUser = userProvider.getUserByEmail(realm, email);
    if (existingUser != null) {
        return existingUser;
    }

    UserModel newUser = userProvider.addUser(realm, email);
    newUser.setEnabled(true);
    newUser.setEmail(email);
    newUser.setUsername(email);
    newUser.setFirstName(firstName);
    newUser.setLastName(lastName);
    newUser.setSingleAttribute("ageOver18", ageOver18);
    newUser.setSingleAttribute("country", country);
    newUser.setSingleAttribute("city", city);
    newUser.setSingleAttribute("university", university);
    newUser.credentialManager().updateCredential(UserCredentialModel.password("temporaryPassword"));

    return newUser;
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