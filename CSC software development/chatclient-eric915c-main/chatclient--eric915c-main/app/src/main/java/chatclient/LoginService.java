package chatclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import chatclient.responses.LoginResponse;
import com.google.gson.JsonObject;

public class LoginService {

    // The URL for the login API endpoint
    private static final String LOGIN_URL = "https://csc413.ajsouza.com/users/login/app";
    /**
     * Sends a login request to the server with the provided username and password.
     * If successful, returns the token from the server.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return An Optional containing the token if login is successful, empty otherwise.
     */
    public static LoginResponse login(String username, String password) {
        try {
            // Create a JSON object for the request body with username and password.
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", username);
            requestBody.addProperty("password", password);

            // Build the HTTP POST request with headers and body.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))  // Set a timeout for the request
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Send the HTTP request and handle the response.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the HTTP response status and body.
            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("HTTP Response Body: " + response.body());

            // Check if the response status code is 200 (OK).
            if (response.statusCode() == 200) {
                // Parse the JSON response from the server.
                JsonObject jsonResponse = com.google.gson.JsonParser.parseString(response.body()).getAsJsonObject();

                // Extract fields from the JSON response.
                String status = jsonResponse.get("status").getAsString();
                String message = jsonResponse.get("message").getAsString();
                String token = jsonResponse.has("token") ? jsonResponse.get("token").getAsString() : null;
                int userId = jsonResponse.has("user_id") ? jsonResponse.get("user_id").getAsInt() : -1;
                String usernameResponse = jsonResponse.has("username") ? jsonResponse.get("username").getAsString() : username;

                // Return a new LoginResponse object with the parsed data.
                return new LoginResponse(status, message, userId, usernameResponse, token);
            } else {
                System.out.println("Unexpected HTTP response: " + response.statusCode());
                return new LoginResponse("fail", "Unexpected response from server", -1, username, "");
            }
        } catch (HttpTimeoutException e) {
            System.out.println("Request timed out. Please try again.");
            return new LoginResponse("fail", "Request timed out", -1, username, "");
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponse("fail", "An error occurred", -1, username, "");
        }
    }
}
