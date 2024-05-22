package keyvi.authenticators;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.common.util.MultivaluedHashMap;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DisclosureFormModel {

    private final List<String> options;
    private boolean hiddenOption;

    public DisclosureFormModel() {
        options = new ArrayList<>();
        options.add("Option1");
        options.add("Option2");

        // Initialize the MultivaluedHashMap
//        MultivaluedHashMap<String, Boolean> map = new MultivaluedHashMap<>();
//        map.putSingle("hiddenOption", false); // Example entry

        // Serialize and encode the map content, then add to options
//        options.add(encodeMapToString(map));

        // Create data model
//        MultivaluedHashMap<String, Object> root = new MultivaluedHashMap<>();
//        MultivaluedHashMap<String, String> testMap = new MultivaluedHashMap<>();
//        testMap.putSingle("one", "two");
//        String hiddenValue = testMap.getFirst("one").toString();
//        options.add(hiddenValue);
//        root.put("hello", testMap);


    }

    // Serialize the map and encode it as a Base64 string
//    private String encodeMapToString(MultivaluedHashMap<String, Boolean> map) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : map.keySet()) {
//            sb.append(key).append(":");
//            List<Boolean> values = map.get(key);
//            for (Boolean value : values) {
//                sb.append(value.toString()).append(",");
//            }
//            sb.deleteCharAt(sb.length() - 1); // Remove last comma
//            sb.append(";");
//        }
//        String rawString = sb.toString();
//        return Base64.getEncoder().encodeToString(rawString.getBytes());
//    }

    /**
     * Method is being used in the freemarker template, which is why we explicitly ignore the unused method warning.
     *
     * @return List of options.
     */

    // Getter for the hidden option
    public boolean isHiddenOption() {
        return hiddenOption;
//        new MultivaluedHashMap()
    }

    // Setter for the hidden option (optional)
    public void setHiddenOption(boolean hiddenOption) {
        this.hiddenOption = hiddenOption;
    }

    @SuppressWarnings("unused")
    public List<String> getOptions() {
        return options;
    }
}
