package calculator.evaluator;

/**
 * Operand class used to represent an operand
 * in a valid mathematical expression.
 */
public class Operand {
    private final int value;

    /**
     * Construct operand from string token.
     * @param token String token representing an operand.
     */
    public Operand(String token) {
        this.value = Integer.parseInt(token);
    }

    /**
     * Construct operand from integer.
     * @param value Integer value of the operand.
     */
    public Operand(int value) {
        this.value = value;
    }

    /**
     * Return value of operand.
     * @return The integer value of the operand.
     */
    public int getValue() {
        return value;
    }

    /**
     * Check to see if given token is a valid
     * operand.
     * @param token The token to check.
     * @return True if the token is a valid operand, false otherwise.
     */
    public static boolean check(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
