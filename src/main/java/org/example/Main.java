package org.example;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            String apiKey = Config.getApiKey();
            String weatherData = WeatherService.getWeather("London", apiKey);
            System.out.println(weatherData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}