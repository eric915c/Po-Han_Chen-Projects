package Server;

import javax.swing.*;
import java.awt.event.*;

// This class handles input from the server view
public class ServerControler {
	public ServerView view;
	public ServerModel model;
	public int helpMessageCount = 1;

	// Reference to a player's tank
	public ServerControler(ServerView thisview, ServerModel thismodel) {
		view = thisview;
		model = thismodel;

		// Action for the "Send Message" button
		view.sendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!model.gameStarted) {
					model.addMessage("The connection with the other player has not been established yet, unable to send messages.");
					return;
				}

				if (!view.messageField.getText().equals("")) {
					model.addMessage("Host player says: " + view.messageField.getText());
					model.playerTypedMessage += "m" + view.messageField.getText() + ";";
					view.messageField.setText("");
				} else {
					model.addMessage("Message content cannot be empty.");
				}
			}
		});

		// Action for the "Create Host" button
		view.createServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!model.serverCreated)
					model.t.start();
			}
		});

		// Action for the "Pause/Resume" button
		view.pauseAndResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.pausePressed = true;
				if (!model.gameOver && model.gameStarted) {
					if (!model.gamePaused) {
						model.gamePaused = true;
						model.addMessage("Host player has paused the game.");
					} else {
						model.gamePaused = false;
						model.addMessage("Host player has resumed the game.");
					}
				}
			}
		});

		// Action for the "Help" button
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

		// Action for the "Exit" button
		view.exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Action for the input field
		view.messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (helpMessageCount > 0) {
					model.addMessage("Tip: Use the \"Tab\" key to freely switch between the control interface and the chat interface.");
					model.addMessage("Tip: Press the Enter key to directly send your message.");
					helpMessageCount--;
				}

				if (e.getKeyCode() == e.VK_ENTER) {
					if (!view.messageField.getText().equals("")) {
						model.addMessage("Host player says: " + view.messageField.getText());
						model.playerTypedMessage += "m" + view.messageField.getText() + ";";
						view.messageField.setText("");
					} else {
						model.addMessage("Message content cannot be empty.");
					}
				}
			}
		});

		JPanel temp = view.mainPanel;
		temp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (model.P1 != null) {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						model.P1.moveUp = true;
						model.P1.moveDown = false;
						model.P1.moveLeft = false;
						model.P1.moveRight = false;
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						model.P1.moveDown = true;
						model.P1.moveUp = false;
						model.P1.moveLeft = false;
						model.P1.moveRight = false;
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						model.P1.moveLeft = true;
						model.P1.moveUp = false;
						model.P1.moveDown = false;
						model.P1.moveRight = false;
					}
					if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						model.P1.moveLeft = false;
						model.P1.moveUp = false;
						model.P1.moveDown = false;
						model.P1.moveRight = true;
					}

					// Fire action changed to ENTER key
					if (e.getKeyCode() == KeyEvent.VK_L)
						model.P1.fire = true;

					if (e.getKeyCode() == e.VK_ENTER) {
						if (!view.messageField.getText().equals("")) {
							model.addMessage("Host player says: " + view.messageField.getText());
							model.playerTypedMessage += "m" + view.messageField.getText() + ";";
							view.messageField.setText("");
						}
					}

					if (e.getKeyChar() == 'y' && model.gameOver && !model.serverVoteYes) {
						model.serverVoteYes = true;
						model.addMessage("Waiting for the client player's response...");
					}

					if (e.getKeyChar() == 'n' && model.gameOver)
						model.serverVoteNo = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				if (model.P1 != null) {
					if (e.getKeyCode() == KeyEvent.VK_UP)
						model.P1.moveUp = false;
					if (e.getKeyCode() == KeyEvent.VK_DOWN)
						model.P1.moveDown = false;
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
						model.P1.moveLeft = false;
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						model.P1.moveRight = false;
					if (e.getKeyCode() == KeyEvent.VK_L)
						model.P1.fire = false;
				}
			}
		});
	}
}
