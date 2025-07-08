package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class VerboseByteCode extends ByteCode {
    private String verboseMode;

    @Override
    public void init(String[] args) {
        verboseMode = args[1];  // Expecting either "ON" or "OFF"
    }

    @Override
    public void execute(VirtualMachine vm) {
        if (verboseMode.equals("ON")) {
            vm.setVerbose(true);  // Turn on verbose mode
        } else if (verboseMode.equals("OFF")) {
            vm.setVerbose(false);  // Turn off verbose mode
        }
    }

    @Override
    public String toString() {
        return "VERBOSE " + verboseMode;
    }
}
