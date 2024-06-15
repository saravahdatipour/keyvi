package keyvi.utils;

import org.keycloak.authentication.AuthenticationFlowContext;


public class FeatureManager
{

public static Boolean isFeatureEnabled(String featureKey, AuthenticationFlowContext context)
{
    return Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get(featureKey));
}

public static String getFeatureValue(String featureKey, AuthenticationFlowContext context)
{
    return context.getAuthenticatorConfig().getConfig().get(featureKey);
} 


}
