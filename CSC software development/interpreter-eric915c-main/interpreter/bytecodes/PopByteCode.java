package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class PopByteCode extends ByteCode {
    private int levelsToPop;

    @Override
    public void init(String[] args) {
        if (args.length > 1) {
            levelsToPop = Integer.parseInt(args[1]);
        }
    }

    @Override
    public void execute(VirtualMachine vm) {
        for (int i = 0; i < levelsToPop; i++) {
            vm.getRunTimeStack().pop();
        }
    }

    @Override
    public String toString() {
        return "POP " + levelsToPop;
    }
}
