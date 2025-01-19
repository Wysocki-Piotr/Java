package Components;

import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;

public class MapViewComponents {

    private final Components components;

    private BorderPane borderPane;
    private StackPane stackPane;
    public MapViewComponents(Components components) {
        this.components = components;
        initializeMapView(components.primaryStage);
        setComebackBatton();
    }


    public void initializeMapView(Stage primaryStage) {
        MapView mapView = new MapView();
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
    private void setComebackBatton(){
        Button button = UiComponents.createButton("Back", 630, 440, 0, 70, 35, 20);
        stackPane.getChildren().add(button);
        button.setOnAction(event -> {

            System.out.println("Button clicked, starting animation...");
            components.primaryStage.setScene(components.sceneMain);
            components.animateCamera(components.camera, 900, 400, true);

        });
    }

}
