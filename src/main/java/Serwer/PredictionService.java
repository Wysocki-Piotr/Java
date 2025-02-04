package Serwer;

import Exceptions.ApiKeyError;
import Exceptions.FileWithCountriesError;
import Exceptions.PageNotFoundException;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PredictionService {
    private final static String apiKey;
    private static final long oneDayInSeconds = 24 * 60 * 60;

    static {
        try {
            apiKey = Config.getApiKey();
        } catch (IOException e) {
            try {
                throw new ApiKeyError("Problemy z kluczem API!");
            } catch (ApiKeyError ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static WeatherForecast.Forecast checkForecast(double lat, double lon, long currentTime) {
        WeatherForecast weather = null;
        weather = readByLatlon(lat, lon);
        long tomorrowStartTime = currentTime + oneDayInSeconds;
        for (WeatherForecast.Forecast forecast : weather.list) {
            if (forecast.dt >= tomorrowStartTime && forecast.dt < tomorrowStartTime + oneDayInSeconds) {
                return forecast;
            }
        }
        return null;
    }
    public static WeatherForecast readByLatlon(double lat, double lon) {
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" +apiKey +"&units=metric";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            try {
                throw new PageNotFoundException("Problemy z łączeniem!");
            } catch (PageNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        // int responseCode = conn.getResponseCode();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder response = new StringBuilder();
        String inputLine;
        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                try {
                    throw new FileWithCountriesError("Problemy z wczytywaniem z pliku!");
                } catch (FileWithCountriesError ex) {
                    throw new RuntimeException(ex);
                }
            }
            response.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            try {
                throw new FileWithCountriesError("Problem z zamykaniem pliku!");
            } catch (FileWithCountriesError ex) {
                throw new RuntimeException(ex);
            }
        }
        Gson gson = new Gson();
        WeatherForecast outcome = gson.fromJson(String.valueOf(response), WeatherForecast.class);
        return outcome;
    }
    public static Boolean checkTempAlert(double lat, double lon, long currentTime) {
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if(forecast.main.temp < 0) return true;
        return false;
    }
    public static Boolean checkWindAlert(double lat, double lon, long currentTime){
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if (forecast.wind.speed > 20) return true;
        return false;
    }
    public static Boolean checkRainAlert(double lat, double lon, long currentTime){
        WeatherForecast.Forecast forecast = checkForecast(lat, lon, currentTime);
        if (forecast.rain != null && forecast.rain._3h > 20) return true;
        return false;
    }
    public static void save(double[] coord) throws IOException{

        WeatherForecast toSave = readByLatlon(coord[0], coord[1]);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(50, 750);

        contentStream.showText("Weather Forecast for " + toSave.city.name + ", " + toSave.city.country);
        contentStream.newLineAtOffset(-35, -20);

        Map<String, StringBuilder> dailyForecasts = groupForecastsByDay(toSave.list);

        for (Map.Entry<String, StringBuilder> entry : dailyForecasts.entrySet()) {
            String date = entry.getKey();
            String forecastDetails = entry.getValue().toString();
            forecastDetails = forecastDetails.replace("\r", "");

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText(date);
            contentStream.newLineAtOffset(0, -15);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            for (String line : forecastDetails.split("\n")) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -15);
            }
            contentStream.newLineAtOffset(0, -10);
        }

        contentStream.endText();
        contentStream.close();

        document.save("weather_forecast.pdf");
        document.close();
    }

    private static Map<String, StringBuilder> groupForecastsByDay(List<WeatherForecast.Forecast> forecasts) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");

        Map<String, StringBuilder> dailyForecasts = new TreeMap<>();
        String firstDay = null;
        boolean hasForecastBefore22 = false;

        for (WeatherForecast.Forecast forecast : forecasts) {

            Date date = new Date(forecast.dt * 1000);
            String day = dateFormat.format(date);
            int hour = Integer.parseInt(hourFormat.format(date));

            if (firstDay == null) {
                firstDay = day;
            }
            if (day.equals(firstDay) && hour < 22) {
                hasForecastBefore22 = true;
            }
            String details = String.format(
                    "  Time: %tR | Temp: %.1f°C | Humidity: %d%% | Weather: %s | Wind: %.1f m/s | Pressure: %d hPa%n",
                    date, forecast.main.temp, forecast.main.humidity, forecast.weather.get(0).description,
                    forecast.wind.speed, forecast.main.pressure);

            dailyForecasts.computeIfAbsent(day, k -> new StringBuilder());
            dailyForecasts.get(day).append(details);
        }
        if (firstDay != null && hasForecastBefore22) {
            dailyForecasts.remove(firstDay);
        }

        return dailyForecasts;
    }

}
