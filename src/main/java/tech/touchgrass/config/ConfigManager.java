package tech.touchgrass.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static Properties properties = new Properties();

    public static void loadConfig() {
        try {
            InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/config.properties"));
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Error loading config file", e);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public static String getProperty(String key) {
        if(properties.isEmpty()) {
            loadConfig();
        }
        return properties.getProperty(key);
    }

    public static int getIntProperty(String key) {
        if(properties.isEmpty()) {
            loadConfig();
        }
        return Integer.parseInt(properties.getProperty(key));
    }

    public static boolean getBooleanProperty(String key) {
        if(properties.isEmpty()) {
            loadConfig();
        }
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static double getDoubleProperty(String key) {
        if(properties.isEmpty()) {
            loadConfig();
        }
        return Double.parseDouble(properties.getProperty(key));
    }
}
