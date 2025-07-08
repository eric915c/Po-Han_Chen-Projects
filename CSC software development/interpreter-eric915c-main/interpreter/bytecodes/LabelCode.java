package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class LabelCode extends ByteCode {
    private String label;

    @Override
    public void init(String[] args) {
        label = args[1];
    }

    @Override
    public void execute(VirtualMachine vm) {
        // Labels don't execute
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "LABEL " + label;
    }
}
