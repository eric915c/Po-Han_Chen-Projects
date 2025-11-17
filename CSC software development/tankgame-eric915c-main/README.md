# csc413-tankgame


| Student Information |                |
|:-------------------:|----------------|
|  Student Name       | Po-Han Chen    |
|  Student Email      | pchen@sfsu.edu |


## Purpose of jar Folder 
The jar folder will store the built jar of your term project.

`NO SOURCE CODE SHOULD BE IN THIS FOLDER. DOING SO WILL CAUSE POINTS TO BE DEDUCTED

`THIS FOLDER CAN NOT BE DELETED OR MOVED`

# Required Information when Submitting Tank Game

## Version of Java Used: openjdk - 22

## IDE used: IntelliJ IDEA

## Steps to Import project into IDE: 
1. clone from GitHub
2. The project uses Maven, select "Import Maven" and IntelliJ will automatically configure dependencies.
3. Go to `File > Project Structure > Project`.
4. Under `Project SDK`, select openjdk - 22
5. For Maven projects: Wait for IntelliJ to download dependencies (this happens automatically if the internet is connected).  For manual setup, go to `Maven > Reload Project` (Maven).
6. Go to `File > Project Structure > Modules`. Mark resources file as `Resources`.

## Steps to Build Your Project:
1. Go to `File > Project Structure`. Confirm the `Project SDK` correctly.
2. The project uses Maven, select "Import Maven" and IntelliJ will automatically configure dependencies.
3. IntelliJ will compile your source code and generate `.class` files in the `out` or `build` directory.
4. Right-click the `Main` file and select `Run Main`.
5. If there are missing dependencies or libraries:
   For Maven: Right-click `pom.xml` > `Maven > Reload Project`.
6. Go to `File > Project Structure > Artifacts` to check or create build artifacts (e.g., `.jar` files).
 
## Steps to run your Project:
1. through jar to execute the game
2. click "Create Host" button to build the server
3. click the "Connect" button to connect to the server
4. Game start!

## Controls to play your Game:

|               | Player 1 | Player 2 |
|---------------|--------|----------|
|  Forward      | ↑      | w        |
|  Backward     | ↓      | s        |
|  Rotate left  | ←      | a        |
|  Rotate Right | →      | d        |
|  Shoot        | l      | space    |
use tab to switch game mode and send message

<!-- You may add more controls if you need to. -->
