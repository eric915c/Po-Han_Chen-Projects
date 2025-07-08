package interpreter.loaders;

import interpreter.bytecodes.ByteCode;
import interpreter.bytecodes.LabelCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Program {

    private List<ByteCode> program;
    private HashMap<String, Integer> labelAddresses;  // Map label names to addresses

    /**
     * Instantiates a program object using an
     * ArrayList
     */
    public Program() {
        this.program = new ArrayList<>();
        this.labelAddresses = new HashMap<>();  // Initialize the labelAddresses map here
    }

    /**
     * Gets the size of the current program.
     * @return size of program
     */
    public int getSize() {
        return program.size();
    }

    /**
     * Grabs an instance of a bytecode at an index.
     * @param programCounter index of bytecode to get.
     * @return a bytecode.
     */
    public ByteCode getCode(int programCounter) {
        return program.get(programCounter);
    }

    /**
     * Adds a bytecode instance to the Program List.
     * @param c bytecode to be added
     */
    public void addCode(ByteCode c) {
        if (c instanceof LabelCode) {
            LabelCode labelCode = (LabelCode) c;
            labelAddresses.put(labelCode.getLabel(), program.size());
        }
        program.add(c);
    }

    /**
     * Makes multiple passes through the program ArrayList
     * resolving addrss for Goto,Call and FalseBranch
     * bytecodes. These bytecodes can only jump to Label
     * codes that have a matching label value.
     */
    public void resolveAddresses() {
        for (ByteCode byteCode : program) {
            byteCode.resolve(this);  // ByteCodes like Goto, Call will resolve using this method
        }
    }

    /**
     * Returns the program counter for a given label.
     * @param label the label to be searched.
     * @return the index in program that corresponds to
     * the label.
     */
    public int getAddressForLabel(String label) {
        return labelAddresses.getOrDefault(label, -1);  // Return -1 if label is not found
    }


}
