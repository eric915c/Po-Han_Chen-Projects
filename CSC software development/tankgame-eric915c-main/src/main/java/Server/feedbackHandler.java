package Server; // This class decodes instruction strings from the client program
// and converts the strings into actual instructions readable by the server program

public class feedbackHandler {
	public static void handleInstruction(ServerModel gameModel, String instruction) {
		if (instruction.length() == 0)
			return;

		int i = 0;
		while (i < instruction.length()) {
			String perInstruction = "";

			// Instruction ends with a semicolon ";"
			while (!instruction.substring(i, i + 1).equals(";")) {
				perInstruction += instruction.substring(i, i + 1);
				i++;
			}

			// Instruction "m" indicates client movement information
			if (perInstruction.substring(0, 1).equals("m")) {
				gameModel.P2.moveUp = false;
				gameModel.P2.moveDown = false;
				gameModel.P2.moveLeft = false;
				gameModel.P2.moveRight = false;
				gameModel.P2.fire = false;

				String temp = perInstruction.substring(1, 2);
				if (temp.equals("1"))
					gameModel.P2.moveUp = true;
				temp = perInstruction.substring(2, 3);
				if (temp.equals("1"))
					gameModel.P2.moveDown = true;
				temp = perInstruction.substring(3, 4);
				if (temp.equals("1"))
					gameModel.P2.moveLeft = true;
				temp = perInstruction.substring(4, 5);
				if (temp.equals("1"))
					gameModel.P2.moveRight = true;
				temp = perInstruction.substring(5, 6);
				if (temp.equals("1"))
					gameModel.P2.fire = true;
			}

			// Instruction "e" indicates server player information
			if (perInstruction.substring(0, 1).equals("e")) {
				gameModel.addMessage("Client player says: " + perInstruction.substring(1, perInstruction.length()));
			}

			// Instruction "j" indicates the client player wants to play again
			if (perInstruction.substring(0, 1).equals("j")) {
				if (gameModel.gameOver)
					gameModel.clientVoteYes = true;
			}

			// Instruction "x" indicates the server player pauses the game
			if (perInstruction.substring(0, 1).equals("x")) {
				if (gameModel.gamePaused) {
					gameModel.addMessage("Client player has resumed the game.");
					gameModel.gamePaused = false;
				} else if (!gameModel.gamePaused) {
					gameModel.addMessage("Client player has paused the game.");
					gameModel.gamePaused = true;
				}
			}
			i++;
		}
	}
}
