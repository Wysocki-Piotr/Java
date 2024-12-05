package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final OkHttpClient client = new OkHttpClient();

    public static String getWeather(String city, String apiKey) throws IOException {
        String url = BASE_URL + "?q=" + city + "&appid=" + apiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}
