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
    }

    /**
     * Method is being used in the freemarker template, which is why we explicitly ignore the unused method warning.
     *
     * @return List of options.
     */

    // Getter for the hidden option
    public boolean isHiddenOption() {
        return hiddenOption;
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
