package Server;

import java.awt.*;

public class player implements Actor {
	public final int UP = 0;
	public final int DOWN = 1;
	public final int LEFT = 2;
	public final int RIGHT = 3;
	public final int size = 12;
	public final Rectangle map = new Rectangle(35, 35, 452, 452);
	public int scores;
	public String type;
	public int life;
	public int speed;
	public int direction;
	public int InvulnerableTime;
	public int freezed;
	public int freezedTime;
	public boolean moveUp;
	public boolean moveDown;
	public boolean moveLeft;
	public boolean moveRight;
	public boolean fire;
	public int numberOfBullet;
	public int coolDownTime;
	public int status;
	public int health;
	public int xPos, yPos, xVPos, yVPos;
	public Rectangle border;
	public Image standardImage;
	public Image[] textures;
	public ServerModel gameModel;

	public player(String type, ServerModel gameModel){
		this.type = type;
		life = 3;
		direction = UP;
		status = 1;
		health = 1;
		numberOfBullet = 1;
		InvulnerableTime = 150;
		this.gameModel = gameModel;

		textures = new Image[4];
		if(type.equals( "1P")){
			//Initial position for Player 1
			xPos  = 198;
			yPos = 498;
			//Textures for Player 1
			for(int i = 0; i < 4; i ++)
				textures[i] = gameModel.textures[54+i];
			standardImage = textures[0];
		}else{
			//Initial position for Player 2
			xPos  = 323;
			yPos = 498;
			//Textures for Player 2
			for(int i = 0; i < 4; i ++)
				textures[i] = gameModel.textures[72+i];
			standardImage = textures[0];
		}

		xVPos = xPos;
		yVPos = yPos;
		border = new Rectangle(xPos - size, yPos - size, 25, 25);

	}

	public void move(){
       if(gameModel.gamePaused){
       		writeToOutputLine();
		    return;
		}

       if(coolDownTime > 0)
			coolDownTime--;
		if(InvulnerableTime > 0)
			InvulnerableTime--;

		 if(freezed == 1){
        	writeToOutputLine();
        	return;
		}

		//If the player clicks the "Fire" button and the conditions are met, a bullet target is created (i.e. a bullet is fired)
		if(fire && coolDownTime == 0 && numberOfBullet > 0){
			//Play bullet shooting sound
			SoundPlayer.playSound("src/main/resources/bullet.wav");
			//Bullet direction
			int c = direction;
			//Bullet position
			int a, b;
			if(direction == UP){
				a = xPos; b = yPos - size;
			}else if(direction == DOWN){
				a = xPos; b = yPos + size;
			}else if(direction == LEFT){
				a = xPos - size; b = yPos;
			}else{
				a = xPos + size; b = yPos;
			}
			//Bullet speed
			int d;
			if(status == 1){
				numberOfBullet = 1;
				d = 7;
			}else{
				d = 12;
			}
			//Bullet capability
			int e;
			if(status == 4){
				e = 2;
			}else{
				e = 1;
			}
			//Add bullet
			gameModel.addActor(new bullet(a,b,c,d,e, this, gameModel));

			if(status> 2)
				coolDownTime = 5;
			else
				coolDownTime = 8;
			//Reduce the number of bullets available, numberOfBullet will increase when bullets are fired
			//Target hit by the player's tank (e.g. wall, enemy tank, etc.);
			numberOfBullet--;
		}


		//Store current position
		int xPosTemp = xPos;
		int yPosTemp = yPos;
		Rectangle borderTemp = new Rectangle(xPosTemp - size, yPosTemp - size, 25,25);

		//Define the next boundary of the player tank based on the movement of the player tank, assuming its next movement is valid;
		boolean notMoving = false;
		if(moveUp){
			if(direction != UP && direction != DOWN)
				xPos = xVPos;
			yPos-=speed;
			direction = UP;
		}else if(moveDown){
			if(direction != UP && direction != DOWN)
				xPos = xVPos;
			yPos+=speed;
			direction = DOWN;
		}else if(moveLeft){
			if(direction != LEFT && direction != RIGHT)
				yPos = yVPos;
			xPos-=speed;
			direction = LEFT;
		}else if(moveRight){
			if(direction != LEFT && direction != RIGHT)
				yPos = yVPos;
			xPos+=speed;
			direction = RIGHT;
		}else{
				notMoving = true;
		}
		if(notMoving){
			if(speed > 0)
				speed--;
		}else{
			if(speed < 3)
				speed++;
		}

		//Update boundaries
		border.y = yPos - size;
		border.x = xPos - size;

		//Check if the next boundary intersects with the map boundary
		if(!border.intersects(map)){
				xPos = xVPos; yPos = yVPos;
				border.x  = xPos - size; border.y = yPos - size;
				writeToOutputLine();
				return;
			}


		//Check collisions with other objects
		for(int i = 0; i < gameModel.actors.length; i++){
			if(gameModel.actors[i] != null){
				if(this != gameModel.actors[i] ){
					if(border.intersects(gameModel.actors[i].getBorder())){
						if(gameModel.actors[i].getType().equals("powerUp")){
							scores+=50;
							powerUp temp = (powerUp)gameModel.actors[i];
							int function = temp.function;

							// Play pickup sound
							SoundPlayer.playSound("src/main/resources/pickup.wav");

							if(function == 0){  //Normal stars, increased speed
								upgrade();
							}else if(function == 1){  //steel wall protection base
								base tempe = (base)gameModel.actors[4];
								tempe.steelWallTime = 600;
							}else if(function == 2){   // Kill all enemies
								for(int j = 0; j < gameModel.actors.length; j++)
									if(gameModel.actors[j] != null)
										if(gameModel.actors[j].getType().equals("enemy")){
											enemy tempe = (enemy)gameModel.actors[j];
											gameModel.addActor(new bomb(tempe.xPos, tempe.yPos, "big", gameModel));
											gameModel.removeActor(gameModel.actors[j]);
										}
									level.NoOfEnemy = 0;
									level.deathCount = 20 - level.enemyLeft;
							}else if(function == 3){   //super shield
								InvulnerableTime = 300 + (int)Math.random()*400;
							}else if(function == 4){  //Freeze all enemies
								enemy.freezedTime = 300 + (int)Math.random()*400;
								enemy.freezedMoment = ServerModel.gameFlow;
							}else if(function == 5){ //super star
								if(status < 3)
									numberOfBullet++;
								status =4;
								health = 2;
								if(type.equals("1P"))
									for(int j = 0; j < 4; j ++)
										textures[j] = gameModel.textures[66+j];
								else
									for(int j = 0; j < 4; j ++)
										textures[j] = gameModel.textures[84+j];
							}else if(function == 6){  // increase life
								life++;
							}

							gameModel.removeActor(gameModel.actors[i]);

						}
						//Static objects such as walls, rivers
						else if(gameModel.actors[i].getType().equals("steelWall") || gameModel.actors[i].getType().equals("wall")){
							if(!gameModel.actors[i].walldestoried()){
								for(int j = 0;j < gameModel.actors[i].getDetailedBorder().length; j++){
									if( gameModel.actors[i].getDetailedBorder()[j] != null){
										if(gameModel.actors[i].getDetailedBorder()[j].intersects(border)){
												xPos = xVPos; yPos = yVPos;
												border.x  = xPos - size; border.y = yPos - size;
												writeToOutputLine();
												return;
										}
									}
								}
							}
						}
						else if(gameModel.actors[i].getType().equals("river") || gameModel.actors[i].getType().equals("base")){
							xPos = xVPos; yPos = yVPos;
							border.x  = xPos - size; border.y = yPos - size;
							writeToOutputLine();
							return;
						}
						//Moving objects, such as enemy tanks
						else if(gameModel.actors[i].getType().equals("enemy") || gameModel.actors[i].getType().equals("Player") ){
							if(!borderTemp.intersects(gameModel.actors[i].getBorder()) || gameModel.actors[i].getType().equals("enemy")){
								xPos = xPosTemp;
								yPos = yPosTemp;
								border.x  = xPos - size; border.y = yPos - size;
								writeToOutputLine();
								return;
							}
						}
					}
				}
			}
		}

		//Adjust real position based on virtual position for smoother movement
		int a = (xPos - 10)/25;
		int b = (xPos - 10)%25;
		if(b < 7)
			b = 0;
		if(b > 18)
			b = 25;
		if((b < 19 && b > 6) || xPos < 17 || xPos > 492)
			b = 13;
		xVPos = a*25 + b + 10;
		int c = (yPos - 10)/25;
		int d = (yPos - 10)%25;
		if(d < 7)
			d = 0;
		if(d > 18)
			d = 25;
		if((d < 19 && d > 6) || yPos < 17 || yPos > 492)
			d = 13;
		yVPos = c*25 + d + 10;

		writeToOutputLine();
	}

	public void writeToOutputLine(){
		//Write changes to the output line
		gameModel.outputLine+="n"+ xPos + "," + yPos + ",";
		int textureIndex = 0;
		if(type.equals("1P")){
			if(status == 1)
				textureIndex = 54 + direction;
			else if (status == 2)
				textureIndex = 58 + direction;
			else if(status == 3)
				textureIndex = 62 + direction;
			else
				textureIndex = 66 + direction;
		}else{
			if(status == 1)
				textureIndex = 72 + direction;
			else if (status == 2)
				textureIndex = 76 + direction;
			else if(status == 3)
				textureIndex = 80 + direction;
			else
				textureIndex = 84 + direction;
		}


		gameModel.outputLine+= "" + textureIndex + ";";

		if(InvulnerableTime > 0)
			gameModel.outputLine+="i"+ xPos + "," + yPos + ";";
	}

	public void draw(Graphics g){
		//Draw the player's tank
		g.drawImage(textures[direction], xPos - size, yPos - size, null);
		if(InvulnerableTime > 0){
			g.setColor(Color.red);
			g.drawRect(xPos - 12, yPos - 12, 25,25);
			g.drawRect(xPos - 11, yPos - 11, 23,23);
		}

		//Display player information (score, life, etc.)
		if(type.equals("1P")){
			g.setColor(Color.yellow);
			g.drawImage(standardImage,  520, 380, null );
			g.drawString("x", 555, 395);
			g.drawString(life + "", 565, 396);
			String SCORE = "000000000" + scores;
			g.drawString(type +" point:" + "", 515, 370);
			g.drawString(SCORE.substring(SCORE.length() - 7, SCORE.length()) + "", 566, 370);
		}
		if(type.equals("2P")){
			g.setColor(Color.green);
			g.drawImage(standardImage,  520, 460, null );
			g.drawString("x", 555, 475);
			g.drawString(life + "", 565, 476);
			String SCORE = "000000000" + scores;
			g.drawString(type +" point:" + "", 515, 450);
			g.drawString(SCORE.substring(SCORE.length() - 7, SCORE.length()) + "", 566, 450);
		}


	}

	public Rectangle getBorder(){
		return border;
	}

	public String getType(){
		return "Player";
	}

	public void hurt(){
		if(InvulnerableTime != 0)
			return;

		//If the tank only has level 1 health and is hit, then the player tank loses one life. If the player tank has the last life and is hit, the game is over.
		//Only when eating super stars, the player will have level 2 health status
		if(health == 1){
			gameModel.addActor(new bomb(xPos, yPos, "big",  gameModel));
			life--;
			if(life == 0){
				xPos = 100000; yPos = 100000;           //this will make the player never come back to the main screen, thus looks like "dead"
				border = new Rectangle(xPos - size, yPos - size, 25, 25);
				xVPos = xPos; yVPos = yPos;
				return;
			}else{
				direction = UP;
				status = 1;
				health = 1;
				numberOfBullet = 1;
				InvulnerableTime = 150;
				if(type.equals( "1P")){
					xPos  = 198;
					yPos = 498;
					border = new Rectangle(xPos - size, yPos - size, 25, 25);
					xVPos = xPos; yVPos = yPos;
					for(int i = 0; i < 4; i ++)
						textures[i] = gameModel.textures[54+i];
				}else{
					xPos = 323;
					yPos = 498;
					border = new Rectangle(xPos - size, yPos - size, 25, 25);
					xVPos = xPos; yVPos = yPos;
					for(int i = 0; i < 4; i ++)
						textures[i] = gameModel.textures[72+i];
				}
			}
		}else{
			health--;
			status = 3;
			if(type.equals( "1P")){
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[62+i];
			}else{
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[80+i];
			}
		}
	}

	public void upgrade(){
		//When the player tank eats normal stars, his bullets will be upgraded
		if(type.equals( "1P")){
			if(status == 1){
				status = 2;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[58+i];
			}else if(status == 2){
				status = 3;
				numberOfBullet ++;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[62+i];
			}else if(status == 3){
				status = 4;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[66+i];
			}
		}else{
			if(status == 1){
				status = 2;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[76+i];
			}else if(status == 2){
				status = 3;
				numberOfBullet ++;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[80+i];
			}else if(status == 3){
				status = 4;
				for(int i = 0; i < 4; i ++)
					textures[i] = gameModel.textures[84+i];
			}
		}
	}

	public void reset(){
		direction = UP;
		InvulnerableTime = 150;
		if(type.equals( "1P")){
			xPos  = 198;
			yPos = 498;
		}else{
			xPos  = 323;
			yPos = 498;
		}

		xVPos = xPos;
		yVPos = yPos;
		border = new Rectangle(xPos - size, yPos - size, 25, 25);
	}

	//Unused methods
	public Rectangle[] getDetailedBorder(){return null;}
	public boolean walldestoried(){return false;}
	public int getxPos() {
		return this.xPos;
	}

	public int getyPos() {
		return this.yPos;
	}

}
