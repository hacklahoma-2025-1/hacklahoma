package tech.touchgrass.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Index implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(Index.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logger.debug("Handling request for index.html");

        byte[] responseBytes;
        try {
            // Read the file as bytes and then convert to String with UTF-8 encoding
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/main/resources/static/index.html"));
            String response = new String(fileBytes, StandardCharsets.UTF_8);
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Error reading index.html", e);
            responseBytes = "Error: Unable to load index.html".getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        logger.debug("Response sent with {} bytes", responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
