package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;
import interpreter.loaders.Program;

public class GotoByteCode extends ByteCode {
    private String label;
    private int targetAddress;

    @Override
    public void init(String[] args) {
        label = args[1];
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.setProgramCounter(targetAddress);
    }

    @Override
    public void resolve(Program program) {
        targetAddress = program.getAddressForLabel(label);
    }

    @Override
    public String toString() {
        return "GOTO " + label;
    }
}
