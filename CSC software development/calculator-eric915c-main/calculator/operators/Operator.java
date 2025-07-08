package calculator.operators;

import calculator.evaluator.Operand;

public abstract class Operator {

    /**
     * Retrieve the priority of an Operator.
     * @return Priority of the Operator as an int.
     */
    public abstract int priority();

    /**
     * Abstract method to execute an operator given two operands.
     * @param operandOne First operand of the operator.
     * @param operandTwo Second operand of the operator.
     * @return An Operand that is the result of the operation.
     */
    public abstract Operand execute(Operand operandOne, Operand operandTwo);

    /**
     * Retrieve an operator instance based on the token.
     * @param token The token representing an operator.
     * @return Reference to an Operator instance, or null if not found.
     */
    public static Operator getOperator(String token) {
        switch (token) {
            case "+":
                return new AddOperator();
            case "-":
                return new SubtractOperator();
            case "*":
                return new MultiplyOperator();
            case "/":
                return new DivideOperator();
            case "^":
                return new PowerOperator();
            default:
                return null;
        }
    }

    /**
     * Determines if a given token is a valid operator.
     * @param token The token to check.
     * @return True if the token is a valid operator, false otherwise.
     */
    public static boolean check(String token) {
        return getOperator(token) != null;
    }
}
