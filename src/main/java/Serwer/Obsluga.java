package Serwer;
import DB.JsonDatabase;
import DB.UserScheme;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Obsluga {
    private final static String apiKey = "e95fb7ea6cda081ad055c8bfdcdb3e5d";

    public static List<String> exist(String location, String nick) throws IOException {
        HttpURLConnection conn = createByName(location);
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        JsonDatabase db = new JsonDatabase();
        List<UserScheme> users = db.readUsers();
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(nick)).findAny().orElse(null);
        users.remove(user);
        LinkedList<String> fav = user.getFavPlaces();
        if (responseCode == 200){
            if (fav.size() < 3) {
                fav.add(location);
                user.setFavPlaces(fav);
                users.add(user);
                db.writeUsers(users);
            }
            else
                System.out.println("Nie można dodać elementu!");
        }
        return fav;
    }

    public static HttpURLConnection createByName(String location) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }
    public static HttpURLConnection createByLatLon(double lat, double lon) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    public static WeatherResponse apiAnswerByLat(double lat, double lon) throws IOException {
        HttpURLConnection conn = createByLatLon(lat, lon);
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response);
        Gson gson = new Gson();
        WeatherResponse outcome = gson.fromJson(String.valueOf(response), WeatherResponse.class);
        return outcome;
    }
}
