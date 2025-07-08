package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class HaltByteCode extends ByteCode {

    @Override
    public void init(String[] args) {
        // No initialization needed for HaltByteCode
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.halt();
    }

    @Override
    public String toString() {
        return "HALT";
    }
}
