package org.example.demo;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 1000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private final Sphere sphere = new Sphere(150);

    private final Sphere sun = new Sphere(70);


    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/hello-view.fxml"));
        Parent root = loader.load();

        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(1000000);
        camera.translateZProperty().set(-1600);
        camera.translateXProperty().set(-220);
        camera.translateYProperty().set(-20);


        Group world = new Group();
        Group universe = new Group();
        universe.getChildren().addAll(world,root);
        world.getChildren().addAll(prepareEarth());

        Scene scene = new Scene(universe, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        scene.getStylesheets().add(getClass().getResource("/cssstyling/styles.css").toExternalForm());
        initMouseControl(world, scene, primaryStage);

        scene.setOnKeyPressed(event ->{
            switch (event.getCode()){
                case T:

                    camera.translateZProperty().set(-1600);
                    camera.translateXProperty().set(-220);
                    camera.translateYProperty().set(-20);
                    break;
                default:
                    break;
            }
        });


        primaryStage.setTitle("Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
        prepareAnimation();
    }

    private void prepareAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                sphere.rotateProperty().set(sphere.getRotate() + 0.2);
            }
        };
        timer.start();
    }


    private Node prepareEarth() {
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResource("/Blue_Marble_2002.png").toExternalForm()));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResource("/earth-l.jpg").toExternalForm()));
        earthMaterial.setSpecularMap(new Image(getClass().getResource("/earth-s.jpg").toExternalForm()));
        earthMaterial.setBumpMap(new Image(getClass().getResource("/earth-n.jpg").toExternalForm()));


        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setMaterial(earthMaterial);


        return sphere;
    }

    private void initMouseControl(Group group, Scene scene, Stage stage) {
        Rotate xRotate;
        Rotate yRotate;
        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY + anchorX - event.getSceneX());
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            double scaleFactor = delta > 0 ? 1.1 : 0.9;

            Camera camera = scene.getCamera();
            double cursorX = event.getSceneX();
            double cursorY = event.getSceneY();

            double directionX = (cursorX - scene.getWidth() / 2) * (scaleFactor - 1);
            double directionY = (cursorY - scene.getHeight() / 2) * (scaleFactor - 1);

            camera.setTranslateX(camera.getTranslateX() + directionX);
            camera.setTranslateY(camera.getTranslateY() + directionY);
            camera.setTranslateZ(camera.getTranslateZ() + delta);
        });
    }


}

