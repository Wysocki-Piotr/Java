package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static String getApiKey() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Plik config.properties nie został znaleziony!");
            }
            properties.load(input);
        }
        return properties.getProperty("api.key");
    }
}
