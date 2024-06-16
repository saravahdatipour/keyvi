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

        JsonObject result = new JsonObject();

        JsonArray disclosedArray = claimsData.getAsJsonArray("disclosed");
        for (JsonElement arrayElement : disclosedArray) {
            JsonArray array = arrayElement.getAsJsonArray();
            for (JsonElement objElement : array) {
                JsonObject obj = objElement.getAsJsonObject();
                String id = obj.get("id").getAsString();
                JsonElement rawValueElement = obj.get("rawvalue");
                String rawValue = rawValueElement.isJsonNull() ? null : rawValueElement.getAsString();

                if (id.equals(Identifiers.IrmaDemoMijnOverheid.AGE_LOWER_OVER_18.getIdentifier())) {
                    result.addProperty("ageOver18", rawValue);
                } else if (id.equals(Identifiers.IrmaDemoMijnOverheid.ADDRESS_COUNTRY.getIdentifier())) {
                    result.addProperty("country", rawValue);
                } else if (id.equals(Identifiers.IrmaDemoMijnOverheid.ADDRESS_CITY.getIdentifier())) {
                    result.addProperty("city", rawValue);
                } else if (id.equals(Identifiers.Pbdf.EMAIL_EMAIL.getIdentifier())) {
                    result.addProperty("email", rawValue);
                } else if (id.equals(Identifiers.IrmaDemoRU.STUDENT_CARD_UNIVERSITY.getIdentifier())) {
                    result.addProperty("university", rawValue);
                }
            }
        }

        return result;
    }
}
