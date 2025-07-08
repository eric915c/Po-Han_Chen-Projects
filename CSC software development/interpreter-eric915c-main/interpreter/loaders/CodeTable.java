/**
 * Code table of byte codes in language X
 * @key name of a specific byte code
 * @value name of the class that the key belongs to.
 */
package interpreter.loaders;

import java.util.HashMap;

public final class CodeTable {

   private static HashMap<String, String> codeTable = new HashMap<>();
   /**
    * Initializes the CodeTable by mapping bytecode names to their respective class names.
    */

   private CodeTable() {
      // do nothing
   }

   /**
    * fill code table with class name mappings
    */
   public static void init() {
      // Map bytecode names (as they appear in .cod files) to their class names
      codeTable.put("HALT", "HaltByteCode");
      codeTable.put("POP", "PopByteCode");
      codeTable.put("GOTO", "GotoByteCode");
      codeTable.put("CALL", "CallByteCode");
      codeTable.put("FALSEBRANCH", "FalseBranchByteCode");
      codeTable.put("RETURN", "ReturnByteCode");
      codeTable.put("BOP", "BopByteCode");
      codeTable.put("LIT", "LitByteCode");
      codeTable.put("ARGS", "ArgsByteCode");
      codeTable.put("STORE", "StoreByteCode");
      codeTable.put("LOAD", "LoadByteCode");
      codeTable.put("WRITE", "WriteByteCode");
      codeTable.put("READ", "ReadByteCode");
      codeTable.put("LABEL", "LabelCode");
      codeTable.put("VERBOSE", "VerboseByteCode");

   }

   /**
    * Returns the ByteCode class name for a given token.
    * 
    * @param token bytecode to map. For example, HALT --> HaltCode
    * @return class name of bytecode
    */
   public static String get(String token) {
      return codeTable.get(token);
   }

}
