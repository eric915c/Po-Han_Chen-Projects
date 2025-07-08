package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class ArgsByteCode extends ByteCode {
    private int numArgs;

    @Override
    public void init(String[] args) {
        numArgs = Integer.parseInt(args[1]);
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.getRunTimeStack().newFrameAt(numArgs);
    }

    @Override
    public String toString() {
        return "ARGS " + numArgs;
    }
}
