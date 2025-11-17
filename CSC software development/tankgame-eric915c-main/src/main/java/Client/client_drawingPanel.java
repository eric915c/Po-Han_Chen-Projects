package Client;

import java.awt.*;
import javax.swing.*;

// Drawing panel class for the client program
public class client_drawingPanel extends JPanel {
	public Image offScreenImage;

	public String[] messageQueue; // Message queue to display messages
	public client_Actor[] drawingList; // List of drawable actors
	public boolean gameStarted; // Flag indicating if the game has started
	public int green, red, blue;
	public int P1Life, P2Life, P1Score, P2Score, EnemyLeft, LevelIndex; // Player lives, scores, enemy count, and level index
	public Image P1Image, P2Image; // Player images

	public client_drawingPanel() {
		P1Image = Toolkit.getDefaultToolkit().getImage("image\\" + 55 + ".jpg");
		P2Image = Toolkit.getDefaultToolkit().getImage("image\\" + 73 + ".jpg");
	}

	public void paintComponent(Graphics g) {
		Graphics offScreenGraphics;
		if (offScreenImage == null) {
			offScreenImage = createImage(640, 550);
		}
		offScreenGraphics = offScreenImage.getGraphics();
		myPaint(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, this);
	}

	public void myPaint(Graphics g) {
		super.paintComponent(g);

		if (gameStarted) {
			// Draw game information
			g.setColor(new Color(81, 111, 230));
			g.drawString("Level: " + LevelIndex, 527, 39);
			g.drawString("Enemies = " + EnemyLeft, 527, 79);

			g.setColor(Color.yellow);
			g.drawImage(P1Image, 520, 380, null);
			g.drawString("x", 555, 395);
			g.drawString(P1Life + "", 565, 396);
			String SCORE = "000000000" + P1Score;
			g.drawString("P1" + " point:" + "", 515, 370);
			g.drawString(SCORE.substring(SCORE.length() - 7, SCORE.length()) + "", 566, 370);

			g.setColor(Color.green);
			g.drawImage(P2Image, 520, 460, null);
			g.drawString("x", 555, 475);
			g.drawString(P2Life + "", 565, 476);
			SCORE = "000000000" + P2Score;
			g.drawString("P2" + " point:" + "", 515, 450);
			g.drawString(SCORE.substring(SCORE.length() - 7, SCORE.length()) + "", 566, 450);

			// Draw background
			g.setColor(Color.blue);
			g.drawRect(10, 10, 501, 501);

			// Draw tanks and other game objects
			if (drawingList != null)
				for (int i = 0; i < drawingList.length; i++)
					if (drawingList[i] != null)
						drawingList[i].draw(g);

			// Draw winning scene
			if (client_level.winningCount > 150) {
				int temp = client_level.winningCount - 150;
				if (temp * 10 > 300)
					temp = 30;
				if (client_level.winningCount > 470)
					temp = 500 - client_level.winningCount;
				g.setColor(Color.gray);
				g.fillRect(11, 11, 500, temp * 10);
				g.fillRect(11, 500 - temp * 10, 500, (1 + temp) * 10 + 2);

				if (client_level.winningCount > 190 && client_level.winningCount < 470) {
					if (client_level.winningCount > 400) {
						red += (int) ((128 - red) * 0.2);
						green += (int) ((128 - green) * 0.2);
					}
					g.setColor(new Color(red, green, blue));
					g.drawString("NEXT LEVEL", 240, 250);
				}
			} else {
				green = 23;
				red = 34;
				blue = 128;
			}

			// Draw the minimap
			drawMiniMap(g);
		}

		// Messages
		g.setColor(new Color(255, 255, 255));
		if (messageQueue != null) {
			for (int i = 0; i < 8; i++) {
				if (messageQueue[i] != null)
					g.drawString(messageQueue[i], 5, 12 + i * 16);
				else
					break;
			}
		}
	}

	// Method to draw the minimap
	public void drawMiniMap(Graphics g) {
		// Set the size of the minimap
		int miniMapWidth = 100;  // Reduced width
		int miniMapHeight = 100; // Reduced height
		int miniMapX = 515;      // Adjusted X position
		int miniMapY = 170;      // Adjusted Y position (moved down to avoid blocking)

		// Draw minimap background
		g.setColor(Color.BLACK);
		g.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

		// Draw borders for the minimap
		g.setColor(Color.WHITE);
		g.drawRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

		// Scale factors for the minimap
		double scaleX = miniMapWidth / 500.0;  // Scale relative to game area
		double scaleY = miniMapHeight / 500.0;

		// Draw actors on the minimap
		if (drawingList != null) {
			for (client_Actor actor : drawingList) {
				if (actor != null) {
					int actorX = (int) (actor.getxPos() * scaleX + miniMapX);
					int actorY = (int) (actor.getyPos() * scaleY + miniMapY);

					// Set color based on actor type
					if (actor.getType().equals("Player")) {
						g.setColor(Color.GREEN);
					} else if (actor.getType().equals("enemy")) {
						g.setColor(Color.RED);
					} else if (actor.getType().equals("bullet")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GRAY); // Default for other objects
					}

					// Draw actor as a small rectangle on the minimap
					g.fillRect(actorX, actorY, 4, 4);
				}
			}
		}
	}

}
