package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class LitByteCode extends ByteCode {
    private int value;
    private String variableName;  // Optional variable name

    @Override
    public void init(String[] args) {
        value = Integer.parseInt(args[1]);
        if (args.length > 2) {
            variableName = args[2];  // Optional variable name
        }
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.getRunTimeStack().push(value);  // Push the value onto the stack
    }

    @Override
    public String toString() {
        return "LIT " + value + (variableName != null ? " " + variableName : "");
    }
}
