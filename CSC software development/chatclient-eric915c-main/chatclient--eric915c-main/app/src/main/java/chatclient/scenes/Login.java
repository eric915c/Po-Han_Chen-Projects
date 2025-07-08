package chatclient.scenes;

import javafx.scene.layout.*;
import chatclient.LoginService;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import chatclient.UserSession;
import chatclient.responses.LoginResponse;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class used to represent a Login Screen
 * You may modify the class as you see fit.
 */
public class Login implements Screen {
    private Stage mainStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;  // Label to display login status

    public Login(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /**
     * Builds the Login Scene and returns the root of that scene.
     * @return A build Scene
     */
    public Pane build() {
        // Root pane with a gradient background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #693c91, #91aee4);");


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 10;");


        Label titleLabel = new Label("Welcome to Chat App");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.DARKSLATEBLUE);
        titleLabel.setPadding(new Insets(0, 0, 20, 0));


        usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5; -fx-padding: 8px; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-text-fill: black;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5; -fx-padding: 8px; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-text-fill: black;");


        Button loginButton = new Button("Login");
        loginButton.setOnAction(this::handleLogin);
        loginButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        loginButton.setMinWidth(100);


        Button resetButton = new Button("Reset");
        resetButton.setOnAction(this::handleReset);
        resetButton.setStyle("-fx-background-color: #e94e77; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        resetButton.setMinWidth(100);


        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        messageLabel.setTextFill(Color.DARKRED);


        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        usernameLabel.setTextFill(Color.DARKSLATEBLUE);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        passwordLabel.setTextFill(Color.DARKSLATEBLUE);


        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(loginButton, 0, 3);
        gridPane.add(resetButton, 1, 3);
        gridPane.add(messageLabel, 0, 4, 2, 1);

        root.setCenter(gridPane);

        return root;
    }


    /**
     * used to handle login button click.
     * This function should send username and password to the server
     * and then depending on the response should either display
     * invalid login credentials or transition to the Servers Screen.
     * @param ev Event from button click
     */
    private void handleLogin(ActionEvent ev) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Call the LoginService to perform login
            LoginResponse loginResponse = LoginService.login(username, password);

            if (loginResponse != null && "success".equalsIgnoreCase(loginResponse.getStatus())) {
                messageLabel.setText("Login successful!");

                // Initialize the UserSession with username, userId, and token from the response
                UserSession.getInstance().initSession(
                        loginResponse.getUsername(),
                        loginResponse.getUser_id(),
                        loginResponse.getToken()
                );

                // Transition to the Servers screen
                transitionToServersScreen();
            } else {
                messageLabel.setText(loginResponse != null ? loginResponse.getMessage() : "Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An error occurred. Please try again.");
        }
    }


    /**
     * used to handle reset button click.
     * @param ev Event from button click
     */
    private void handleReset(ActionEvent ev) {
        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }

    /**
     * Transitions to the Servers screen after successful login.
     */
    private void transitionToServersScreen() {
        Servers serversScreen = new Servers(mainStage);
        mainStage.getScene().setRoot(serversScreen.build());
    }
}
