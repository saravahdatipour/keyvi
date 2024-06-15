// Private function to prepare the identifiers for FTL
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

    public Array parseDisclosedArray()
    {

    // Parse claims JSON string using Gson
    Gson gson = new Gson();
    JsonObject claimsData = gson.fromJson(claims, JsonObject.class);

    String email = null;
    String firstName = "Yivi";
    String lastName = "User";
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


    }