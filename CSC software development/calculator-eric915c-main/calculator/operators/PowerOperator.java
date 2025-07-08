package calculator.operators;

import calculator.evaluator.Operand;

public class PowerOperator extends Operator {
    @Override
    public int priority() {
        return 3; // High priority for exponentiation
    }

    @Override
    public Operand execute(Operand operandOne, Operand operandTwo) {
        return new Operand((int) Math.pow(operandOne.getValue(), operandTwo.getValue()));
    }
}
