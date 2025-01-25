package Components;

import Alert.Localization;
import Serwer.WeatherResponse;
import Serwer.WeatherService;
import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MapViewComponents {

    private final Components components;
    private MapView mapView;
    private StackPane stackPane;
    private Button showButton;
    private TextField xField;
    private TextField yField;
    private Button moveBackButton;
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



    public MapViewComponents(Components components) {
        this.components = components;
        initializeMapView(components.primaryStage);
        setPanelLayout();
        setButtonsFunctionalities();
    }


    public void initializeMapView(Stage primaryStage) {
        mapView = new MapView();
        mapView.setMapType(MapType.OSM);
        double[] curCoordinates = Localization.getCurrentLocalizationByApi();
        mapView.setCenter(new Coordinate(curCoordinates[0], curCoordinates[1]));
        mapView.setZoom(11);
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
        Label enterLabel = UiComponents.createLabel("Podaj koordynaty", Color.WHITE, 30, 470, -420, 0);
        stackPane.getChildren().add(enterLabel);

        moveBackButton = UiComponents.createButton("Wróć", 610, 440, 0, 70, 35, 15);
        stackPane.getChildren().add(moveBackButton);

        Label xLabel = UiComponents.createLabel("X:", Color.WHITE, 22, 320, -370, 0);
        Label yLabel = UiComponents.createLabel("Y:", Color.WHITE, 22, 320, -340, 0);


        stackPane.getChildren().add(xLabel);
        stackPane.getChildren().add(yLabel);

        xField = new TextField();
        xField.setPromptText("Wpisz współrzędne X");
        xField.setMaxWidth(200);
        xField.setTranslateY(-370);
        xField.setTranslateX(470);
        stackPane.getChildren().add(xField);

        yField = new TextField();
        yField.setPromptText("Wpisz współrzędne Y");
        yField.setMaxWidth(200);
        yField.setTranslateY(-340);
        yField.setTranslateX(470);
        stackPane.getChildren().add(yField);

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

        currentLocationWeatherButton = UiComponents.createButton("Pokaż pogode w swojej lokalizacji", 410, 440, 0, 300, 35, 15);
        stackPane.getChildren().add(currentLocationWeatherButton);
        stackPane.getChildren().addAll(tempLabel, countryLabel, windLabel, pressureLabel, humidityLabel,
                tempLabelResp,countryLabelResp,windLabelResp,pressureLabelResp,humidityLabelResp);

        showButton = new Button("Przenieść do miejsca na mapie");
        showButton.setTranslateY(-300);
        showButton.setTranslateX(470);

        stackPane.getChildren().add(showButton);


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
            if (CoordinateValidator.userInputValidatorX(xField.getText()) && CoordinateValidator.userInputValidatorY(yField.getText())) {
                setCoordinates(translateUserCoordinatesToProperTypeX(xField.getText()),
                        translateUserCoordinatesToProperTypeY(yField.getText()));

                try {
                    WeatherResponse response = getWeatherDataFromAPI(translateUserCoordinatesToProperTypeX(xField.getText()),
                            translateUserCoordinatesToProperTypeY(yField.getText()));


                    setWeatherDataOnLabels(response);



                } catch (IOException e) {
                    throw new RuntimeException(e);
                }




            } else {
                //TODO: throw exception
            }
        });
        moveBackButton.setOnAction(event -> {

            components.primaryStage.setScene(components.sceneMain);
            components.animateCamera(components.camera, 900, 400, true);

        });
        currentLocationWeatherButton.setOnAction(event ->{
            double[] curCoordinates = Localization.getCurrentLocalizationByApi();
            setCoordinates(curCoordinates[0], curCoordinates[1]);
            try {
                WeatherResponse response = getWeatherDataFromAPI(curCoordinates[0],curCoordinates[1]);
                setWeatherDataOnLabels(response);
                xField.setText(String.valueOf(curCoordinates[0]));
                yField.setText(String.valueOf(curCoordinates[1]));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
    }


    private static class CoordinateValidator {

        public static boolean userInputValidatorX(String input) {
            if (input.isEmpty() || input == null) {
                return false;
            }

            char last = input.charAt(input.length() - 1);
            if (last != 'E' && last != 'W') {
                return false;
            }

            Double value = Double.parseDouble(input.substring(0, input.length() - 1));
            if (value <0 || value>180) {
                return false;
            }
            return true;
        }
        public static boolean userInputValidatorY(String input) {
            if (input.isEmpty() || input == null) {
                return false;
            }
            char last = input.charAt(input.length() - 1);
            if (last != 'N' && last != 'S') {
                return false;
            }
            Double value = Double.parseDouble(input.substring(0, input.length() - 1));
            if (value <0 || value>90) {
                return false;
            }
            return true;
        }

    }

    private WeatherResponse getWeatherDataFromAPI(double x,double y) throws IOException {
        HttpURLConnection conn =WeatherService.createByLatLon(x, y);
        WeatherResponse weatherResponse = WeatherService.apiAnswer(conn);
        return weatherResponse;
    }

    private void setCoordinates(double x, double y) {
        mapView.setCenter(new Coordinate(x, y));
        mapView.setZoom(11);
    }

    private double translateUserCoordinatesToProperTypeX(String userCoordinateX) {
        char lastChar = userCoordinateX.charAt(userCoordinateX.length()-1);
        if (lastChar == 'E'){
            return Double.parseDouble(userCoordinateX.substring(0, userCoordinateX.length()-1));
        }else{
            return -Double.parseDouble(userCoordinateX.substring(0, userCoordinateX.length()-1));
        }
    }

    private double translateUserCoordinatesToProperTypeY(String userCoordinateY) {
        char lastChar = userCoordinateY.charAt(userCoordinateY.length()-1);
        if (lastChar == 'N'){
            return Double.parseDouble(userCoordinateY.substring(0, userCoordinateY.length()-1));
        }else{
            return -Double.parseDouble(userCoordinateY.substring(0, userCoordinateY.length()-1));
        }
    }

    private void displayWeatherAPIResponse(){
        //TODO Display weather data from API
    }





}
