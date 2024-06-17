package keyvi.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import keyvi.attributes.Identifiers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

                // Create an instance of HttpClient
                HttpClient httpClient = HttpClient.newHttpClient();

                // Create a GET request to the token status endpoint
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                // Send the request and retrieve the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Check if the response status code is 200 (OK)
                if (response.statusCode() == 200) {
                    // Get the response body as a string
                    String responseBody = response.body().trim();

                    // Check if the response body is exactly "DONE"
                    if (responseBody.equals("DONE")) {
                        // Token is valid
                        return true;
                    }
                }
            }
        }
        return false;
    } catch (Exception e) {
        // Handle any exceptions that occur during the HTTP request or JSON parsing
        System.err.println("Error validating Yivi response: " + e.getMessage());
        return false;
    }
}


}
