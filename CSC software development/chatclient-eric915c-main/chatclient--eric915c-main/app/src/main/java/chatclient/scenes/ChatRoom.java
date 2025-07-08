package chatclient.scenes;

import chatclient.Constants;
import chatclient.UserSession;
import chatclient.responses.ListUsers;
import chatclient.responses.User;
import chatclient.listviewcells.MessageListCell;
import chatclient.responses.WebSocketResponse;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.google.gson.JsonObject;
import javafx.application.Platform;



import javax.websocket.*;
import java.net.URI;
import chatclient.responses.Message;
import chatclient.responses.ListMessages;
import org.glassfish.tyrus.client.ClientManager;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javafx.geometry.Insets;


public class ChatRoom implements Screen, MessageHandler.Whole<String> {
    private String roomName;
    private int roomId;
    private int ownerId;

    private final Stage stage;
    private UserSession userSession;
    private Session session;
    private ListView<String> users;
    private ListView<MessageListCell> messages;
    private TextField messageField;
    private Button sendButton;
    // Initialize the HttpClient
    private final HttpClient client = HttpClient.newHttpClient();


    public ChatRoom(Stage primaryStage) {
        this.stage = primaryStage;
        messages = new ListView<>();
        users = new ListView<>();
        messageField = new TextField();
        this.userSession = UserSession.getInstance();

        // Set the cell factory for the messages ListView to display MessageListCell items correctly
        messages.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MessageListCell item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label formattedLabel = new Label(item.getUsername() + " [" + item.getTimestamp() + "]: " + item.getMessage());
                    formattedLabel.setWrapText(true);
                    formattedLabel.setMaxWidth(300); // Adjust as needed
                    formattedLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    setGraphic(formattedLabel);
                }
            }
        });

    }



    /**
     * Builds the Scene for viewing a chat room.
     * @return
     */
    public Pane build() {
        VBox vbox = new VBox(10);
        Label roomLabel = new Label("Chat Room: " + roomName);
        Label roomIdLabel = new Label("Room ID: " + roomId);
        Label ownerIdLabel = new Label("Owner ID: " + ownerId);

        messages.setPrefHeight(300);
        users.setPrefHeight(100);
        messageField.setPromptText("Type message here:");

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(messageField.getText()));

        Button leaveButton = new Button("Leave Room");
        leaveButton.setOnAction(e -> {
            leaveRoom();
            Servers serversScreen = new Servers(stage);
            stage.getScene().setRoot(serversScreen.build());
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            UserSession.getInstance().logout();
            Login loginScreen = new Login(stage);
            stage.getScene().setRoot(loginScreen.build());
        });

        // Add components to layout
        vbox.getChildren().addAll(roomLabel, roomIdLabel, ownerIdLabel, users, messages, messageField, sendButton, leaveButton, logoutButton);
        return vbox;
    }




    /**
     * Transition to the chatroom UI where users can send/receive messages.
     */
    /**
     * Transition to the chatroom UI where users can send/receive messages.
     */
    private void ToChatRoomUI(String roomName, int roomId, int ownerId) {
        VBox vbox = new VBox(15); // Set a bit more spacing for readability
        vbox.setPadding(new Insets(20)); // Add padding around the edges
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #99a1df, #99a1df);"); // Dark background for contrast

        // Label for displaying Room Name, Room ID, and Owner ID at the top
        Label roomLabel = new Label("Chat Room: " + roomName);
        Label roomIdLabel = new Label("Room ID: " + roomId);
        Label ownerIdLabel = new Label("Owner ID: " + ownerId);

        // Style for each label to make the text larger and more readable
        roomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
        roomIdLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        ownerIdLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        // Configure the users ListView to display room users
        users.setPrefHeight(100);
        users.setStyle("-fx-background-color: #555555; -fx-text-fill: rgba(255,255,255,0.49); -fx-border-color: #444444;");

        // Configure the messages ListView to display chat messages
        messages.setPrefHeight(300);
        messages.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #333333;");

        // TextField for typing messages, with prompt and styling
        messageField.setPromptText("Type message here:");
        messageField.setStyle("-fx-background-color: #eeeeee; -fx-text-fill: black; -fx-prompt-text-fill: gray; -fx-border-color: #cccccc; -fx-background-radius: 5;");

        // Send Button
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(messageField.getText()));
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        sendButton.setMinWidth(80);

        // Leave Room button
        Button leaveButton = new Button("Leave Room");
        leaveButton.setOnAction(e -> {
            leaveRoom();
            System.out.println("You have left the room.");
            Servers serversScreen = new Servers(stage);
            stage.getScene().setRoot(serversScreen.build());
        });
        leaveButton.setStyle("-fx-background-color: #f89564; -fx-text-fill: white; -fx-background-radius: 5;");
        leaveButton.setMinWidth(100);

        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            UserSession.getInstance().logout();
            System.out.println("You have logged out.");
            Login loginScreen = new Login(stage);
            stage.getScene().setRoot(loginScreen.build());
        });
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;");
        logoutButton.setMinWidth(100);

        // Add all components, including the new labels, to the layout
        vbox.getChildren().addAll(roomLabel, roomIdLabel, ownerIdLabel, users, messages, messageField, sendButton, leaveButton, logoutButton);

        // Set this new layout as the root of the current scene
        stage.getScene().setRoot(vbox);
    }





    /**
     * Initializes a connection of a WebSocket to the chat server.
     * @param roomId which room connecting to
     * @param userId who's connecting to the room
     * @param roomName the name of the room.
     * @return whether or not the connection was made.
     */
    public boolean initWebsocket(final int roomId, final int userId, final String roomName,final int ownerId) {
        try {
            final String token = UserSession.getInstance().getToken();
            if (token == null || token.isEmpty()) {
                return false;
            }

            URI websocketUri = new URI("wss://" + Constants.DOMAIN + "/chat?room_id=" + roomId
                    + "&user_id=" + userId + "&token=" + token);

            ClientManager client = ClientManager.createClient();

            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    System.out.println("WebSocket connection opened. Session ID: " + session.getId());
                    setSession(session);
                    // Set the onMessage method as the handler for incoming messages
                    session.addMessageHandler(ChatRoom.this);


                    // Load users and message history upon joining the room
                    loadUsersPane();
                    loadMessages();
                    Platform.runLater(() -> ToChatRoomUI(roomName, roomId, ownerId));
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println(closeReason.getReasonPhrase());
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    System.err.println(thr.getMessage());
                    thr.printStackTrace();
                }
            }, websocketUri);
            return true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Set a reference to the WebSocket Session
     * @param s reference to WebSocket Session.
     */
    public void setSession(Session s) {
        this.session = s;
        if (s != null) {
            System.out.println(s.getId());
        } else {
            System.out.println("Session cleared.");
        }
    }


    /**
     * handles an incoming message from the Chat Server.
     * Gson is needed here to parse the Incoming Message
     * to a WebSocketResponse Type.
     * Depending on the value of event, this function should
     * handling a regular chat message, a join event,
     * and a leave event.
     * @param msg
     */
    public void handleIncomingMessage(final String msg) {
        try {
            JsonObject jsonMessage = new Gson().fromJson(msg, JsonObject.class);

            // Extract event type, username, and timestamp
            String eventType = jsonMessage.has("event") ? jsonMessage.get("event").getAsString() : "unknown";
            String username = jsonMessage.has("username") ? jsonMessage.get("username").getAsString() : "Unknown";
            String timestamp = jsonMessage.has("timestamp") ? jsonMessage.get("timestamp").getAsString() : "";
            String messageText = jsonMessage.has("message") ? jsonMessage.get("message").getAsString() : "";

            Platform.runLater(() -> {
                if ("message".equals(eventType)) {
                    messages.getItems().add(new MessageListCell(username, messageText, timestamp));
                } else if ("join".equals(eventType)) {
                    users.getItems().add(username + " (" + timestamp + ") joined the room.");
                } else if ("leave".equals(eventType)) {
                    users.getItems().add(username + " (" + timestamp + ") left the room.");
                }
            });
        } catch (Exception e) {
            System.err.println("Error processing incoming message: " + e.getMessage());
            e.printStackTrace();
        }
    }






    /**
     * Send a message to the Chat Server.
     * The message must be in the correct format
     * when being sent to the server.
     * @param message to be send
     * @param e ActionEvent object.
     */
    public void sendMessage(String message, ActionEvent e) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Cannot send empty message.");
            return;
        }

        if (session != null && session.isOpen()) {
            JsonObject messageJson = new JsonObject();
            messageJson.addProperty("event", "message");
            messageJson.addProperty("message", message);
            messageJson.addProperty("username", UserSession.getInstance().getUsername());

            session.getAsyncRemote().sendText(messageJson.toString());
            System.out.println("Message sent: " + message);
        } else {
            System.out.println("Not connected to the WebSocket.");
        }
    }


    /**
     * send message to server using the current WebSocket
     * session.
     * @param message to be sent.
     */
    public void sendMessage(final String message) {
        if (session == null || !session.isOpen()) {
            System.out.println("WebSocket session is not open.");
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Cannot send an empty message.");
            return;
        }

        WebSocketResponse wsm = new WebSocketResponse();
        wsm.setEvent("message");
        wsm.setMessage(message);
        wsm.setRoom_id(UserSession.getInstance().getCurrentRoomId());
        wsm.setUser_id(UserSession.getInstance().getUserId());
        wsm.setUsername(UserSession.getInstance().getUsername());

        // Convert to JSON
        Gson gson = new Gson();
        String messageJson = gson.toJson(wsm);


        session.getAsyncRemote().sendText(messageJson);

        messageField.clear();
    }




    /**
     * fetch current users in a active room.
     * Users should be added to the  users ListView
     */
    public void loadUsersPane() {
        try {
            final String token = UserSession.getInstance().getToken();
            final int roomId = UserSession.getInstance().getCurrentRoomId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://" + Constants.DOMAIN + "/api/rooms/" + roomId + "/users"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ListUsers listUsers = new Gson().fromJson(response.body(), ListUsers.class);

                Platform.runLater(() -> {
                    users.getItems().clear();
                    for (User user : listUsers.getUsers()) {
                        users.getItems().add(user.getUsername() + " (" + user.getUser_id() + ")");
                    }
                });
            } else {
                System.err.println(response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * fetch current messages in a active room.
     * Messages should be added to the messages ListView
     */
    public void loadMessages() {
        try {
            String token = UserSession.getInstance().getToken();
            int roomId = UserSession.getInstance().getCurrentRoomId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://" + Constants.DOMAIN + "/api/rooms/" + roomId + "/messages"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ListMessages listMessages = new Gson().fromJson(response.body(), ListMessages.class);
                Platform.runLater(() -> {
                    messages.getItems().clear();
                    for (Message msg : listMessages.getMessages()) {
                        messages.getItems().add(new MessageListCell(msg.getUsername(), msg.getText(), msg.getCreated_at()));
                    }
                });
            } else {
                System.err.println(response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void leaveRoom() {
        if (session != null && session.isOpen()) {
            try {
                session.close();  // Close the WebSocket connection
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        users.getItems().clear();    // Clear the list of users in the room
        messages.getItems().clear(); // Clear the chat messages
        UserSession.getInstance().setCurrentRoomId(-1); // Reset room ID in UserSession
    }


    @Override
    public void onMessage(String message) {
        System.out.println(message);
        handleIncomingMessage(message);
    }
}