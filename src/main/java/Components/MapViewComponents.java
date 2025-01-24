package Components;

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

public class MapViewComponents {

    private final Components components;
    private MapView mapView;
    private StackPane stackPane;
    private Button showButton;
    private TextField xField;
    private TextField yField;
    private Button moveBackButton;
    private WeatherService weatherService;

    public MapViewComponents(Components components) {
        this.components = components;
        initializeMapView(components.primaryStage);

        setPanelLayout();
        setButtonsFunctionalities();
    }


    public void initializeMapView(Stage primaryStage) {
        mapView = new MapView();
        mapView.setMapType(MapType.OSM);
        mapView.setCenter(new Coordinate(52.2312, 20.9897));
        mapView.setZoom(14);
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

        moveBackButton = UiComponents.createButton("Wróć", 630, 440, 0, 70, 35, 20);
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

        Label weatherLabel = UiComponents.createLabel("Temperatura:", Color.WHITE, 15, 340, -270, 0);
        stackPane.getChildren().add(weatherLabel);
        //weatherLabel.setVisible(false);

        Label timeLabel = UiComponents.createLabel("Czas:", Color.WHITE, 15, 313, -240, 0);
        stackPane.getChildren().add(timeLabel);
        //timeLabel.setVisible(false);

        Label windLabel = UiComponents.createLabel("Wiatr:", Color.WHITE, 15, 313, -210, 0);
        stackPane.getChildren().add(windLabel);
        //windLabel.setVisible(false);

        Label pressureLabel = UiComponents.createLabel("Ciśnienie:", Color.WHITE, 15, 328, -180, 0);
        stackPane.getChildren().add(pressureLabel);
        //pressureLabel.setVisible(false);

        Label humidityLabel = UiComponents.createLabel("Wilgotność:", Color.WHITE, 15, 333, -150, 0);
        stackPane.getChildren().add(humidityLabel);
        //humidityLabel.setVisible(false);



        showButton = new Button("Przenieść do miejsca na mapie");
        showButton.setTranslateY(-300);
        showButton.setTranslateX(470);

        stackPane.getChildren().add(showButton);


    }

    private void setButtonsFunctionalities(){

        showButton.setOnAction(event -> {
            if (CoordinateValidator.userInputValidatorX(xField.getText()) && CoordinateValidator.userInputValidatorY(yField.getText())) {
                setCoordinates(translateUserCoordinatesToProperTypeX(xField.getText()),
                        translateUserCoordinatesToProperTypeY(yField.getText()));
            } else {
                //TODO: throw exception
            }
        });
        moveBackButton.setOnAction(event -> {

            components.primaryStage.setScene(components.sceneMain);
            components.animateCamera(components.camera, 900, 400, true);

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

    private void getWeatherDataFromAPI(double x,double y) throws IOException {
        HttpURLConnection conn =WeatherService.createByLatLon(x, y);
        WeatherResponse weatherResponse = WeatherService.apiAnswer(conn);
        System.out.println(weatherResponse.coord.lat);
    }

    private void setCoordinates(double x, double y) {
        mapView.setCenter(new Coordinate(x, y));
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
