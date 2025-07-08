package interpreter.virtualmachine;

import interpreter.loaders.Program;
import interpreter.bytecodes.ByteCode;

import java.util.Stack;

public class VirtualMachine {

    private RunTimeStack runTimeStack;
    private Stack<Integer> returnAddress;
    private Program program;
    private int programCounter;
    private boolean isRunning;
    private boolean verbose;

    public VirtualMachine(Program program) {
        this.program = program;
        this.runTimeStack = new RunTimeStack();
        this.returnAddress = new Stack<>();
        this.programCounter = 0;
        this.isRunning = true;
        this.verbose = false;  // Verbose mode is off by default

    }

    /**
     * Execute the program by running each bytecode in the program list
     */
    public void executeProgram() {
        while (isRunning) {
            ByteCode code = program.getCode(programCounter);
            code.execute(this);
            programCounter++;
            // If verbose mode is on, print the bytecode and stack contents
            if (verbose) {
                System.out.println(code);
                runTimeStack.dump();
            }
        }
    }

    public void halt() {
        isRunning = false;
    }

    // Method to enable or disable verbose mode
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int address) {
        this.programCounter = address;
    }

    public void pushReturnAddress(int address) {
        returnAddress.push(address);
    }

    public int popReturnAddress() {
        return returnAddress.pop();
    }




    public RunTimeStack getRunTimeStack() {
        return runTimeStack;
    }
}
