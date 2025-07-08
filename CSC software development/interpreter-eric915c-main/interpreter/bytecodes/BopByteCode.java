package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class BopByteCode extends ByteCode {
    private String operator;

    @Override
    public void init(String[] args) {
        operator = args[1];  // The operator to perform (e.g., +, -, *, etc.)
    }

    @Override
    public void execute(VirtualMachine vm) {
        if (vm.getRunTimeStack().getSize() < 2) {
            System.out.println("Error: Not enough operands on the stack for binary operation.");
            return;  // Exit if there aren’t enough operands
        }

        int rightOperand = vm.getRunTimeStack().pop();
        int leftOperand = vm.getRunTimeStack().pop();
        int result = 0;

        switch (operator) {
            case "+":
                result = leftOperand + rightOperand;
                break;
            case "-":
                result = leftOperand - rightOperand;
                break;
            case "*":
                result = leftOperand * rightOperand;
                break;
            case "/":
                if (rightOperand == 0) {
                    System.out.println("Error: Division by zero.");
                    return;
                }
                result = leftOperand / rightOperand;
                break;
            default:
                System.out.println("Error: Unsupported binary operator.");
                return;
        }

        vm.getRunTimeStack().push(result);  // Push the result back onto the stack
    }

    @Override
    public String toString() {
        return "BOP " + operator;
    }
}
