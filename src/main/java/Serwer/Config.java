package Serwer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static String getApiKey() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("config.properties"));
        return properties.getProperty("api.key");
    }
}
