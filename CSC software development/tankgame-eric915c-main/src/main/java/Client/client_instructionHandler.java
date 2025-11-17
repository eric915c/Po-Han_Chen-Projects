package Client;// This class decodes instruction strings from the server program and converts them into actual instructions
// Readable by the client program
public class client_instructionHandler {
	public static void handleInstruction(ClientModel gameModel, String instruction){
		if(instruction.length() == 0)
			return;

		int i = 0;
		while(i < instruction.length()){
			String perInstruction = "";

			// Instructions are separated by ";" in the instruction string
			while(!instruction.substring(i, i+1).equals(";")){
				perInstruction+=instruction.substring(i, i+1);
				i++;
			}

			// Instructions starting with "L" indicate level loading, followed by "L" level index
			if(perInstruction.substring(0,1).equals("L")){
				client_level.loadLevel(gameModel, Integer.parseInt(perInstruction.substring(1,2)));
				return;
			}

			// Instructions starting with "w" indicate changes to wall objects
			if(perInstruction.substring(0,1).equals("w")){
				int xPos = 0; int yPos = 0; boolean[] shape = new boolean[16];
				String temp = "";
				int j = 1;
				// Get x position
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get y position
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				//Wall boundary details
				for(int k = 0; k < 16; k++){
					if(perInstruction. substring(j, j+1).equals("1"))
						shape[k] = true;
					else
						shape[k] = false;
					j++;
				}

				//Execute instruction
				for(int k = 0; k < gameModel.drawingList.length; k++){
					if(gameModel.drawingList[k] != null){
						if(gameModel.drawingList[k].getxPos() == xPos && gameModel.drawingList[k].getyPos() == yPos){
							client_wall tempWall = new client_wall(xPos, yPos, 4, gameModel);
							tempWall.shape = shape;
							gameModel.drawingList[k] = tempWall;
						}
					}
				}
			}

			//Instructions starting with "s" indicate changes to a steel wall object
			if(perInstruction.substring(0,1).equals("s")){
				int xPos = 0; int yPos = 0; boolean[] shape = new boolean[4];
				String temp = "";
				int j = 1;
				//Get x position
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get y position
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				//Steel wall boundary details
				for(int k = 0; k < 4; k++){
					if(perInstruction. substring(j, j+1).equals("1"))
						shape[k] = true;
					else
						shape[k] = false;
					j++;
				}

				//Execute instruction
				for(int k = 0; k < gameModel.drawingList.length; k++){
					if(gameModel.drawingList[k] != null){
						if(gameModel.drawingList[k].getxPos() == xPos && gameModel.drawingList[k].getyPos() == yPos){
							client_Steelwall tempWall = new client_Steelwall(xPos, yPos, 4, gameModel);
							tempWall.shape = shape;
							gameModel.drawingList[k] = tempWall;
						}
					}
				}
			}

			//Instructions starting with "b" indicate that the base has been destroyed
			if(perInstruction.substring(0,1).equals("b")){
				gameModel.drawingList[4] = new client_normalObject(260, 498,  gameModel, "base", 1);
			}

			//Instructions starting with "n" indicate normal objects, such as tanks or start symbols
			if(perInstruction.substring(0,1).equals("n")){
				int xPos = 0; int yPos = 0; int textureIndex = -1;
				String temp = "";
				int j = 1;
				//Get x position of the object
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get y position of the object
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				//Get the texture index of the object
				temp = "";
				while(j < perInstruction.length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				textureIndex = Integer.parseInt(temp);

				//Execute instruction
				gameModel.addActor(new client_normalObject(xPos, yPos, gameModel, "normal", textureIndex));
			}


			//Instructions starting with "t" indicate bullets
			if(perInstruction.substring(0,1).equals("t")){
				int xPos = 0; int yPos = 0; int direction = -1;
				String temp = "";
				int j = 1;
				//Get x position of the bullet
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get y position of the bullet
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				//Direction of the bullet
				temp = "";
				while(j < perInstruction.length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				direction = Integer.parseInt(temp);

				//Execute instruction
				gameModel.addActor(new client_bullet(xPos, yPos, gameModel, direction));
			}

			//// Instruction starting with "o" indicates a bomb
			if(perInstruction.substring(0,1).equals("o")){
				int xPos = 0; int yPos = 0; int size = -1;
				String temp = "";
				int j = 1;
				//Get the x position of the bomb
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get the y position of the bomb
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				//Size of the bomb
				temp = "";
				while(j < perInstruction.length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				if(temp.equals("small"))
					size = 1;
				else
					size = 0;
				//Execute instruction
				gameModel.addActor(new client_bomb(xPos, yPos, size, gameModel));
			}

			//Instruction starting with "i" indicates a tank shield
			if(perInstruction.substring(0,1).equals("i")){
				int xPos = 0; int yPos = 0;
				String temp = "";
				int j = 1;
				//Get the x position of the shield
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				xPos =  Integer.parseInt(temp);

				//Get the y position of the shield
				temp = "";
				while(j < perInstruction. length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				yPos = Integer.parseInt(temp);

				//Execute instruction
				gameModel.addActor(new client_shield(xPos, yPos, gameModel));
			}

			//Instruction starting with "p" indicates level and player information
			if(perInstruction.substring(0,1).equals("p")){
				String temp = "";
				int j = 1;
				//Get the number of enemies left
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.EnemyLeft =  Integer.parseInt(temp);

				//Get the level index
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.LevelIndex =  Integer.parseInt(temp);

				//Player 1 life
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.P1Life =  Integer.parseInt(temp);

				//Player 1 score
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.P1Score =  Integer.parseInt(temp);

				//Player 2 life
				temp = "";
				while(!perInstruction. substring(j, j+1).equals(",")){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.P2Life =  Integer.parseInt(temp);

				//Player 2 score
				temp = "";
				while(j < perInstruction.length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				j++;
				gameModel.view.mainPanel.P2Score =  Integer.parseInt(temp);
			}

			//Instruction starting with "g" indicates the victory count
			if(perInstruction.substring(0,1).equals("g")){
				String temp = "";
				int j = 1;
				//Get the number of enemies left
				while(j < perInstruction.length()){
					temp+=perInstruction. substring(j, j+1);
					j++;
				}
				client_level.winningCount = Integer.parseInt(temp);
			}

			//Instruction starting with "m" indicates host player information
			if(perInstruction.substring(0,1).equals("m")){
				gameModel.addMessage("The host player says:" + perInstruction.substring(1,perInstruction.length()));
			}

			//Instruction starting with "a" indicates the game is over
			if(perInstruction.substring(0,1).equals("a")){
				if(!gameModel.gameOver){
					gameModel.addMessage("GAME OVER ! 　Would you like to play again? (y / n) ( y / n ) ?");
					gameModel.gameOver = true;
				}
			}
			//Instruction starting with "j" indicates the host player wants to play again
			if(perInstruction.substring(0,1).equals("j")){
				if(gameModel.gameOver)
					gameModel.serverVoteYes = true;
			}

			//Instruction starting with "x" indicates the host player paused the game
			if(perInstruction.substring(0,1).equals("x")){
				int temp = Integer.parseInt(perInstruction.substring(1,2));
				if(temp == 0){
					if(gameModel.gamePaused){
						gameModel.addMessage("The host player resumed the game.");
						gameModel.gamePaused = false;
					}
				}else{
					if(!gameModel.gamePaused){
						gameModel.addMessage("The host player resumed the game.");
						gameModel.gamePaused = true;
					}
				}
			}
			i++;
		}
	}
}