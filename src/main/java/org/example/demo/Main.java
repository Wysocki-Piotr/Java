package org.example.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
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

    @FXML
    private Label welcomeLabel;

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
        world.getChildren().addAll(prepareEarth());
        universe.getChildren().addAll(world);
        universe.getChildren().add(root);

        Label label1 = createLabel("Global",Color.WHITE,60,-700,-370,-200);
        Label label2 = createLabel("Weather",Color.WHITE,60,-620,-300,-200);
        Label label3 = createLabel("App",Color.WHITE,60,-660,-230,-200);




        //-------------------Buttons--------------------------
        Button buttonRegister = new Button("Create Account");
        buttonRegister.setTranslateX(-560);
        buttonRegister.setTranslateY(5);
        buttonRegister.setTranslateZ(-200);
        buttonRegister.setPrefSize(200,25);
        buttonRegister.setWrapText(true);
        buttonRegister.setStyle("-fx-font-size: 10px;-fx-background-color: transparent;-fx-text-fill: white;");


        Button buttonLogin = new Button("Log in");
        buttonLogin.setTranslateX(-560);
        buttonLogin.setTranslateY(5);
        buttonLogin.setTranslateZ(-200);
        buttonLogin.setPrefSize(200,25);
        buttonLogin.setWrapText(true);
        buttonLogin.setStyle("-fx-font-size: 10px;-fx-background-color: transparent;-fx-text-fill: white;");


        //-------------------Register--------------------------
        TextField textRegisterPassRep = new TextField();
        textRegisterPassRep.setPromptText("Enter your password");
        textRegisterPassRep.setTranslateX(-620);
        textRegisterPassRep.setTranslateY(-60);
        textRegisterPassRep.setTranslateZ(-200);
        textRegisterPassRep.setPrefHeight(25);
        textRegisterPassRep.setPrefWidth(200);

        TextField textRegisterEmail = new TextField();
        textRegisterEmail.setPromptText("Enter your email");
        textRegisterEmail.setTranslateX(-620);
        textRegisterEmail.setTranslateY(-100);
        textRegisterEmail.setTranslateZ(-200);
        textRegisterEmail.setPrefHeight(25);
        textRegisterEmail.setPrefWidth(200);

        TextField textRegisterPass = new TextField();
        textRegisterPass.setPromptText("Enter your password again");
        textRegisterPass.setTranslateX(-620);
        textRegisterPass.setTranslateY(-20);
        textRegisterPass.setTranslateZ(-200);
        textRegisterPass.setPrefHeight(25);
        textRegisterPass.setPrefWidth(200);

        //-------------------Login--------------------------
        TextField textLoginPass = new TextField();
        textLoginPass.setPromptText("Enter your password");
        textLoginPass.setTranslateX(-620);
        textLoginPass.setTranslateY(-20);
        textLoginPass.setTranslateZ(-200);
        textLoginPass.setPrefHeight(25);
        textLoginPass.setPrefWidth(200);
        textLoginPass.setFocusTraversable(false);

        TextField textLoginEmail = new TextField();
        textLoginEmail.setPromptText("Enter your email");
        textLoginEmail.setTranslateX(-620);
        textLoginEmail.setTranslateY(-70);
        textLoginEmail.setTranslateZ(-200);
        textLoginEmail.setPrefHeight(25);
        textLoginEmail.setPrefWidth(200);
        textLoginEmail.setFocusTraversable(false);



        buttonLogin.onActionProperty().set((ActionEvent event) -> {
            universe.getChildren().removeAll(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin);
            universe.getChildren().addAll(textLoginEmail,textLoginPass,buttonRegister);
        });

        buttonRegister.onActionProperty().set((ActionEvent event) -> {
            universe.getChildren().removeAll(textLoginEmail,textLoginPass,buttonRegister);
            universe.getChildren().addAll(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin);
        });


        universe.getChildren().addAll(label1,label2,label3,textLoginEmail,textLoginPass,buttonRegister);
        Scene scene = new Scene(universe, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        //scene.getStylesheets().add(getClass().getResource("/cssstyling/styles.css").toExternalForm());
        initMouseControl(world, scene, primaryStage);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
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

        primaryStage.requestFocus();
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

    private Label createLabel(String text,Color color,int size,int X,int Y,int Z){
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", size));
        label.setStyle("-fx-font-weight: bold;");
        label.setTranslateX(X);
        label.setTranslateY(Y);
        label.setTranslateZ(Z);
        return label;
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