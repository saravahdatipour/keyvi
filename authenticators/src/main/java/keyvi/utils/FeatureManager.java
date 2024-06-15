
public static bool isFeatureEnabled(String featureKey, AuthenticationFlowContext context)
{
    return Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get(featureKey))
}

public static string getFeatureValue(String featureKey, AuthenticationFlowContext context)
{
    return context.getAuthenticatorConfig().getConfig().get(featureKey)
} 