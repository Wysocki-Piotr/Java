package Serwer;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import java.util.List;

public class WeatherForecast {

    public int cod;
    public String message;
    public int cnt;
    public List<Forecast> list;
    public City city;

    public static class Forecast {
        public long dt;
        public Main main;
        public List<Weather> weather;
        public Clouds clouds;
        public Wind wind;
        public int visibility;
        public double pop;
        public Rain rain;
        public Snow snow;
        public Sys sys;
        public String dtTxt;
    }

    public static class Main {
        public double temp;
        public double feelsLike;
        public double tempMin;
        public double tempMax;
        public int pressure;
        public int seaLevel;
        public int grndLevel;
        public int humidity;
        public double tempKf;
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public static class Clouds {
        public int all;
    }

    public static class Wind {
        public double speed;
        public int deg;
        public double gust;
    }

    public static class Rain {
        public double _3h;
    }

    public static class Snow {
        public double _3h;
    }

    public static class Sys {
        public String pod;
    }

    public static class City {
        public int id;
        public String name;
        public Coord coord;
        public String country;
        public int population;
        public int timezone;
        public long sunrise;
        public long sunset;

        public static class Coord {
            public double lat;
            public double lon;
        }
    }
}
