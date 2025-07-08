package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;
import interpreter.loaders.Program;

public abstract class ByteCode {

    /**
     * Initializes the ByteCode with arguments from the .cod file.
     * @param args the arguments used to initialize the ByteCode.
     */
    public abstract void init(String[] args);

    /**
     * Executes the ByteCode on the VirtualMachine.
     * @param vm the virtual machine on which the ByteCode operates.
     */
    public abstract void execute(VirtualMachine vm);

    /**
     * Resolves addresses for ByteCodes like Goto, Call, and FalseBranch.
     * @param program the program where the label addresses can be resolved.
     */
    public void resolve(Program program) {
        // Optional override in ByteCodes that require label resolution
    }

    /**
     * Returns a string representation of the ByteCode.
     * @return the string representation.
     */
    public abstract String toString();
}
