package calculator.evaluator;

import calculator.operators.*;

import java.util.Stack;
import java.util.StringTokenizer;

public class Evaluator {

  private final Stack<Operand> operandStack;
  private final Stack<Operator> operatorStack;

  public Evaluator() {
    operandStack = new Stack<>();
    operatorStack = new Stack<>();
  }

  public int evaluateExpression(String expression) throws InvalidTokenException {
    String expressionToken;
    StringTokenizer expressionTokenizer;
    String delimiters = " +/*-^()"; // Add parentheses to delimiters

    // 3 arguments tell the tokenizer to return delimiters as tokens
    expressionTokenizer = new StringTokenizer(expression, delimiters, true);

    while (expressionTokenizer.hasMoreTokens()) {
      // filter out spaces
      expressionToken = expressionTokenizer.nextToken().trim();
      if (expressionToken.isEmpty()) {
        continue;
      }

      // check if token is an operand
      if (Operand.check(expressionToken)) {
        operandStack.push(new Operand(expressionToken));
      } else if (expressionToken.equals("(")) {
        // Push a marker for '(' onto the operator stack
        operatorStack.push(new Operator() {
          @Override
          public int priority() {
            return -1; // Lowest priority
          }
          @Override
          public Operand execute(Operand op1, Operand op2) {
            throw new UnsupportedOperationException("Marker does not execute.");
          }
        });
      } else if (expressionToken.equals(")")) {
        while (true) {
          if (operatorStack.isEmpty()) {
            break;
          }else if (operatorStack.peek().priority() == -1){
            break;
          }
          Operator operatorFromStack = operatorStack.pop();
          Operand operandTwo = operandStack.pop();
          Operand operandOne = operandStack.pop();
          Operand result = operatorFromStack.execute(operandOne, operandTwo);
          operandStack.push(result);
        }

        if (operatorStack.isEmpty() || operatorStack.peek().priority() != -1) {
          throw new InvalidTokenException("Mismatched parentheses");
        }
        operatorStack.pop(); // Pop the '(' marker
      } else {
        if (!Operator.check(expressionToken)) {
          throw new InvalidTokenException(expressionToken);
        }

        // TODO fix this line of code.
        Operator newOperator = Operator.getOperator(expressionToken);

        // Pop operators from the stack and evaluate them until an operator with lower
        // precedence is encountered or the stack is empty.
        while (true) {
          if (operatorStack.isEmpty() || operatorStack.peek().priority() < newOperator.priority()) {
            break;
          }
          Operator operatorFromStack = operatorStack.pop();
          Operand operandTwo = operandStack.pop();
          Operand operandOne = operandStack.pop();
          Operand result = operatorFromStack.execute(operandOne, operandTwo);
          operandStack.push(result);
        }


        //push new operator onto the stack
        operatorStack.push(newOperator);
      }
    }

    // Once no more tokens need to be scanned from StringTokenizer, we need to
    // evaluate the remaining sub-expressions.
    do {
      if (operatorStack.peek().priority() == -1) {
        throw new InvalidTokenException("Mismatched parentheses");
      }
      Operator operatorFromStack = operatorStack.pop();
      Operand operandTwo = operandStack.pop();
      Operand operandOne = operandStack.pop();
      Operand result = operatorFromStack.execute(operandOne, operandTwo);
      operandStack.push(result);
    }while (!operatorStack.isEmpty());

    if (operandStack.isEmpty()) {
      throw new InvalidTokenException("Expression could not be evaluated.");
    }

    // The last operand on the stack is the result
    return operandStack.pop().getValue();
  }

  public static void main(String[] args) throws InvalidTokenException {
    if (args.length == 1) {
      Evaluator e = new Evaluator();
      System.out.println(e.evaluateExpression(args[0]));
    } else {
      System.err.println("Invalid arguments or No expression given");
    }
  }
}