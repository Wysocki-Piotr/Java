package Serwer;

public class WeatherResponse {
        public Coord coord;
        public Weather[] weather;
        public Main main;
        public Wind wind;
        public Clouds clouds;
        public Sys sys;
        public String base;
        public int visibility;
        public int dt;
        public int timezone;
        public int id;
        public String name;
        public int cod;
    public class Coord {
        public double lon;
        public double lat;
    }

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public class Main {
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
        public int sea_level;
        public int grnd_level;
    }

    public class Wind {
        public double speed;
        public int deg;
        public double gust;
    }

    public class Clouds {
        public int all;
    }

    public class Sys {
        public int type;
        public int id;
        public String country;
        public int sunrise;
        public int sunset;
    }
}
