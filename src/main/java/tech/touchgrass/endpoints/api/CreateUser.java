package tech.touchgrass.endpoints.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.touchgrass.db.classes.user.User;
import tech.touchgrass.db.classes.user.UserDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateUser implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreateUser.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try (InputStream is = exchange.getRequestBody()) {
                String requestBody = readInputStream(is);
                JSONObject json = new JSONObject(requestBody);

                String email = json.getString("email");
                String username = json.getString("username");
                String password = json.getString("password");

                if (UserDatabase.userExists(email, username)) {
                    sendResponse(exchange, 409, "User already exists");
                } else {
                    User user = new User(username, email, password);
                    boolean userCreated = UserDatabase.createUser(user);
                    if (userCreated) {
                        sendResponse(exchange, 201, "User created successfully");
                    } else {
                        sendResponse(exchange, 500, "Failed to create user");
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing request", e);
                sendResponse(exchange, 500, "Internal server error");
            }
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }

    private String readInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}