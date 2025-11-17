package Client;

import java.awt.*;
// Create an interface
public interface client_Actor {
	public void draw(Graphics g); // Method to draw the actor on the screen
	public int getxPos(); // Method to get the x-coordinate of the actor
	public int getyPos(); // Method to get the y-coordinate of the actor

	String getType(); // Add this method to return the type of the actor

}
