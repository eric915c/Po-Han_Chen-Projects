package calculator.operators;

import calculator.evaluator.Operand;

public class DivideOperator extends Operator {
    @Override
    public int priority() {
        return 2; // Medium priority for division
    }

    @Override
    public Operand execute(Operand operandOne, Operand operandTwo) {
        if (operandTwo.getValue() == 0) {
            throw new ArithmeticException("Division by zero.");
        }
        return new Operand(operandOne.getValue() / operandTwo.getValue());
    }
}
