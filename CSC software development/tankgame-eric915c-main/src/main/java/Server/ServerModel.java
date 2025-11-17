package Server;

import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;

public class ServerModel implements ActionListener {
	// Reference to the view
	public ServerView view;

	// Connection variables
	public ServerSocket serverSocket;
	public Socket clientSocket;
	public PrintWriter out;
	public BufferedReader in;
	public String inputLine, outputLine;

	// Server status
	public boolean serverCreated;
	public boolean clientConnected;
	public boolean gameStarted;
	public boolean gamePaused;
	public boolean gameOver;
	public boolean serverVoteYes, serverVoteNo;
	public boolean clientVoteYes, clientVoteNo;
	public boolean pausePressed;

	// Game messages
	public String[] messageQueue;
	public int messageIndex;
	public String playerTypedMessage = "";

	// The actual game runs on this thread, while the main thread listens to user input
	public Ticker t;

	public Image[] textures;

	// Game variables
	public static int gameFlow;
	public Actor[] actors;
	public player P1;   // Tank controlled by the server player
	public player P2;   // Tank controlled by the client player

	public ServerModel(ServerView thisview) {
		view = thisview;
		messageQueue = new String[8];
		view.mainPanel.messageQueue = messageQueue;

		addMessage("Welcome to Tank Battle Host! Please click the \"Create Host\" button to start the game.");

		t = new Ticker(1000);
		t.addActionListener(this);
	}

	public void createServer() {
		addMessage("Creating host (port 9999)...");

		try {
			serverSocket = new ServerSocket(9999);
			serverCreated = true;
		} catch (Exception e) {
			addMessage("Unable to create host. Please ensure port 9999 is not used by another program.");
			System.out.println(e);
			t.stop();
			return;
		}

		addMessage("Host created. Waiting for a player to connect...");

		try {
			clientSocket = serverSocket.accept();
			clientConnected = true;

			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (Exception e) {
			addMessage("Error during connection. Please create the host again.");
			serverCreated = false;
			clientConnected = false;
			t.stop();

			// Destroy everything created in case of an error
			try {
				serverSocket.close();
				clientSocket.close();
				out.close();
				in.close();
			} catch (Exception ex) {}

			return;
		}

		view.messageField.setEnabled(true);
		addMessage("Player connected. Loading the game...");

		// Once the client connects, notify the client to start loading the game
		out.println("L1;");

		// Load the game
		textures = new Image[88];
		for (int i = 1; i < textures.length + 1; i++)
			textures[i - 1] = Toolkit.getDefaultToolkit().getImage("image\\" + i + ".jpg");

		// Set up the first level
		actors = new Actor[400];
		level.loadLevel(this);

		P1 = new player("1P", this);
		addActor(P1);
		P2 = new player("2P", this);
		addActor(P2);

		gameStarted = true;
		view.mainPanel.actors = actors;
		view.mainPanel.gameStarted = true;

		addMessage("Game loaded. Let the battle begin!");
	}

	public void actionPerformed(ActionEvent e) {
		createServer();

		// Do nothing if the server was not successfully created
		if (!serverCreated)
			return;

		// Game logic loop
		try {
			while ((inputLine = in.readLine()) != null) {
				// Handle feedback messages from the client
				feedbackHandler.handleInstruction(this, inputLine);

				outputLine = "";

				if (!gamePaused)
					gameFlow++;

				if (pausePressed) {
					if (!gamePaused) {
						outputLine += "x0;";
					} else {
						outputLine += "x1;";
					}
					pausePressed = false;
				}

				if (gameOver || (P1.life == 0 && P2.life == 0)) {
					if (P1.freezed != 1)
						outputLine += "a;";

					if ((P1.freezed != 1 || messageIndex == 1) && serverVoteYes) {
						addMessage("Waiting for the client player's response...");
					}
					if (P1.freezed != 1 || messageIndex == 0) {
						addMessage("GAME OVER! Would you like to play again? (y / n)");
					}
					gameOver = true;
					P1.freezed = 1;
					P2.freezed = 1;

					if (serverVoteNo && !serverVoteYes)
						System.exit(0);

					if (serverVoteYes) {
						outputLine += "j;";
						if (clientVoteYes) {
							addMessage("Client player agreed to play again. Restarting the game...");

							// Restart the game
							P1 = new player("1P", this);
							P2 = new player("2P", this);
							level.reset();
							level.loadLevel(this);
							gameOver = false;
							serverVoteYes = false;
							clientVoteYes = false;
							serverVoteNo = false;
							enemy.freezedMoment = 0;
							enemy.freezedTime = 0;
							gameFlow = 0;

							// Notify the client to restart the game
							outputLine += "L1;";
						}
					}
				}

				if (level.deathCount == 20 && !gameOver) {
					level.winningCount++;
					if (level.winningCount == 120) {
						P1.freezed = 1;
						P2.freezed = 1;
					}
					if (level.winningCount == 470) {
						if (P1.life > 0)
							P1.reset();
						if (P2.life > 0)
							P2.reset();
						level.loadLevel(this);
						// Notify the client to load the next level
						outputLine += "L" + (1 + (level.currentLevel - 1) % 8) + ";";
					}
					if (level.winningCount == 500) {
						P1.freezed = 0;
						P2.freezed = 0;
						level.deathCount = 0;
						level.winningCount = 0;
					}
				}

				// Spawn enemy tanks frequently
				if (!gamePaused)
					level.spawnEnemy(this);

				for (int i = 0; i < actors.length; i++) {
					if (actors[i] != null)
						actors[i].move();
				}

				// Remove a message from the queue every 10 seconds (if there is any)
				if (gameFlow % 300 == 0)
					removeMessage();

				// Add player and level information to the output line
				outputLine += "p" + level.enemyLeft + "," + level.currentLevel + "," + P1.life + "," + P1.scores + "," + P2.life + "," + P2.scores + ";";
				outputLine += "g" + level.winningCount + ";";

				// Add player-typed message information to the output line
				if (!playerTypedMessage.equals("")) {
					outputLine += playerTypedMessage;
					playerTypedMessage = "";
				}

				// Send the final instruction string to the client program
				out.println(outputLine);

				// Trigger the view to repaint itself
				view.mainPanel.repaint();

				// Stop all tank actions if the player switches to chat mode
				if (!view.mainPanel.hasFocus()) {
					P1.moveLeft = false;
					P1.moveUp = false;
					P1.moveDown = false;
					P1.moveRight = false;
					P1.fire = false;
				}

				Thread.sleep(30);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			view.messageField.setEnabled(false);
			serverVoteYes = false;
			serverVoteNo = false;
			clientVoteYes = false;
			serverCreated = false;
			gameStarted = false;
			gameOver = false;
			gameFlow = 0;
			enemy.freezedTime = 0;
			enemy.freezedMoment = 0;
			view.mainPanel.gameStarted = false;
			t.stop();
			addMessage("Client player has disconnected. Please create the host again.");

			// Destroy everything in case of an error during the game
			try {
				out.close();
				in.close();
				clientSocket.close();
				serverSocket.close();
			} catch (Exception exc) {}

			// Reset game data
			P1 = null;
			P2 = null;
			level.reset();
		}
	}

	// Add a game object (e.g., tanks, bullets, etc.) to the game system
	public void addActor(Actor actor) {
		for (int i = 0; i < actors.length; i++)
			if (actors[i] == null) {
				actors[i] = actor;
				break;
			}
	}

	// Remove a game object from the game system
	public void removeActor(Actor actor) {
		for (int i = 0; i < actors.length; i++)
			if (actors[i] == actor) {
				actors[i] = null;
				break;
			}
	}

	// Display a message on the screen
	public void addMessage(String message) {
		if (messageIndex < 8) {
			messageQueue[messageIndex] = message;
			messageIndex++;
		} else {
			for (int i = 0; i < 7; i++)
				messageQueue[i] = messageQueue[i + 1];
			messageQueue[7] = message;
		}

		// Trigger the view to repaint the screen if the game has not started
		if (!gameStarted)
			view.mainPanel.repaint();
	}

	// Remove the oldest message from the screen
	public void removeMessage() {
		if (messageIndex == 0)
			return;

		messageIndex--;
		for (int i = 0; i < messageIndex; i++)
			messageQueue[i] = messageQueue[i + 1];
		messageQueue[messageIndex] = null;

		// Trigger the view to repaint the screen if the game has not started
		if (!gameStarted)
			view.mainPanel.repaint();
	}
}
