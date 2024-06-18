package keyvi.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import keyvi.attributes.Identifiers;
import org.jboss.logging.Logger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class YiviUtilities {
    private static final Logger LOG = Logger.getLogger(YiviUtilities.class);

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

    public static String getJsonString(JsonObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            return jsonObject.get(key).getAsString();
        }
        return null;  // Return null if the key doesn't exist or the value is JsonNull
    }

    public static boolean isResponseValid(String claimsJson) {
    try {
        JsonElement element = JsonParser.parseString(claimsJson);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            String token = getJsonString(jsonObject, "token");
            if (token != null) {
                // Create the URL for the token status endpoint
                String url = "https://catchthebugs.com/session/" + token + "/status";

                // Create a Jakarta EE client
                Client client = ClientBuilder.newClient();

                try {
                    // Create a WebTarget for the token status endpoint
                    WebTarget target = client.target(url);

                    // Send a GET request to the token status endpoint
                    Response response = target.request().get();

                    // Check if the response status code is 200 (OK)
                    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                        // Get the response body as a string
                        String responseBody = response.readEntity(String.class).trim();

                        // Check if the response body is exactly "DONE"
                        if (responseBody.equals("DONE")) {
                            // Token is valid
                            return true;
                        }
                    }
                } finally {
                    // Close the client
                    client.close();
                }
            }
        }
        return false;
    } catch (Exception e) {
        // Handle any exceptions that occur during the HTTP request or JSON parsing
        LOG.warnf("Error validating Yivi response exception was thrown: %s", e);
        return false;
    }
}


}
