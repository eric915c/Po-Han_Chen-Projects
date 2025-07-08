package chatclient;

import chatclient.responses.User;
import chatclient.responses.ListUsers;

import java.util.ArrayList;
import java.util.List;


public class UserSession {
    private static UserSession session=null;
    private String username;
    private int userId;
    private String token;
    private int currentRoomId;
    private String email;
    private String apiKey;
    private List<User> currentRoomUsers;

    /**
     * Private Constructor to not allow outside instantiation.
     */
    private UserSession(){
        this.userId = -1;  // Initialize with invalid userId
        this.token = null;
        this.username = null;
        this.currentRoomId = -1; // Initialize with invalid room ID
        this.email = null;
        this.apiKey = null;
        this.currentRoomUsers = new ArrayList<>();
    }

    /**
     * use to get an instance of UserSession.
     * If there is no current user Session, a new one is made.
     * It is important that after you call this function call initSession
     * @return instance of UserSession
     */
    public static UserSession getInstance(){
        if (session == null) {
            session = new UserSession();
        }
        return session;
    }

    /**
     * Initial a UserSession from login response.
     * @param username of logged in user.
     * @param userId of logged is user
     * @param token used to access API and connect to WebSocket.
     */
    public  void initSession(String username, int userId, String token){
        this.username = username;
        this.userId = userId;
        this.token = token;
        this.email = null;
        this.apiKey = null;
        System.out.println("Session initialized with username: " + username + ", userId: " + userId + ", token: " + token);

    }

    public void logout() {
        // Clear user data
        this.token = null;
        this.userId = -1;
        this.username = null;
        this.currentRoomId = -1;

        System.out.println("User has logged out successfully.");
    }

    /**
     * Sets the current room ID for the user session.
     * @param roomId the room ID the user is currently in.
     */
    public void setCurrentRoomId(int roomId) {
        this.currentRoomId = roomId;
        System.out.println("Current room ID set to: " + roomId);
    }

    /**
     * Gets the current room ID.
     * @return the current room ID.
     */
    public int getCurrentRoomId() {
        return this.currentRoomId;
    }


    /**
     * Sets the list of users in the current room, using data from ListUsers.java.
     * @param users List of users from ListUsers response.
     */
    public void setCurrentRoomUsers(ListUsers users) {
        if (users != null && users.getUsers() != null) {
            this.currentRoomUsers.clear();
            this.currentRoomUsers.addAll(users.getUsers());
            System.out.println("Users in current room updated successfully.");
        } else {
            System.err.println("ListUsers instance or its users list is null.");
        }
    }

    /**
     * Gets the list of users in the current room.
     * @return List of users in the current room.
     */
    public List<User> getCurrentRoomUsers() {
        return this.currentRoomUsers;
    }

    /**
     * Fetches usernames of all users in the current room.
     * @return List of usernames in the current room.
     */
    public List<String> getCurrentRoomUsernames() {
        List<String> usernames = new ArrayList<>();
        for (User user : currentRoomUsers) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    /**
     * Fetches user IDs of all users in the current room.
     * @return List of user IDs in the current room.
     */
    public List<Integer> getCurrentRoomUserIds() {
        List<Integer> userIds = new ArrayList<>();
        for (User user : currentRoomUsers) {
            userIds.add(user.getUser_id());
        }
        return userIds;
    }


    /**
     * Gets the username of the user session.
     * @return username of logged in user.
     */
    public String getUsername() {
        if (username == null) {
            throw new IllegalStateException("User session not initialized: username is missing");
        }
        return username;
    }

    /**
     * Gets the user ID of the logged in user.
     * @return userId of logged in user.
     */
    public int getUserId() {
        if (this.userId < 0) {
            throw new IllegalStateException("User session is not correctly initialized: userId is missing");
        }
        return userId;
    }

    /**
     * Gets the API token used for authenticated API requests.
     * @return the token.
     */
    public String getToken() {
        if (this.token == null) {
            throw new IllegalStateException("User session not initialized: token is missing");
        }
        return token;
    }

    /**
     * Gets the email of the logged-in user.
     * @return email of the user.
     */
    public String getEmail() {
        if (this.email == null) {
            throw new IllegalStateException("User session not initialized: email is missing");
        }
        return email;
    }

    /**
     * Gets the API key of the logged-in user.
     * @return API key of the user.
     */
    public String getApiKey() {
        if (this.apiKey == null) {
            throw new IllegalStateException("User session not initialized: API key is missing");
        }
        return apiKey;
    }

    /**
     * Allows updating the API token without reinitializing the entire session.
     * This might be useful if the token expires and needs refreshing.
     * @param newToken the new API token.
     */
    public void updateToken(String newToken) {
        if (newToken == null || newToken.isEmpty()) {
            throw new IllegalArgumentException("Cannot update to a null or empty token.");
        }
        this.token = newToken;
    }

    /**
     * used to clean current UserSession. Be sure to
     * set all data-fields to values that denote the session
     * is not active.
     */
    public void clearSession() {
        this.userId = -1;
        this.token = null;
        this.username = null;
        this.currentRoomId = -1;
        this.email = null;
        this.apiKey = null;
        this.currentRoomUsers.clear();  // Clear the users list
        session = null;
    }
}
