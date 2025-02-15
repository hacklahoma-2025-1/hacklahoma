package tech.touchgrass;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.touchgrass.config.ConfigManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        logger.info("Starting TouchGrass");

        logger.info("Loading Config");
        ConfigManager.loadConfig();
        logger.info("Config Loaded");

        logger.info("Building HttpServer Server");
        HttpServer server = HttpServer.create(new InetSocketAddress(ConfigManager.getIntProperty("server.port")), 0);

        logger.info("Creating Static Contexts");
        server.createContext("/", new tech.touchgrass.endpoints.Index());

        logger.info("Creating API Contexts");
        server.createContext("/api/createUser", new tech.touchgrass.endpoints.api.CreateUser());

        logger.info("Starting HttpServer");
        server.start();
        logger.info("HttpServer Started");

    }

}