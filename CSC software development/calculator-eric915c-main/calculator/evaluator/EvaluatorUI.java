package calculator.evaluator;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluatorUI extends JFrame implements ActionListener {

     private JTextField expressionTextField = new JTextField();
     private JPanel buttonPanel = new JPanel();

     // total of 20 buttons on the calculator,
     // numbered from left to right, top to bottom
     // bText[] array contains the text for corresponding buttons
     private static final String[] buttonText = {
         "7", "8", "9", "+",
         "4", "5", "6", "-",
         "1", "2", "3", "*",
         "(", "0", ")", "/",
         "C", "CE", "=", "^"
     };

     /**
      * C  is for clear, clears entire expression
      * CE is for clear expression, clears last entry up until the last operator.
      */

     public static void main(String argv[]) {
         new EvaluatorUI();
     }

     public EvaluatorUI() {
         setLayout(new BorderLayout());
         this.expressionTextField.setPreferredSize(new Dimension(600, 50));
         this.expressionTextField.setFont(new Font("Courier", Font.BOLD, 24));
         this.expressionTextField.setHorizontalAlignment(JTextField.CENTER);

         add(expressionTextField, BorderLayout.NORTH);
         expressionTextField.setEditable(false);

         add(buttonPanel, BorderLayout.CENTER);
         buttonPanel.setLayout(new GridLayout(5, 4));

         //create 20 buttons with corresponding text in bText[] array
         JButton jb;
         for (String s : EvaluatorUI.buttonText) {
             jb = new JButton(s);
             jb.setFont(new Font("Courier", Font.BOLD, 24));
             jb.addActionListener(this);
             this.buttonPanel.add(jb);
         }

         setTitle("Calculator");
         setSize(400, 400);
         setLocationByPlatform(true);
         setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         setVisible(true);
     }

     /**
      * This function is called anytime a button is pressed
      * on our Calculator GUI.
      * @param actionEventObject Event object generated when a
      *                          button is pressed.
      */
     @Override
     public void actionPerformed(ActionEvent actionEventObject) {
         JButton source = (JButton) actionEventObject.getSource();
         String buttonText = source.getText();

         switch (buttonText) {
             case "C":
                 expressionTextField.setText("");
                 break;
             case "CE":
                 String currentText = expressionTextField.getText();
                 int lastOperatorIndex = Math.max(currentText.lastIndexOf('+'), Math.max(currentText.lastIndexOf('-'), Math.max(currentText.lastIndexOf('*'), currentText.lastIndexOf('/'))));
                 if (lastOperatorIndex > 0) {
                     expressionTextField.setText(currentText.substring(0, lastOperatorIndex + 1));
                 } else {
                     expressionTextField.setText("");
                 }
                 break;
             case "=":
                 Evaluator evaluator = new Evaluator();
                 try {
                     int result = evaluator.evaluateExpression(expressionTextField.getText());
                     expressionTextField.setText(String.valueOf(result));
                 } catch (InvalidTokenException e) {
                     expressionTextField.setText("Error");
                 }
                 break;
             default:
                 expressionTextField.setText(expressionTextField.getText() + buttonText);
                 break;
         }
     }

}
