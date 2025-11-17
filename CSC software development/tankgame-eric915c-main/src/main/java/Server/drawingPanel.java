package Server;

import java.awt.*;
import javax.swing.*;

// The drawingPanel class belongs to the server program
public class drawingPanel extends JPanel {
	public Image offScreenImage;

	// These references point to real objects in the serverModel
	public String[] messageQueue;
	public Actor[] actors;
	public boolean gameStarted;
	public int green, red, blue;

	public drawingPanel() {}

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
			// Draw background
			g.setColor(Color.blue);
			g.drawRect(10, 10, 501, 501);

			// Draw tanks and other objects
			if (actors != null) {
				for (int i = 0; i < actors.length; i++) {
					if (actors[i] != null) {
						actors[i].draw(g);
					}
				}
			}

			// Draw level information
			g.setColor(new Color(81, 111, 230));
			g.drawString("Level: " + level.currentLevel, 527, 39);
			g.drawString("Enemies =  " + level.enemyLeft, 527, 79);

			// Draw minimap
			drawMiniMap(g);

			// Draw victory scene
			if (level.winningCount > 150) {
				int temp = level.winningCount - 150;
				if (temp * 10 > 300) {
					temp = 30;
				}
				if (level.winningCount > 470) {
					temp = 500 - level.winningCount;
				}
				g.setColor(Color.gray);
				g.fillRect(11, 11, 500, temp * 10);
				g.fillRect(11, 500 - temp * 10, 500, (1 + temp) * 10 + 2);

				if (level.winningCount > 190 && level.winningCount < 470) {
					if (level.winningCount > 400) {
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
		}

		// Display messages
		g.setColor(new Color(255, 255, 255));
		if (messageQueue != null) {
			for (int i = 0; i < 8; i++) {
				if (messageQueue[i] != null) {
					g.drawString(messageQueue[i], 5, 12 + i * 16);
				} else {
					break;
				}
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
		if (actors != null) {
			for (Actor actor : actors) {
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
