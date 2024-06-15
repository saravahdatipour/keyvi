package keyvi.attributes;

import org.keycloak.authentication.AuthenticationFlowContext;
import keyvi.attributes.Identifiers;
import java.util.List;         
import java.util.ArrayList;   
import java.util.Arrays;      
import java.util.stream.Collectors; 



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

        // Determine whether to force "yes" value for all attributes
        boolean forceYesForAll = true; // Set based on configuration or a fixed value

        // Generate the identifiers string with possible enforcement of "yes" values
        String identifiersStringified = prepareIdentifiersForFTL(enableCountry, enableAgeLowerOver18, enableEmailEmail, enableAddressCity, enableStudentCardUniversity, forceYesForAll);
        context.form().setAttribute("identifiersStringified", identifiersStringified);
}

    private static String prepareIdentifiersForFTL(boolean enableCountry, boolean enableAgeLowerOver18, boolean enableEmailEmail, boolean enableAddressCity, boolean enableStudentCardUniversity, boolean forceYesForAll) {
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("{\"content\":[");

    List<String> sections = new ArrayList<>();

    if (enableAgeLowerOver18) {
        sections.add(createSection("Over 18", Arrays.asList("irma-demo.MijnOverheid.ageLower.over18"), forceYesForAll));
    }

    if (enableCountry) {
        sections.add(createSection("Country", Arrays.asList("irma-demo.MijnOverheid.address.country"), forceYesForAll));
    }

    if (enableAddressCity) {
        sections.add(createSection("City", Arrays.asList("irma-demo.MijnOverheid.address.city"), forceYesForAll));
    }

    if (enableEmailEmail) {
        sections.add(createSection("Email", Arrays.asList("pbdf.sidn-pbdf.email.email"), forceYesForAll));
    }

    if (enableStudentCardUniversity) {
        sections.add(createSection("University", Arrays.asList("irma-demo.RU.studentCard.university"), forceYesForAll));
    }

    jsonBuilder.append(String.join(",", sections));
    jsonBuilder.append("]}");

    return jsonBuilder.toString();
}

private static String createSection(String label, List<String> attributes, boolean forceYes) {
    StringBuilder sectionBuilder = new StringBuilder();
    sectionBuilder.append("{\"label\":\"").append(label).append("\",");

    if (forceYes) {
        sectionBuilder.append("\"attributes\":{");
        List<String> attrs = new ArrayList<>();
        for (String attr : attributes) {
            attrs.add("\"" + attr + "\":\"yes\"");
        }
        sectionBuilder.append(String.join(",", attrs));
        sectionBuilder.append("}");
    } else {
        sectionBuilder.append("\"attributes\":[");
        sectionBuilder.append(attributes.stream().map(attr -> "\"" + attr + "\"").collect(Collectors.joining(",")));
        sectionBuilder.append("]");
    }

    sectionBuilder.append("}");
    return sectionBuilder.toString();
}



// private Array fetchRequiredAttributes()
// {

// }

// private UserResult ValidateExistanceOfRequiredAttributes()
// {
    
// }


}

