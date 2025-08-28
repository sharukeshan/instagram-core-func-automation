package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/resources/config.properties";
    
    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("Failed to load config file: " + e.getMessage());
            throw new RuntimeException("Configuration file not found", e);
        }
    }
    
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config file");
        }
        return value.trim();
    }
    
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return (value != null) ? value.trim() : defaultValue;
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
    
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    // Specific getters for commonly used properties
    public static String getBrowser() {
        return getProperty("browser");
    }
    
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    public static String getTestUsername() {
        return getProperty("test.username");
    }
    
    public static String getTestPassword() {
        return getProperty("test.password");
    }
    
    public static String getScreenshotPath() {
        return getProperty("screenshot.path");
    }
    
    public static String getReportPath() {
        return getProperty("report.path");
    }
}