package interpreter.virtualmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RunTimeStack {
    private List<Integer> runTimeStack;
    private Stack<Integer> framePointer;

    public RunTimeStack() {
        runTimeStack = new ArrayList<>();
        framePointer = new Stack<>();
        // Initial frame starts at index 0.
        framePointer.push(0);
    }

    /**
     * Dumps the contents of the runtime stack.
     */
    public void dump() {
        // Display contents of the runtime stack
        System.out.println(runTimeStack);
    }

    /**
     * Push a value onto the stack.
     */
    public int push(int value) {
        runTimeStack.add(value);
        return value;
    }

    /**
     * Pop the top value from the stack.
     */
    public int pop() {
        if (runTimeStack.isEmpty()) {
            System.out.println("Error: Trying to pop from an empty stack.");
            return -1;
        }
        return runTimeStack.remove(runTimeStack.size() - 1);
    }

    /**
     * Peek the top value without removing it.
     */
    public int peek() {
        if (runTimeStack.isEmpty()) {
            System.out.println("Error: Trying to peek from an empty stack.");
            return -1;
        }
        return runTimeStack.get(runTimeStack.size() - 1);
    }

    /**
     * Create a new frame.
     */
    public void newFrameAt(int offset) {
        framePointer.push(runTimeStack.size() - offset);
    }

    /**
     * Pop the current frame from the stack.
     */
    public void popFrame() {
        int frameStart = framePointer.pop();
        while (runTimeStack.size() > frameStart) {
            runTimeStack.remove(runTimeStack.size() - 1);
        }
    }

    /**
     * Load a value from an offset within the current frame.
     */
    public int load(int offset) {
        int index = framePointer.peek() + offset;
        if (index >= runTimeStack.size()) {
            System.out.println("Error: Index out of bounds.");
            return -1;
        }
        return runTimeStack.get(index);
    }

    /**
     * Store a value into the current frame.
     */
    public int store(int offset) {
        int value = pop();  // pop the top value
        runTimeStack.set(framePointer.peek() + offset, value);
        return value;
    }

    public int getSize() {
        return runTimeStack.size();
    }

    public int getFrameSize() {
        if (framePointer.isEmpty()) {
            return 0;
        }
        return runTimeStack.size() - framePointer.peek();
    }
}
