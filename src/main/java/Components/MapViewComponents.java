package Components;

import Alert.Localization;
import Exceptions.FileWithCountriesError;
import Exceptions.PageNotFoundException;
import Serwer.WeatherResponse;
import Serwer.WeatherService;
import com.sothawo.mapjfx.*;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MapViewComponents {

    protected boolean isActive = false;

    private final Components components;
    private MapView mapView;
    private StackPane stackPane;
    private Button showButton;
    private TextField inputLongtitude;
    private TextField inputLatitude;
    protected Button moveBackButton;
    private WeatherService weatherService;
    private Button currentLocationWeatherButton;
    private Label tempLabel;
    private Label countryLabel;
    private Label windLabel;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label tempLabelResp;
    private Label countryLabelResp;
    private Label windLabelResp;
    private Label pressureLabelResp;
    private Label humidityLabelResp;
    private Label[] labels;
    private Button pointFromUserButton;
    private Button randomPointButton;
    private Label errorLabel;
    DecimalFormat df = new DecimalFormat("#.#####");

    private double [] curCoordinates;



    public MapViewComponents(Components components) {
        this.components = components;
        initializeMapView(components.primaryStage);
        prepareErrorLabel();
        setPanelLayout();
        setButtonsFunctionalities();
        addCrosshair();


    }


    public void initializeMapView(Stage primaryStage) {
        mapView = new MapView();
        mapView.setMapType(MapType.OSM);
        CompletableFuture.supplyAsync(() -> Localization.getCurrentLocalizationByApi())
                .thenAccept(resultCoordinates -> {
                    curCoordinates = resultCoordinates;
                })
                .exceptionally(ex -> {
                    return null;
                });

        mapView.setCenter(new Coordinate(0.0, 0.0));
        mapView.setZoom(0);
        mapView.initialize(Configuration.builder()
                .showZoomControls(true)
                .build());


        Rectangle overlay = new Rectangle(400, 940, Color.rgb(0, 0, 0, 0.5));
        overlay.setTranslateX(470);
        overlay.setTranslateY(-10);
        overlay.setArcWidth(50);
        overlay.setArcHeight(50);

        stackPane = new StackPane();
        stackPane.getChildren().addAll(mapView, overlay);

        Scene mapViewScene = new Scene(stackPane, Components.getWIDTH(), Components.getHEIGHT());
        primaryStage.setScene(mapViewScene);
        primaryStage.setX(primaryStage.getX());
        primaryStage.setY(primaryStage.getY());
        primaryStage.show();
    }

    private void setPanelLayout() {

        //---------------------Buttons---------------------
        moveBackButton = UiComponents.createButton("Wróć", 610, 440, 0, 70, 35, 15);
        stackPane.getChildren().add(moveBackButton);
        currentLocationWeatherButton = UiComponents.createButton("Pokaż pogode w swojej lokalizacji", 410, 440, 0, 300, 35, 15);

        showButton = new Button("Przenieść do miejsca na mapie");
        showButton.setTranslateY(-300);
        showButton.setTranslateX(470);

        pointFromUserButton = UiComponents.createButton("Pokaż pogodę w tym miejscu",470,300,0,300,35,15);
        pointFromUserButton.setTranslateY(300);
        pointFromUserButton.setTranslateX(470);

        randomPointButton = UiComponents.createButton("Szczęśliwy traf",470,350,0,350,35,15);


        //---------------------Labels---------------------

        Label enterLabel = UiComponents.createLabel("Podaj koordynaty", Color.WHITE, 30, 470, -420, 0);
        stackPane.getChildren().add(enterLabel);
        Label xLabel = UiComponents.createLabel("Long:", Color.WHITE, 22, 320, -370, 0);
        Label yLabel = UiComponents.createLabel("Lat:", Color.WHITE, 22, 320, -340, 0);


        stackPane.getChildren().add(xLabel);
        stackPane.getChildren().add(yLabel);
        //---------------TextFields---------------------
        inputLongtitude = new TextField();
        inputLongtitude.setPromptText("Wpisz współrzędne X");
        inputLongtitude.setMaxWidth(200);
        inputLongtitude.setTranslateY(-370);
        inputLongtitude.setTranslateX(470);
        stackPane.getChildren().add(inputLongtitude);

        inputLatitude = new TextField();
        inputLatitude.setPromptText("Wpisz współrzędne Y");
        inputLatitude.setMaxWidth(200);
        inputLatitude.setTranslateY(-340);
        inputLatitude.setTranslateX(470);
        stackPane.getChildren().add(inputLatitude);

        //---------------------Weather Data---------------------

        tempLabel = UiComponents.createLabel("Temperatura:", Color.WHITE, 15, 340, -270, 0);
        tempLabelResp = UiComponents.createLabel("", Color.WHITE, 15, 490, -270, 0);
        tempLabel.setVisible(false);
        tempLabelResp.setVisible(false);

        countryLabel = UiComponents.createLabel("Kraj:", Color.WHITE, 15, 313, -240, 0);
        countryLabelResp = UiComponents.createLabel("", Color.WHITE, 15, 490, -240, 0);
        countryLabel.setVisible(false);
        countryLabelResp.setVisible(false);

        windLabel = UiComponents.createLabel("Wiatr:", Color.WHITE, 15, 313, -210, 0);
        windLabelResp = UiComponents.createLabel("", Color.WHITE, 15, 490, -210, 0);
        windLabel.setVisible(false);
        windLabelResp.setVisible(false);

        pressureLabel = UiComponents.createLabel("Ciśnienie:", Color.WHITE, 15, 328, -180, 0);
        pressureLabelResp = UiComponents.createLabel("", Color.WHITE, 15, 490, -180, 0);
        pressureLabel.setVisible(false);
        pressureLabelResp.setVisible(false);

        humidityLabel = UiComponents.createLabel("Wilgotność:", Color.WHITE, 15, 333, -150, 0);
        humidityLabelResp = UiComponents.createLabel("", Color.WHITE, 15, 490, -150, 0);
        humidityLabel.setVisible(false);
        humidityLabelResp.setVisible(false);


        stackPane.getChildren().add(currentLocationWeatherButton);
        stackPane.getChildren().addAll(tempLabel, countryLabel, windLabel, pressureLabel, humidityLabel,
                tempLabelResp,countryLabelResp,windLabelResp,pressureLabelResp,humidityLabelResp,pointFromUserButton,
                randomPointButton);

        stackPane.getChildren().addAll(showButton,errorLabel);


    }

    private void setWeatherDataOnLabels(WeatherResponse response){
        tempLabelResp.setText(response.main.temp + "°C");
        countryLabelResp.setText(CountryMap.getCountryName(response.sys.country));
        windLabelResp.setText(response.wind.speed + " m/s");
        pressureLabelResp.setText(response.main.pressure + " hPa");
        humidityLabelResp.setText(response.main.humidity + "%");

        labels = new Label[]{tempLabel, countryLabel, windLabel, pressureLabel, humidityLabel,
                tempLabelResp, countryLabelResp, windLabelResp,
                pressureLabelResp, humidityLabelResp};

        for (Label label : labels) {
            label.setVisible(true);
        }
    }


    private void setButtonsFunctionalities(){

        showButton.setOnAction(event -> {

            boolean checkLatitude;
            boolean checkLongtitude;

            try{

                checkLatitude = CoordinateValidator.userInputValidatorLatitude(inputLatitude.getText());
                checkLongtitude = CoordinateValidator.userInputValidatorLongtitude(inputLongtitude.getText());

            }catch (Exception e){

                checkLongtitude=false;
                checkLatitude=false;

            }

            if (checkLatitude && checkLongtitude) {
                setCoordinates(inputLatitude.getText(),inputLongtitude.getText(),11);

                try {
                    WeatherResponse response = getWeatherDataFromAPI(inputLatitude.getText(),inputLongtitude.getText());
                    setWeatherDataOnLabels(response);

                } catch (IOException | PageNotFoundException | FileWithCountriesError e) {
                    throw new RuntimeException(e);
                }
            } else {
                showErrorLabel("Wprowadzono błędne dane");
            }
        });

        moveBackButton.setOnAction(event -> {

            components.primaryStage.setScene(components.sceneMain);
            components.animateCamera(components.camera, 900, 400, true);

        });

        currentLocationWeatherButton.setOnAction(event ->{
            if(curCoordinates == null){
                showErrorLabel("Lokalizacja aktualnie niedostępna");
                return;
            }
            setCoordinates(String.valueOf(curCoordinates[0]),String.valueOf(curCoordinates[1]),11);
            inputLongtitude.setText(String.valueOf(curCoordinates[1]*1000000d / 1000000d));
            inputLatitude.setText(String.valueOf(curCoordinates[0]*1000000d / 1000000d));
            try {
                WeatherResponse response = getWeatherDataFromAPI(String.valueOf(curCoordinates[0]),String.valueOf(curCoordinates[1]));
                setWeatherDataOnLabels(response);


            } catch (IOException | PageNotFoundException | FileWithCountriesError e) {
                throw new RuntimeException(e);
            }


        });

        pointFromUserButton.setOnAction(event ->{
            double[] curCoordinates = curCoordinates();
            setCoordinates(String.valueOf(curCoordinates[0]),String.valueOf(curCoordinates[1]),11);

            try {
                WeatherResponse response = getWeatherDataFromAPI(curCoordinates[0],curCoordinates[1]);
                setWeatherDataOnLabels(response);
                inputLatitude.setText(String.valueOf(curCoordinates[0]*1000000d / 1000000d));
                inputLongtitude.setText(String.valueOf(curCoordinates[1]*1000000d / 1000000d));
            } catch (IOException | PageNotFoundException | FileWithCountriesError e) {
                throw new RuntimeException(e);
            }
        });

        randomPointButton.setOnAction(event ->{
            WeatherResponse resp = randomPointWeather();

            setWeatherDataOnLabels(resp);

        });
    }

    private static class CoordinateValidator {

        public static boolean userInputValidatorLatitude(String input) {
            if (input.isEmpty() || input == null) {
                return false;
            }

            Double value;
            try{
                value = Double.parseDouble(input);
            }catch (Exception e){
                return false;
            }

            if (Math.abs(value) > 90) {
                return false;
            }
            return true;
        }
        public static boolean userInputValidatorLongtitude(String input) {
            if (input.isEmpty() || input == null) {
                return false;
            }

            Double value;
            try{
                value = Double.parseDouble(input);
            }catch (Exception e){
                return false;
            }



            if (Math.abs(value)>180) {
                return false;
            }
            return true;
        }

    }

    private WeatherResponse getWeatherDataFromAPI(String latitude,String longtitude) throws IOException, PageNotFoundException, FileWithCountriesError {
        double latitudeValue = Double.parseDouble(latitude);
        double longtitudeValue = Double.parseDouble(longtitude);
        HttpURLConnection conn =WeatherService.createByLatLon(latitudeValue, longtitudeValue);
        WeatherResponse weatherResponse = WeatherService.apiAnswer(conn);
        return weatherResponse;
    }

    private WeatherResponse getWeatherDataFromAPI(double latitude, double longtitude) throws IOException, PageNotFoundException, FileWithCountriesError {
        HttpURLConnection conn = WeatherService.createByLatLon(latitude, longtitude);
        WeatherResponse weatherResponse = WeatherService.apiAnswer(conn);
        return weatherResponse;
    }

    private void setCoordinates(String latitude, String longtitude,int zoom) {
        double latitudeValue = Double.parseDouble(latitude);
        double longtitudeValue = Double.parseDouble(longtitude);
        mapView.setCenter(new Coordinate(latitudeValue, longtitudeValue));
        mapView.setZoom(zoom);
    }

    private WeatherResponse randomPointWeather(){

        WeatherResponse resp;

        while(true){
            double randLat = Math.round(ThreadLocalRandom.current().nextDouble(-90, 90) * 1000000d) / 1000000d;
            double randLong = Math.round(ThreadLocalRandom.current().nextDouble(-180, 180) * 1000000d) / 1000000d;
            try {
                resp = getWeatherDataFromAPI(randLat,randLong);
                if(resp.sys.country == null){
                    continue;
                }else{
                    setCoordinates(String.valueOf(randLat),String.valueOf(randLong),7);
                    inputLatitude.setText(String.valueOf(randLat*1000000d / 1000000d));
                    inputLongtitude.setText(String.valueOf(randLong*1000000d / 1000000d));
                    return resp;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (PageNotFoundException e) {
                throw new RuntimeException(e);
            } catch (FileWithCountriesError e) {
                throw new RuntimeException(e);
            }


        }

    }

    private double[] curCoordinates(){
        Coordinate center = mapView.getCenter();
        return new double[]{center.getLatitude(), center.getLongitude()};
    }

    private void addCrosshair() {
        Line horizontalLine = new Line(-10, 0, 10, 0);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(2);

        Line verticalLine = new Line(0, -10, 0, 10);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(2);

        stackPane.getChildren().addAll(horizontalLine, verticalLine);
    }

    private void prepareErrorLabel() {
        errorLabel = new Label("Wprowadzono błędne dane");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        errorLabel.setTranslateY(-460); // Adjust the position as needed
        errorLabel.setVisible(false);
    }

    private void showErrorLabel(String text) {
        errorLabel.setVisible(true);
        errorLabel.setText(text);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), errorLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(event -> errorLabel.setVisible(false));
        fadeTransition.play();
    }





}
