package keyvi.utils;

import com.google.gson.*;
import keyvi.attributes.Identifiers;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;


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
                    LOG.infof("Validating token: %s", token);

                    // Create an Apache HttpClient
                    try (CloseableHttpClient client = HttpClients.createDefault()) {
                        // Create a GET request
                        HttpGet request = new HttpGet(url);
                        LOG.warnf("Sending GET request to: %s", url);

                        // Send the GET request and retrieve the response
                        HttpResponse response = client.execute(request);
                        int statusCode = response.getStatusLine().getStatusCode();
                        LOG.warnf("Response status code: %d", statusCode);

                        // Check if the response status code is 200 (OK)
                        if (statusCode == 200) {
                            // Get the response body as a string
                            HttpEntity entity = response.getEntity();
                            String responseBody = EntityUtils.toString(entity).trim();
                            LOG.warnf("Response body: %s", responseBody);

                            // Check if the response body is exactly "DONE"
                            if (responseBody.equals("DONE")) {
                                LOG.info("Token is valid");
                                return true;
                            } else {
                                LOG.warn("Invalid response body");
                            }
                        } else {
                            LOG.warnf("Unexpected status code: %d", statusCode);
                        }
                    }
                } else {
                    LOG.warnf("Token not found in JSON");
                }
            } else {
                LOG.warnf("Invalid JSON format");
            }
            return false;
        } catch (Exception e) {
            // Handle any exceptions that occur during the HTTP request or JSON parsing
            LOG.warnf("Error validating Yivi response: %s", e.getMessage());
            LOG.warnf("Exception details:", e);
            return false;
        }
    }


}
