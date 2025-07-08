package chatclient.scenes;

import chatclient.Constants;
import chatclient.UserSession;
import chatclient.responses.CreateRoom;
import chatclient.listviewcells.ServerListCell;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Servers implements Screen {
    private final Stage mainStage;
    private ListView<ServerListCell> servers; // Correctly define as ListView<ServerListCell>
    private final HttpClient client = HttpClient.newHttpClient();

    public Servers(Stage s) {
        this.mainStage = s;
        this.servers = new ListView<>(); // Initialize ListView<ServerListCell>
    }

    /**
     * Builds the Server listing Screen
     * @return root to the Server Listing Screen
     */
    public Pane build() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #99a1df, #99a1df);");


        Label titleLabel = new Label("List of Rooms");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKSLATEBLUE);
        titleLabel.setPadding(new Insets(0, 0, 15, 0));


        servers.setPrefHeight(300);
        servers.setPlaceholder(new Label("No rooms available"));
        servers.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #185ba8; -fx-background-radius: 5; -fx-border-radius: 5;");


        Button refreshButton = new Button("Load Rooms");
        refreshButton.setOnAction(this::refreshServerList);
        refreshButton.setStyle("-fx-background-color: #800080; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        refreshButton.setMinWidth(120);

        Button connectButton = new Button("Join Room");
        connectButton.setOnAction(this::connectToRoom);
        connectButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        connectButton.setMinWidth(120);

        Button createButton = new Button("Create New Room");
        createButton.setOnAction(this::showCreateRoomForm);
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        createButton.setMinWidth(120);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            logout();
            System.out.println("User logged out.");
        });
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        logoutButton.setMinWidth(120);

        // Add components to layout
        root.getChildren().addAll(titleLabel, servers, refreshButton, connectButton, createButton, logoutButton);
        return root;
    }


    /**
     * Fetch active servers from the Chat Server
     * @param ev
     */
    private void refreshServerList(ActionEvent ev) {
        try {
            String token = UserSession.getInstance().getToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://" + Constants.DOMAIN + "/api/rooms"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = com.google.gson.JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray rooms = jsonResponse.getAsJsonArray("rooms");

                servers.getItems().clear(); // Clear previous room list

                for (int i = 0; i < rooms.size(); i++) {
                    JsonObject room = rooms.get(i).getAsJsonObject();
                    String roomName = room.get("room_name").getAsString();
                    int roomId = room.get("id").getAsInt();
                    int ownerId = room.get("owner_id").getAsInt();

                    // Create a ServerListCell object with room details
                    ServerListCell cell = new ServerListCell(roomName, ownerId, roomId, servers);
                    servers.getItems().add(cell); // Add the cell to the ListView
                }
            } else {
                System.err.println(response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to the selected room in the servers listing.
     * Opens a new WebSocket and fetches messages and users for the room.
     * @param ev
     */
    private void connectToRoom(ActionEvent ev) {
        ServerListCell selectedRoomCell = servers.getSelectionModel().getSelectedItem();

        if (selectedRoomCell == null) {
            System.out.println("No room selected.");
            return;
        }

        String roomName = selectedRoomCell.getRoomName();
        int roomId = selectedRoomCell.getRoomId();
        int ownerId = selectedRoomCell.getOwnerId();

        try {
            ChatRoom chatRoom = new ChatRoom(mainStage);
            UserSession.getInstance().setCurrentRoomId(roomId);

            mainStage.getScene().setRoot(chatRoom.build());

            // Initialize WebSocket connection with message handling in ChatRoom
            boolean websocketConnected = chatRoom.initWebsocket(roomId, UserSession.getInstance().getUserId(), roomName, ownerId);

            if (!websocketConnected) {
                System.err.println("Failed to connect to WebSocket for room: " + roomName);
            } else {
                System.out.println("WebSocket connection established successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCreateRoomForm(ActionEvent ev) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Room");
        dialog.setHeaderText("Enter a name for the new room:");

        dialog.showAndWait().ifPresent(this::handleCreateRoom);
    }

    private void handleCreateRoom(String roomName) {
        try {
            String token = UserSession.getInstance().getToken();

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("room_name", roomName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://" + Constants.DOMAIN + "/api/rooms"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                CreateRoom createRoomResponse = new Gson().fromJson(response.body(), CreateRoom.class);

                if ("success".equals(createRoomResponse.getStatus())) {
                    refreshServerList(new ActionEvent());
                } else {
                    System.err.println("Failed to create room: " + createRoomResponse.getMessage());
                }
            } else {
                System.err.println("Failed to create room. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logout() {
        UserSession.getInstance().clearSession();
        Login loginScreen = new Login(mainStage);
        mainStage.getScene().setRoot(loginScreen.build());
    }
}
