package keyvi.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import keyvi.attributes.Identifiers;

public class YiviUtilities {

    public static JsonObject parseDisclosedArray(String claims) {
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

        JsonObject result = new JsonObject();
        result.addProperty("email", email);
        result.addProperty("country", country);
        result.addProperty("city", city);
        result.addProperty("university", university);
        result.addProperty("ageOver18", ageOver18);
        return result;
    }
}
