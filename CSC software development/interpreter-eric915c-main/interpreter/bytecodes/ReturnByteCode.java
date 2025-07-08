package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class ReturnByteCode extends ByteCode {
    private String label;

    @Override
    public void init(String[] args) {
        if (args.length > 1) {
            label = args[1];
        }
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.popFrame();
        if (vm.isVerbose()) {
            System.out.println(toString());
        }
    }

    @Override
    public String toString() {
        return "RETURN " + (label != null ? label : "");
    }
}
