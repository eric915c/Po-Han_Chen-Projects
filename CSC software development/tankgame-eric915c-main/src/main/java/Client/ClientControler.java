package Client;

import javax.swing.*;
import java.awt.event.*;

//This class handles input from the client view framework
public class ClientControler {
	public boolean serverConnected;
	public boolean gameStarted;
	public boolean gamePaused;
	public ClientView view;
	public ClientModel model;
	public int helpMessageCount = 1;

	public ClientControler(ClientView thisview, ClientModel thismodel){
		view = thisview;
		model = thismodel;

		//Send message button operation
		view.sendMessage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!model.gameStarted){
					model.addMessage("Connection with the host player has not been established yet, unable to send messages.");
					return;
				}

				if(!view.messageField.getText().equals("")){
					model.addMessage("Client player says:" + view.messageField.getText());
					model.playerTypedMessage += "e" + view.messageField.getText() + ";";
					view.messageField.setText("");
				} else {
					model.addMessage("Message content cannot be empty.");
				}
			}
		});

		//Handle connectServer button operation
		view.connectServer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!model.serverConnected){
					model.serverIP = view.IPfield.getText();
					model.t.start();
				}
			}
		});

		//Handle pauseAndResume button operation
		view.pauseAndResume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!model.gameOver && model.gameStarted){
					model.pausePressed = true;
					if(!model.gamePaused){
						model.gamePaused = true;
						model.addMessage("Client player has paused the game.");
					} else {
						model.gamePaused = false;
						model.addMessage("Client player has resumed the game.");
					}
				}
			}
		});

		// Handle the "Help" button operation
		view.help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addMessage("****************************** Tank Battle ******************************");
				model.addMessage("Help: Press the 's' key to fire bullets, and use the arrow keys on the keyboard to control the tank's movement.");
				model.addMessage("If the keys are not responding, please 1. Turn off Caps Lock; 2. Use the Tab key to switch focus.");
				model.addMessage("If you are using the chat interface, please switch back to the control interface.");
				model.addMessage("Green Tank Controls: 'W' - Forward, 'A' - Left, 'S' - Backward, 'D' - Right, 'Space' - Fire.");
				model.addMessage("Yellow Tank Controls: Arrow keys for movement (Up - Forward, Down - Backward, Left - Left, Right - Right), 'L' - Fire.");
				model.addMessage("********************************************************************************");
				model.addMessage("");
			}
		});

		// Handle the "Exit" button operation
		view.exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Handle keyboard input
		view.messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (helpMessageCount > 0) {
					model.addMessage("Tip: Use the \"Tab\" key to freely switch between the control interface and the chat interface.");
					model.addMessage("Tip: Press the Enter key to directly send your message.");
					helpMessageCount--;
				}

				if (e.getKeyCode() == e.VK_ENTER) { // Check if the Enter key is pressed
					if (!view.messageField.getText().equals("")) {
						model.addMessage("Client-side player says: " + view.messageField.getText());
						model.playerTypedMessage += "e" + view.messageField.getText() + ";"; // Add message with prefix "e"
						view.messageField.setText(""); // Clear the text field
					} else {
						model.addMessage("Message content cannot be empty."); // Display warning if the field is empty
					}
				}
			}
		});


		JPanel temp = view.mainPanel;
		temp.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				//using WASD to control tank
				if(e.getKeyCode() == KeyEvent.VK_W){
					model.moveUp = true;
					model.moveDown = false;
					model.moveLeft = false;
					model.moveRight = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_S){
					model.moveDown = true;
					model.moveUp = false;
					model.moveLeft = false;
					model.moveRight = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_A){
					model.moveLeft = true;
					model.moveUp = false;
					model.moveDown = false;
					model.moveRight = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_D){
					model.moveLeft = false;
					model.moveUp = false;
					model.moveDown = false;
					model.moveRight = true;
				}

				// Handle spacebar for firing bullets
				if(e.getKeyCode() == KeyEvent.VK_SPACE)
					model.fire = true;

				if (e.getKeyCode() == e.VK_ENTER) { // Check if the Enter key is pressed
					if (!view.messageField.getText().equals("")) {
						model.addMessage("Client-side player says: " + view.messageField.getText());
						model.playerTypedMessage += "e" + view.messageField.getText() + ";"; // Add the message to playerTypedMessage
						view.messageField.setText(""); // Clear the text field
					}
				}

				// Player votes Yes/No
				if (e.getKeyChar() == 'y' && model.gameOver && !model.clientVoteYes) {
					model.clientVoteYes = true; // Set the client's vote to Yes
					model.addMessage("Waiting for the host player's response...");
				}


				if(e.getKeyChar() == 'n'  && model.gameOver)
					model.clientVoteNo = true;
			}

			public void keyReleased(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_W)
					model.moveUp = false;
				if(e.getKeyCode() == KeyEvent.VK_S)
					model.moveDown = false;
				if(e.getKeyCode() == KeyEvent.VK_A)
					model.moveLeft = false;
				if(e.getKeyCode() == KeyEvent.VK_D)
					model.moveRight = false;
				if(e.getKeyCode() == KeyEvent.VK_SPACE)
					model.fire = false;
			}
		});
	}
}
