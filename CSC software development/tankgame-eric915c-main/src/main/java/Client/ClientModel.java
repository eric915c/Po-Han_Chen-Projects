package Client;

import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;

public class ClientModel implements ActionListener{
	public ClientView view;

	//Connection variables
	public Socket clientSocket;
	public PrintWriter out;
	public BufferedReader in;
	public String fromServer,  fromUser;
	public String serverIP;

	//Client status
	public boolean serverConnected;
	public boolean gameStarted;
	public boolean gamePaused;
	public boolean gameOver;
	public boolean serverVoteYes, serverVoteNo;
	public boolean clientVoteYes, clientVoteNo;
	public boolean pausePressed;

	//Display messages
	public String[] messageQueue;
	public int messageIndex;
	public String playerTypedMessage = "";


	//textures
	public Image[] textures;

	//The actual game runs on this thread, while the main thread listens for user input
	public client_Ticker t;

	//Game variables
	public static int gameFlow;
	public client_Actor[] drawingList;
	public boolean moveUp;
	public boolean moveDown;
	public boolean moveLeft;
	public boolean moveRight;
	public boolean fire;

	// Rotation flags for tanks
	public boolean rotateLeft;
	public boolean rotateRight;



	public ClientModel(ClientView thisview){
		view = thisview;
		messageQueue = new String[8];
		view.mainPanel.messageQueue = messageQueue;
		addMessage("Welcome to the Tank Battle client!");
		addMessage("Please enter the host IP address and click the 'Connect Host' button to start the game.");

		t = new client_Ticker(1000);
		t.addActionListener(this);

	}

	public void connectServer(){
		addMessage("Connecting to the host.");

		try{
		 	serverIP = view.IPfield.getText();
		 	InetAddress addr = InetAddress.getByName(serverIP);
			clientSocket = new Socket(addr, 9999);

			out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		}catch(Exception e){
			t.stop();
			System.out.println(e);
			addMessage("An error occurred while connecting. Please check: 1. Whether the entered IP is correct; 2. The host is available.");
			return;
		}

		serverConnected = true;
		addMessage("Successfully connected to the host, starting to load the game.");
		view.IPfield.setFocusable(false);
		view.IPfield.setEnabled(false);

		//Load game textures
		textures = new Image[88];
		for(int i = 1; i < textures.length+1; i++)
			textures[i-1] = Toolkit.getDefaultToolkit().getImage("image\\" + i + ".jpg");


		drawingList = new client_Actor[400];

		gameStarted = true;
		view.mainPanel.gameStarted = gameStarted;;
		view.mainPanel.drawingList = drawingList;
		view.messageField.setEnabled(true);
		addMessage("Loading complete, the game has started!");
	}

	public void actionPerformed(ActionEvent e){
		connectServer();

		//Do nothing if the program cannot connect to the server
		if(!serverConnected)
				return;

		//Game logic loop, the client program does not perform any logical calculations, it only accepts drawing instructions
		try{
			while ((fromServer = in.readLine()) != null) {
                fromUser = "";

                gameFlow++;

				if(pausePressed){
					fromUser+= "x;";
					pausePressed = false;
				}

				if(gameOver){
					if(clientVoteNo)
						System.exit(0);

					if(clientVoteYes){
						fromUser+="j;";
						if(serverVoteYes){
							addMessage("The host player has decided to play again, the game has restarted...");
							gameOver = false;
							clientVoteYes = false;
							serverVoteYes = false;
						}
					}
				}

				//Instruction string feedback, tell the server what the client is doing
				fromUser +="m";
				if(moveUp)
					fromUser+= "1";
				else
					fromUser+= "0";
				if(moveDown)
					fromUser+="1";
				else
					fromUser+= "0";
				if(moveLeft)
					fromUser+="1";
				else
					fromUser+= "0";
				if(moveRight)
					fromUser+="1";
				else
					fromUser+= "0";
				if(fire)
					fromUser+="1";
				else
					fromUser+= "0";
				fromUser+=";";

				//Process instructions from the server
				client_instructionHandler.handleInstruction(this, fromServer);

				//Remove one message from the message queue every 10 seconds (if any)
				if(gameFlow%300 == 0)
					removeMessage();

				//Output player tank information
				if(!playerTypedMessage.equals("")){
					fromUser+=playerTypedMessage;
					playerTypedMessage = "";
				}

				//Send feedback instructions
				out.println(fromUser);

				//Call the view to repaint itself
				view.mainPanel.repaint();

				//If switching to dialogue mode, stop all tank actions
				if(!view.mainPanel.hasFocus()){
					moveLeft = false;
					moveUp = false;
					moveDown = false;
					moveRight = false;
					fire = false;
				}
        	}
		}catch(Exception ex){
			ex.printStackTrace();
			t.stop();
			view.messageField.setEnabled(false);
			serverConnected = false;
			gameStarted = false;
			view.mainPanel.gameStarted = false;
			gameOver = false;
			addMessage("The host has exited.");
			view.IPfield.setFocusable(true);
			view.IPfield.setEnabled(true);

			//When an error occurs, close anything that has been created
			try{
				out.close();
				in.close();
				clientSocket.close();
			}catch(Exception exc){
				System.out.println(exc);
			}
		}
	}

	//Display a message on the screen
	public void addMessage(String message){
		if(messageIndex < 8){
			messageQueue[messageIndex] = message;
			messageIndex++;
		}
		else{
			for(int  i = 0; i < 7; i++)
				messageQueue[i] = messageQueue[i+1];
			messageQueue[7] = message;
		}

		//Call the view to repaint the screen if the game has not started
		if(!gameStarted)
			view.mainPanel.repaint();
	}

	//Remove the earliest message on the screen
	public void removeMessage(){
		if(messageIndex == 0)
			return;

		messageIndex--;
		for(int  i = 0; i < messageIndex; i++)
			messageQueue[i] = messageQueue[i+1];
		messageQueue[messageIndex] = null;

		//Call the view to repaint the screen if the game has not started
		if(!gameStarted)
			view.mainPanel.repaint();
	}

	//Add a game object (e.g., tanks, bullets, etc.) to the drawing list
	public void addActor(client_Actor actor){
		for(int i = 0; i < drawingList.length; i ++ )
			if(drawingList[i] == null){
				drawingList[i] = actor;
				break;
			}
	}

	//Remove a game object from the drawing list
	public void removeActor(client_Actor actor){
			for(int i = 0; i < drawingList.length; i ++ )
					if(drawingList[i] == actor){
						drawingList[i] = null;
						break;
			}
	}


}
