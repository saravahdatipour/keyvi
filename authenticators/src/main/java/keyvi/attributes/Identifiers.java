package keyvi.attributes;

public class Identifiers {

    public enum IrmaDemoMijnOverheid {
        AGE_LOWER_OVER_18("irma-demo.MijnOverheid.ageLower.over18"),
        ADDRESS_COUNTRY("irma-demo.MijnOverheid.address.country"),
        ADDRESS_CITY("irma-demo.MijnOverheid.address.city");

        private final String identifier;

        IrmaDemoMijnOverheid(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    public enum Pbdf {
        EMAIL_EMAIL("pbdf.pbdf.email.email");

        private final String identifier;

        Pbdf(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    public enum IrmaDemoRU {
        STUDENT_CARD_UNIVERSITY("irma-demo.RU.studentCard.university");

        private final String identifier;

        IrmaDemoRU(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}
