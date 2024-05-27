package keyvi.authenticators;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisclosureAuthenticatorFactory implements AuthenticatorFactory {
    private static final String PROVIDER_ID = "disclosure-authenticator";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.CONDITIONAL,
            AuthenticationExecutionModel.Requirement.DISABLED
    };
    @Override
    public String getDisplayType() {
        return "Disclosure  Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }



    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "This Authenticator does nothing for now. but Disclosure will be implemented soon";
    }
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();
        ProviderConfigProperty enable_property= new ProviderConfigProperty();
        enable_property.setName("enableYivi");
        enable_property.setLabel("enable Yivi");
        enable_property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        enable_property.setDefaultValue("false");
        enable_property.setHelpText("Enable or disable using Yivi.");
        configProperties.add(enable_property);

//        ProviderConfigProperty over_18= new ProviderConfigProperty();
//        over_18.setName("over18property");
//        over_18.setLabel("over18 property");
//        over_18.setType(ProviderConfigProperty.BOOLEAN_TYPE);
//        over_18.setDefaultValue("false");
//        over_18.setHelpText("Enable or disable using over 18 credential.");
//        configProperties.add(over_18);

//        ProviderConfigProperty yivi_email= new ProviderConfigProperty();
//        yivi_email.setName("yiviemail");
//        yivi_email.setLabel("yivi email property");
//        yivi_email.setType(ProviderConfigProperty.BOOLEAN_TYPE);
//        yivi_email.setHelpText("Enable or disable using email credential");
//        configProperties.add(yivi_email);

        ProviderConfigProperty country = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableCountry", "yivi country property", "It enables or disables using country in request", country);
        configProperties.add(country);

        ProviderConfigProperty age = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableAgeLowerOver18", "yivi age over 18 property", "It enables or disables using over 18 age in request", age);
        configProperties.add(age);

        ProviderConfigProperty city = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableAddressCity", "yivi city property", "It enables or disables using city in request", city);
        configProperties.add(city);

        ProviderConfigProperty email = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableEmailEmail", "yivi email property", "It enables or disables using email in request", email);
        configProperties.add(email);

        ProviderConfigProperty studentCardUniversity = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableStudentCardUniversity", "yivi univeristy property", "It enables or disables using university in request", studentCardUniversity);
        configProperties.add(studentCardUniversity);

        return configProperties;
    }

    private void initiatizeConfigProperty(String name, String label, String helpText, ProviderConfigProperty configProperty)
    {
        configProperty.setName(name);
        configProperty.setLabel(label);
        configProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        configProperty.setHelpText(helpText);
    }
    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new DisclosureAuthenticator();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}