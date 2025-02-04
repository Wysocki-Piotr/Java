package Serwer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Alert.AlertManagement;
import Components.Components;

public class Config {
    private boolean internetAvailable = false;
    private Components components;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
    protected static String getApiKey() throws FileNotFoundException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        return properties.getProperty("api.key");
    }

    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
}
    public Config(Components components) {
        this.components = components;
    }

    public void startChecking() {
        scheduler.scheduleAtFixedRate(this::checkInternet, 0, 2, TimeUnit.SECONDS);
    }
    public void startCheckingIfAccesible() {scheduler2.scheduleAtFixedRate(this::check, 0, 2, TimeUnit.SECONDS);}
    public void checkInternet() {
        boolean isAvailable = isInternetAvailable();
        internetAvailable = isAvailable;

        if (internetAvailable) {
            AlertManagement alert = new AlertManagement(components);
            alert.scheduleTemperatureCheck();
        } else {
            components.noInternetAvalaible(scheduler, this);
        }
    }
    public void check(){
        if (isInternetAvailable()){
            AlertManagement alert = new AlertManagement(components);
            alert.scheduleTemperatureCheck();
            components.internetAvalaible(scheduler2, this);
        }

    }
}
