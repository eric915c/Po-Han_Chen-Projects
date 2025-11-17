package Server;

import javax.swing.*;
import java.awt.*;

// This class represents the server's graphical interface
public class ServerView extends JFrame {
	public drawingPanel mainPanel;
	public JButton createServer, exit, pauseAndResume, help, hiddenButton;
	public JTextField messageField;
	public JButton sendMessage;

	public ServerControler controler;
	public ServerModel model;

	public ServerView() {
		super("Tank Battle");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }

		getContentPane().setLayout(null);

		// Create the main panel for animation drawing
		mainPanel = new drawingPanel();
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 22, 679, 605);
		mainPanel.setBackground(new Color(128, 64, 0));
		messageField = new JTextField();
		messageField.setBounds(0, 519, 560, 22);
		messageField.setEnabled(false);
		sendMessage = new JButton("Send");
		sendMessage.setBounds(570, 518, 62, 24);
		sendMessage.setFocusable(false);
		mainPanel.add(messageField);
		mainPanel.add(sendMessage);
		getContentPane().add(mainPanel);
		mainPanel.setFocusable(true);

		// Create option buttons
		createServer = new JButton("Create Host");
		createServer.setBounds(0, 0, 120, 22);
		getContentPane().add(createServer);
		createServer.setFocusable(false);

		pauseAndResume = new JButton("Pause/Resume");
		pauseAndResume.setBounds(120, 0, 120, 22);
		getContentPane().add(pauseAndResume);
		pauseAndResume.setFocusable(false);

		help = new JButton("Help");
		help.setBounds(240, 0, 120, 22);
		getContentPane().add(help);
		help.setFocusable(false);

		exit = new JButton("Exit");
		exit.setBounds(360, 0, 120, 22);
		getContentPane().add(exit);
		exit.setFocusable(false);

		// Set up the main frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 130, 640, 590);
		setVisible(true);
		setResizable(false);

		// Set up the server model
		model = new ServerModel(this);

		// Set up the server controller
		controler = new ServerControler(this, model);

		// Play background music
		BackgroundMusic music = new BackgroundMusic();
		music.play("src/main/resources/background.wav");
	}

	public static void main(String[] args) {
		new ServerView();
	}
}
