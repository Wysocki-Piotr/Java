package Serwer;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PredictionService {
    private final static String apiKey;

    static {
        try {
            apiKey = Config.getApiKey();
        } catch (IOException e) {
            System.out.println("Problemy z kluczem API");
            throw new RuntimeException();
        }
    }
    public static WeatherForecast readByLatlon(double lat, double lon) throws IOException {
        String urlString = "api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" +apiKey +"&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Gson gson = new Gson();
        WeatherForecast outcome = gson.fromJson(String.valueOf(response), WeatherForecast.class);
        return outcome;
    }
    public static Boolean checkTempAlert(){
        return true;
    }
    public static Boolean checkWindAlert(){
        return true;
    }
    public static Boolean checkRainAlert(){
        return true;
    }

}
