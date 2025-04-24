package Util;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s\\.]+\\.[^@\\s]+$");

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-postadressen får inte vara tom.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Ogiltig e-postadress format.");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Namnet får inte vara tomt.");
        }
    }

    public static void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Priset måste vara större än eller lika med 0.");
        }
    }

    public static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Kvantiteten måste vara större än eller lika med 0.");
        }
    }

    public static void validateStock(int requestedQuantity, int availableQuantity) {
        validateQuantity(requestedQuantity);

        if (requestedQuantity > availableQuantity) {
            throw new IllegalArgumentException(
                    "Otillräckligt lager. Tillgängligt: " + availableQuantity + ", begärt: " + requestedQuantity
            );
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Lösenordet måste vara minst 6 tecken.");
        }
    }
}