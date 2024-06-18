package keyvi.authenticators;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
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
        return "Authenticator that handles attribute disclosure verification.";
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

        ProviderConfigProperty email = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableEmailEmail", "yivi email property", "It enables or disables using email in request", email);
        configProperties.add(email);

        ProviderConfigProperty disableAttributeStorage = new ProviderConfigProperty();
        disableAttributeStorage.setName("disableAttributeStorage");
        disableAttributeStorage.setLabel("Disable Attribute Storage");
        disableAttributeStorage.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        disableAttributeStorage.setDefaultValue("false");
        disableAttributeStorage.setHelpText("If true, attributes will not be stored after verification.");
        configProperties.add(disableAttributeStorage);

        //masked account config
        ProviderConfigProperty enableMasked = new ProviderConfigProperty();
        enableMasked.setName("enableMaskedAccount");
        enableMasked.setLabel("Enable Masked Account");
        enableMasked.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        enableMasked.setDefaultValue("false");
        enableMasked.setHelpText("Enable or disable masked account feature.");
        configProperties.add(enableMasked);

        ProviderConfigProperty domainProperty = new ProviderConfigProperty();
        domainProperty.setName("maskedAccountDomain");
        domainProperty.setLabel("Masked Account Domain");
        domainProperty.setType(ProviderConfigProperty.STRING_TYPE);
        domainProperty.setDefaultValue("@yivisso.com");
        domainProperty.setHelpText("Specify the domain to use for masked accounts. For example @yivisso.com");
        configProperties.add(domainProperty);

        ProviderConfigProperty keyProperty = new ProviderConfigProperty();
        keyProperty.setName("maskedAccountKey");
        keyProperty.setLabel("Masked Account Key");
        keyProperty.setType(ProviderConfigProperty.STRING_TYPE);
        keyProperty.setDefaultValue("keyvi-masked");
        keyProperty.setHelpText("Specify the key prefix for masked accounts. For example keyvi-masked");
        configProperties.add(keyProperty);


        ProviderConfigProperty age = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableAgeLowerOver18", "yivi age over 18 property", "It enables or disables using over 18 age in request", age);
        configProperties.add(age);

        ProviderConfigProperty country = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableCountry", "yivi country property", "It enables or disables using country in request", country);
        configProperties.add(country);

        ProviderConfigProperty acceptedCountry = new ProviderConfigProperty();
        acceptedCountry.setName("countryAcceptedValue");
        acceptedCountry.setLabel("Country string should match:");
        acceptedCountry.setType(ProviderConfigProperty.STRING_TYPE);
        acceptedCountry.setDefaultValue("Netherlands");
        configProperties.add(acceptedCountry);


        //All Provider Config Properties below are disabled in admin:
        ProviderConfigProperty city = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableAddressCity", "yivi city property (archived)", "It enables or disables using city in request", city);
        city.setReadOnly(true);
        configProperties.add(city);

        ProviderConfigProperty studentCardUniversity = new ProviderConfigProperty();
        this.initiatizeConfigProperty("enableStudentCardUniversity", "yivi univeristy property (archived)", "It enables or disables using university in request", studentCardUniversity);
        studentCardUniversity.setReadOnly(true);
        configProperties.add(studentCardUniversity);

        ProviderConfigProperty useCustomConfig = new ProviderConfigProperty();
        useCustomConfig.setName("onlyUseCustomConfig");
        useCustomConfig.setLabel("Only Use Custom Disclosure Config (archived)");
        useCustomConfig.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        useCustomConfig.setDefaultValue("false");
        useCustomConfig.setReadOnly(true);
        useCustomConfig.setHelpText("Enable to use custom disclosure JSON instead of default settings.");
        configProperties.add(useCustomConfig);

        ProviderConfigProperty jsonProperty = new ProviderConfigProperty();
        jsonProperty.setName("disclosureConfigYivi");
        jsonProperty.setLabel("Custom Config Disclosure Array (archived)");
        jsonProperty.setType(ProviderConfigProperty.STRING_TYPE);
        jsonProperty.setReadOnly(true);
        jsonProperty.setHelpText("Enter Disclosure part of json string.");
        configProperties.add(jsonProperty);

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