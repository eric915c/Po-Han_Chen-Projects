package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class LoadByteCode extends ByteCode {
    private int offset;
    private String id;

    @Override
    public void init(String[] args) {
        offset = Integer.parseInt(args[1]);
        if (args.length > 2) {
            id = args[2];
        }
    }

    @Override
    public void execute(VirtualMachine vm) {
        vm.loadRunStack(offset);
        if (vm.isVerbose()) {
            System.out.println(toString());
        }
    }

    @Override
    public String toString() {
        return "LOAD " + offset + (id != null ? " " + id : "");
    }
}
