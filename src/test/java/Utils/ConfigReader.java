package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties PROPERTIES = new Properties();
    private static final String FILE_NAME = "config.properties";

    static {
        try (InputStream fis = ConfigReader.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (fis == null) {
                throw new RuntimeException("Configuration file not found: " + FILE_NAME);
            }
            PROPERTIES.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + FILE_NAME, e);
        }
    }

    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Missing or empty property: " + key);
        }
        return value.trim();
    }

    public static String getUrl() {
        return getProperty("url");
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static String getValidUsername() {
        return getProperty("validUsername");
    }

    public static String getValidPassword() {
        return getProperty("validPassword");
    }

    public static String getInvalidUsername() {
        return getProperty("invalidUsername");
    }

    public static String getInvalidPassword() {
        return getProperty("invalidPassword");
    }
}
