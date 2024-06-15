package keyvi.attributes;

import org.keycloak.authentication.AuthenticationFlowContext;
import keyvi.attributes.Identifiers;


public class AttributeManager
{


public static void setRequiredAttributes(AuthenticationFlowContext context)
{
        boolean enableYivi = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableYivi"));
        context.form().setAttribute("enableYivi", enableYivi);

        //todo: use feature manager
        boolean enableCountry = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableCountry"));
        boolean enableAgeLowerOver18 = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableAgeLowerOver18"));
        boolean enableEmailEmail = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableEmailEmail"));
        boolean enableAddressCity = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableAddressCity"));
        boolean enableStudentCardUniversity = Boolean.parseBoolean(context.getAuthenticatorConfig().getConfig().get("enableStudentCardUniversity"));

        String identifiersStringified = prepareIdentifiersForFTL(enableCountry, enableAgeLowerOver18, enableEmailEmail, enableAddressCity, enableStudentCardUniversity);
        context.form().setAttribute("identifiersStringified", identifiersStringified);
}

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


// private Array fetchRequiredAttributes()
// {

// }

// private UserResult ValidateExistanceOfRequiredAttributes()
// {
    
// }


}

