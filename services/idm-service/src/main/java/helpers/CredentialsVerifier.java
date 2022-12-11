package helpers;


public class CredentialsVerifier {
    private static boolean validatePasswordCharacterRequirement(char[] password) {
        boolean containsUpper = false;
        boolean containsLower = false;
        boolean containsNumber = false;

        for (char c : password) {
            if (containsUpper == true && containsLower == true && containsNumber == true) break;
            else {
                if (Character.isUpperCase(c)) {
                    containsUpper = true;
                    continue;
                }
                if (Character.isLowerCase(c)) {
                    containsLower = true;
                    continue;
                }
                if (Character.isDigit(c)) {
                    containsNumber = true;
                    continue;
                }
            }
        }

        if (containsUpper == false || containsLower == false || containsNumber == false) {
            return false;
        }
        return true;
    }

    public static void verifyEmailAndPassword(char[] password, String email) {
        // Password does not meet length requirements
        if (password.length < 10 || password.length > 20) {
//            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }

        // Password does not meet character requirement
        if (!validatePasswordCharacterRequirement(password)) {
//            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }

        // Email address has invalid length
        if (email.length() < 6 || email.length() > 32) {
//            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }

        // Email address has invalid format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
//            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }
    }
}
