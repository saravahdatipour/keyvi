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


private Array fetchRequiredAttributes()
{

}

private UserResult ValidateExistanceOfRequiredAttributes()
{
    
}