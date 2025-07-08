package interpreter.loaders;

import interpreter.loaders.Program;
import interpreter.loaders.InvalidProgramException;
import interpreter.bytecodes.ByteCode;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class ByteCodeLoader {
    private String codSourceFileName;

    /**
     * Constructs ByteCodeLoader object given a COD source code
     * file name
     * @param fileName name of .cod File to load.
     */
    public ByteCodeLoader(String fileName){
        this.codSourceFileName = fileName;
    }

    /**
     * Loads a program from a .cod file.
     * @return a constructed Program Object.
     * @throws InvalidProgramException thrown when
     * loadCodes fails.
     */
    public Program loadCodes() throws InvalidProgramException {
        Program program = new Program();
        try (BufferedReader br = new BufferedReader(new FileReader(codSourceFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Create a new ByteCode instance from the line and add it to the program
                String[] tokens = line.split("\\s+");
                String byteCodeName = tokens[0];
                String byteCodeClass = CodeTable.get(byteCodeName);

                if (byteCodeClass == null) {
                    throw new InvalidProgramException("ByteCode not recognized: " + byteCodeName);
                }

                ByteCode byteCode = (ByteCode) Class.forName("interpreter.bytecodes." + byteCodeClass).getDeclaredConstructor().newInstance();
                byteCode.init(tokens);
                program.addCode(byteCode);
            }
        } catch (IOException | ReflectiveOperationException e) {
            throw new InvalidProgramException("Failed to load codes: " + e.getMessage());
        }
        return program;
    }
}
