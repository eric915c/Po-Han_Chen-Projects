import javax.swing.*;
import java.awt.*;
import Client.ClientView;
import Server.ServerView;

public class Main {
    public static void main(String[] args) {
        // Create the main window (JFrame)
        JFrame frame = new JFrame("Tank Battle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create ServerView and ClientView
        ServerView serverView = new ServerView();
        ClientView clientView = new ClientView();

        // Add both views to the TabbedPane
        tabbedPane.addTab("Server", serverView);
        tabbedPane.addTab("Client", clientView);

        // Add the TabbedPane to the JFrame
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Set the window size and display it
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
}
