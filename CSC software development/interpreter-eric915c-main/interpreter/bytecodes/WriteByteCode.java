package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;

public class WriteByteCode extends ByteCode {

    @Override
    public void init(String[] args) {
        // No arguments needed
    }

    @Override
    public void execute(VirtualMachine vm) {
        System.out.println("Executing WRITE");  // Debug info

        if (vm.getRunTimeStack().getSize() == 0) {
            System.out.println("Error: Stack is empty before WRITE operation.");
            return;
        }

        int topValue = vm.getRunTimeStack().peek();
        if (topValue == -1) {
            System.out.println("Error: Unable to write, invalid value from stack.");
            return;
        }

        System.out.println("Writing value: " + topValue);

        // Print stack status after operation
        vm.getRunTimeStack().dump();
    }

    @Override
    public String toString() {
        return "WRITE";
    }
}
