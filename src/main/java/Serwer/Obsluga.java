package Serwer;
import DB.JsonDatabase;
import DB.UserScheme;

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
    // ma zwracać listę miejsc dla danego usera

    public static List<String> exist(String text, String nick) throws IOException {

        String location = text;
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        JsonDatabase db = new JsonDatabase();
        List<UserScheme> users = db.readUsers();
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(nick)).findAny().orElse(null);
        users.remove(user);
        LinkedList<String> fav = user.getFavPlaces();
        if (responseCode == 200){
            if (fav.size() < 3) {
                fav.add(text);
                user.setFavPlaces(fav);
                users.add(user);
                db.writeUsers(users);
            }
            else
                System.out.println("Nie można dodać elementu!");
        }
        return fav;
    }

}
