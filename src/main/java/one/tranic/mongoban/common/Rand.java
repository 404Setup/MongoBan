package one.tranic.mongoban.common;

public class Rand {
    /**
     * Generates a random warning ID string of the specified length. The generated warning ID
     * consists of lowercase alphabetical characters and numerical digits.
     *
     * @param length the length of the random warning ID to generate
     * @return a randomly generated warning ID string of the specified length
     */
    public static String generateRandomWarnId(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder warnId = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            warnId.append(chars.charAt(randomIndex));
        }
        return warnId.toString();
    }
}
