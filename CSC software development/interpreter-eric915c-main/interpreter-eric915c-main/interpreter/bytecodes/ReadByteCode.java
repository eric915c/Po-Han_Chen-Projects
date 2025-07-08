package interpreter.bytecodes;

import interpreter.virtualmachine.VirtualMachine;
import java.util.Scanner;

public class ReadByteCode extends ByteCode {

    @Override
    public void init(String[] args) {
        // No initialization required for Read
    }

    @Override
    public void execute(VirtualMachine vm) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int value = scanner.nextInt();
        vm.getRunTimeStack().push(value);
    }

    @Override
    public String toString() {
        return "READ";
    }
}
