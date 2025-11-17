package Server;

import java.awt.*;

public class enemy implements Actor {
	public final int UP = 0;
	public final int DOWN = 1;
	public final int LEFT = 2;
	public final int RIGHT = 3;
	public final int size = 12;
	public final Rectangle map = new Rectangle(35, 35, 452, 452);
	public static int freezedTime;
	public static int freezedMoment;
	public int numberOfBullet;
	public int coolDownTime;
	public int type;
	public int speed;
	public int direction;
	public int interval;
	public int health;
	public int xPos, yPos, xVPos, yVPos;
	public Rectangle border;
	public boolean flashing;
	public double firePosibility;
	public Image[] textures;
	public ServerModel gameModel;

	public enemy(int type, boolean flashing, int xPos, int yPos, ServerModel gameModel) {
		this.type = type;
		this.xPos = xPos;
		this.yPos = yPos;
		this.flashing = flashing;
		this.gameModel = gameModel;

		// Set common attributes for all enemies
		interval = (int) (Math.random() * 200);
		direction = (int) (Math.random() * 4);
		numberOfBullet = 1;
		xVPos = xPos;
		yVPos = yPos;
		border = new Rectangle(xPos - size, yPos - size, 25, 25);

		// Set unique attributes for different enemy types such as appearance, speed, etc.
		if (type == 1) {
			firePosibility = 0.95;
			speed = 2;
			textures = new Image[8];
			for (int i = 0; i < 8; i++)
				textures[i] = gameModel.textures[38 + i];
		} else if (type == 2) {
			firePosibility = 0.95;
			speed = 4;
			textures = new Image[8];
			for (int i = 0; i < 8; i++)
				textures[i] = gameModel.textures[2 + i];
		} else if (type == 3) {
			firePosibility = 0.9;
			speed = 2;
			textures = new Image[8];
			for (int i = 0; i < 8; i++)
				textures[i] = gameModel.textures[10 + i];
		} else {
			firePosibility = 0.95;
			health = 3;
			speed = 2;
			textures = new Image[20];
			for (int i = 0; i < 20; i++)
				textures[i] = gameModel.textures[18 + i];
		}
	}

	public void move() {
		if (gameModel.gamePaused) {
			writeToOutputLine();
			return;
		}

		if (freezedTime > ServerModel.gameFlow - freezedMoment) {
			writeToOutputLine();
			return;
		}

		// The enemy tank will continue to move in the same direction within one interval (if unaffected by other objects).
		// At the end of each interval, it will turn in a new direction.
		if (interval > 0)
			interval--;
		if (interval == 0) {
			interval = (int) (Math.random() * 200);
			int newDirection = (int) (Math.random() * 4);
			if (direction != newDirection) {
				if (direction / 2 != newDirection / 2) {
					xPos = xVPos;
					yPos = yVPos;
					border.x = xPos - size;
					border.y = yPos - size;
				}
				direction = newDirection;
			}
		}

		// Randomly decide whether to fire a bullet. Enemy tanks cannot fire if the first bullet is not destroyed.
		if (coolDownTime > 0)
			coolDownTime--;
		if (Math.random() > firePosibility && coolDownTime == 0 && numberOfBullet > 0) {
			// Get bullet direction
			int c = direction;
			// Get bullet position
			int a, b;
			if (direction == UP) {
				a = xPos;
				b = yPos - size;
			} else if (direction == DOWN) {
				a = xPos;
				b = yPos + size;
			} else if (direction == LEFT) {
				a = xPos - size;
				b = yPos;
			} else {
				a = xPos + size;
				b = yPos;
			}
			// Get bullet speed
			int d;
			if (type == 3) {
				d = 12;
			} else {
				d = 7;
			}
			// Add bullet
			gameModel.addActor(new bullet(a, b, c, d, 1, this, gameModel));
			coolDownTime = 7;
			if (type == 3)
				coolDownTime = 5;
			numberOfBullet--;
		}

		// Save current position information. If new moves are invalid, revert changes.
		int xPosTemp = xPos;
		int yPosTemp = yPos;
		Rectangle borderTemp = new Rectangle(xPosTemp - size, yPosTemp - size, 25, 25);

		// Define the next border of the enemy tank assuming valid movement based on direction
		if (direction == UP) {
			yPos -= speed;
		} else if (direction == DOWN) {
			yPos += speed;
		} else if (direction == LEFT) {
			xPos -= speed;
		} else {
			xPos += speed;
		}

		// Update border
		border.y = yPos - size;
		border.x = xPos - size;

		// Check if the next border intersects the map boundary. If not, randomly generate direction.
		if (!border.intersects(map)) {
			direction = (int) (Math.random() * 4);
			interval = (int) (Math.random() * 250);
			xPos = xVPos;
			yPos = yVPos;
			border.x = xPos - size;
			border.y = yPos - size;
			writeToOutputLine();
			return;
		}

		// Check if the next border intersects other objects, such as player-controlled tanks, walls, etc.
		for (int i = 0; i < gameModel.actors.length; i++) {
			if (gameModel.actors[i] != null) {
				if (this != gameModel.actors[i]) {
					if (border.intersects(gameModel.actors[i].getBorder())) {
						// Static objects such as rivers, walls, etc.
						if (gameModel.actors[i].getType().equals("steelWall") || gameModel.actors[i].getType().equals("wall")) {
							if (!gameModel.actors[i].walldestoried()) {
								for (int j = 0; j < gameModel.actors[i].getDetailedBorder().length; j++) {
									if (gameModel.actors[i].getDetailedBorder()[j] != null) {
										if (gameModel.actors[i].getDetailedBorder()[j].intersects(border)) {
											if (Math.random() > 0.90)
												direction = (int) (Math.random() * 4);
											xPos = xVPos;
											yPos = yVPos;
											border.x = xPos - size;
											border.y = yPos - size;
											writeToOutputLine();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Find the virtual position of the tank when it is at a 90-degree angle and adjust the real position using the virtual position
		int a = (xPos - 10) / 25;
		int b = (xPos - 10) % 25;
		if (b < 7)
			b = 0;
		if (b > 18)
			b = 25;
		if ((b < 19 && b > 6) || xPos < 17 || xPos > 492)
			b = 13;
		xVPos = a * 25 + b + 10;
		int c = (yPos - 10) / 25;
		int d = (yPos - 10) % 25;
		if (d < 7)
			d = 0;
		if (d > 18)
			d = 25;
		if ((d < 19 && d > 6) || yPos < 17 || yPos > 492)
			d = 13;
		yVPos = c * 25 + d + 10;
		writeToOutputLine();
	}

	public void writeToOutputLine() {
		// Write changes to the output line
		gameModel.outputLine += "n" + xPos + "," + yPos + ",";
		int textureIndex = 0;
		if (flashing && gameModel.gameFlow % 10 > 4) {
			if (type == 1)
				textureIndex = 42 + direction;
			else if (type == 2)
				textureIndex = 6 + direction;
			else if (type == 3)
				textureIndex = 14 + direction;
			else
				textureIndex = 34 + direction;
		} else {
			if (type == 1)
				textureIndex = 38 + direction;
			else if (type == 2)
				textureIndex = 2 + direction;
			else if (type == 3)
				textureIndex = 10 + direction;
			else {
				if (health == 3)
					textureIndex = 18 + direction;
				else if (health == 2)
					textureIndex = 22 + direction;
				else if (health == 1)
					textureIndex = 26 + direction;
				else
					textureIndex = 30 + direction;
			}
		}
		gameModel.outputLine += "" + textureIndex + ";";
	}

	public void hurt() {
		if (flashing)
			gameModel.addActor(new powerUp(gameModel));
		flashing = false;
		boolean death = false;
		if (type != 4)
			death = true;
		else {
			if (health == 0)
				death = true;
			else {
				if (health == 3) {
					for (int i = 0; i < 4; i++)
						textures[i] = textures[4 + i];
				} else if (health == 2) {
					for (int i = 0; i < 4; i++)
						textures[i] = textures[8 + i];
				} else if (health == 1) {
					for (int i = 0; i < 4; i++)
						textures[i] = textures[12 + i];
				}
				health--;
			}
		}

		if (death) {
			level.NoOfEnemy--;
			level.deathCount++;
			gameModel.removeActor(this);
			gameModel.addActor(new bomb(xPos, yPos, "big", gameModel));
		}
	}

	public String getType() {
		return "enemy";
	}

	public void draw(Graphics g) {
		if (flashing && gameModel.gameFlow % 10 > 4)
			g.drawImage(textures[textures.length - 4 + direction], xPos - size, yPos - size, null);
		else
			g.drawImage(textures[direction], xPos - size, yPos - size, null);
	}

	public Rectangle getBorder() {
		return border;
	}

	// Unused methods
	public Rectangle[] getDetailedBorder() {
		return null;
	}

	public boolean walldestoried() {
		return false;
	}
	public int getxPos() {
		return this.xPos;
	}

	public int getyPos() {
		return this.yPos;
	}
}
