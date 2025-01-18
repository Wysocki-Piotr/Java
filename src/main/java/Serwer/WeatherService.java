package Serwer;
import DB.JsonDatabase;
import DB.UserScheme;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;

public class WeatherService {
    private final static String apiKey;

    static {
        try {
            apiKey = Config.getApiKey();
        } catch (IOException e) {
            System.out.println("Problemy z kluczem API");
            throw new RuntimeException();
        }
    }

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

    public static WeatherResponse apiAnswer(HttpURLConnection conn) throws IOException {
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Gson gson = new Gson();
        WeatherResponse outcome = gson.fromJson(String.valueOf(response), WeatherResponse.class);
        return outcome;
    }
    public static Map<String, String> filterWeather(String comb, String country, double min, double max) throws IOException {
        Map <String, String> mapa = returnCitiesMap();
        List<String> listaKluczy = mapa.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(country))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Map <String, String> filtered = new HashMap<>();
        for (String city: listaKluczy){
            HttpURLConnection conn = createByName(city);
            WeatherResponse outcome = apiAnswer(conn); //napisac wyjatek jesli dany kraj nie dziala (blad 400)
            if (outcome.weather[0].main.equals(comb) && min <= outcome.main.temp && outcome.main.temp <= max) {
                String url = "https://openweathermap.org/img/wn/" + outcome.weather[0].icon + "@2x.png";
                filtered.put(city, url);
            }
        }
        Map<String, String> limitedMap = filtered.entrySet()
                .stream()
                .limit(3)
                .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue()
        ));
        System.out.println(filtered);
        return filtered;
    }

    public static Map<String, String> returnCitiesMap() throws IOException {
        File file = new File("src/main/java/Serwer/cities_list.xlsx");
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Map<String, String> cityCountryMap = new HashMap<>();
        for (Row row : sheet) {
            Cell cityCell = row.getCell(0);
            Cell countryCell = row.getCell(3);
            if (cityCell != null && countryCell != null) {
                String city = cityCell.getStringCellValue();
                String country = countryCell.getStringCellValue();
                cityCountryMap.put(city, country);
            }
        }
        fis.close();
        return cityCountryMap;
    }
}
