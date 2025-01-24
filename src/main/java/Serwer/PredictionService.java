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
    private static final long oneDayInSeconds = 24 * 60 * 60;

    static {
        try {
            apiKey = Config.getApiKey();
        } catch (IOException e) {
            System.out.println("Problemy z kluczem API");
            throw new RuntimeException();
        }
    }
    public static WeatherForecast.Forecast checkForecast(double lat, double lon, long currentTime) {
        WeatherForecast weather = null;
        try {
            weather = readByLatlon(lat, lon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long tomorrowStartTime = currentTime + oneDayInSeconds;
        for (WeatherForecast.Forecast forecast : weather.list) {
            if (forecast.dt >= tomorrowStartTime && forecast.dt < tomorrowStartTime + oneDayInSeconds) {
                return forecast;
            }
        }
        return null; // niemozliwe dla tego api
    }
    public static WeatherForecast readByLatlon(double lat, double lon) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" +apiKey +"&units=metric";
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
    public static Boolean checkTempAlert(double lat, double lon, long currentTime) throws IOException {
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if(forecast.main.temp < 0) return true;
        return false;
    }
    public static Boolean checkWindAlert(double lat, double lon, long currentTime){
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if (forecast.wind.speed > 0) return true;
        return false;
    }
    public static Boolean checkRainAlert(double lat, double lon, long currentTime){
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if (forecast.rain != null && forecast.rain._3h > 20) return true;
        return false;
    }

}
