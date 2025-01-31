package Serwer;
import DB.JsonDatabase;
import DB.UserScheme;
import Exceptions.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
            try {
                throw new ApiKeyError("Problemy z kluczem Api!");
            } catch (ApiKeyError ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static List<String> exist(String location, String nick) throws DBError, PageNotFoundException {
        HttpURLConnection conn = createByName(location);
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonDatabase db = null;
        try {
            db = new JsonDatabase();
        } catch (IOException e) {
            throw new DBError("Nie znaleziono bazy danych!");
        }
        List<UserScheme> users = null;
        try {
            users = db.readUsers();
        } catch (IOException e) {
            throw new DBError("Problemy z wczytywaniem użytkowników!");
        }
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(nick)).findAny().orElse(null);
        users.remove(user);
        LinkedList<String> fav = user.getFavPlaces();
        if (responseCode == 200){
            if (fav.size() < 3) {
                fav.add(location);
                user.setFavPlaces(fav);
                users.add(user);
                try {
                    db.writeUsers(users);
                } catch (IOException e) {
                    throw new DBError("Problemy z zapisywaniem użytkowników");
                }
            }
        }
        return fav;
    }

    public static HttpURLConnection createByName(String location) throws PageNotFoundException {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
                throw new PageNotFoundException("Nie znaleziono strony!");
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
    public static HttpURLConnection createByLatLon(double lat, double lon) {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            try {
                throw new PageNotFoundException("Nie znaleziono strony");
            } catch (PageNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static WeatherResponse apiAnswer(HttpURLConnection conn) throws PageNotFoundException, FileWithCountriesError {
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
                throw new FileWithCountriesError("Problemy z wczytywaniem do pliku!");
            }
            response.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        WeatherResponse outcome = gson.fromJson(String.valueOf(response), WeatherResponse.class);
        return outcome;
    }
    public static Map<String, String> filterWeather(String comb, String country, double min, double max) throws FileWithCountriesError, Credentials, PageNotFoundException {
        Map <String, String> mapa = returnCitiesMap();
        List<String> listaKluczy = mapa.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(country))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Map <String, String> filtered = new HashMap<>();
        for (String city: listaKluczy){
            HttpURLConnection conn = null;
            try {
                conn = createByName(city);
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                } else if (responseCode == 400) {
                    try {
                        throw new Credentials("Problemy z uzyskaniem dostępu do lokacji");
                    } catch (Credentials e) {
                        throw new RuntimeException(e);
                    }
                } else if (responseCode == 404) {
                    try {
                        throw new PageNotFoundException("Problemy ze stroną API");
                    } catch (PageNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
            WeatherResponse outcome = apiAnswer(conn);
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
        return filtered;
    }

    public static Map<String, String> returnCitiesMap() throws FileWithCountriesError {
        File file = new File("src/main/java/Serwer/cities_list.xlsx");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileWithCountriesError("Nie znaleziono pliku z krajami!");
        }
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(fis);
        } catch (IOException e) {
            throw new FileWithCountriesError("Problem z odczytywaniem pliku!");
        }
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
        try {
            fis.close();
        } catch (IOException e) {
            throw new FileWithCountriesError("Problem z zamykaniem pliku!");
        }
        return cityCountryMap;
    }
}
