package org.example.demo;

import DB.JsonDatabase;
import DB.UserScheme;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;

public class Main extends Application {

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 1000;
    private AnimationTimer timer;

    private final Sphere sphere = new Sphere(150);
    private Scene scene;



    @Override
    public void start(Stage primaryStage) throws IOException {

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






        //---------------Labels-----------------
        Label label1 = UiComponents.createLabel("Global",Color.WHITE,60,-700,-370,-200);
        Label label2 = UiComponents.createLabel("Weather",Color.WHITE,60,-620,-300,-200);
        Label label3 = UiComponents.createLabel("App",Color.WHITE,60,-660,-230,-200);

        //---------------Buttons-----------------
        Button buttonRegister = UiComponents.createButton("Create Account",-560,5,-200,200,25,10);
        Button buttonLogin = UiComponents.createButton("Log in",-560,5,-200,200,25,10);
        Button buttonEnter = UiComponents.createButton("Enter",-620,30,-200,200,25,20);

        //---------------TextFields-----------------
        TextField textRegisterPassRep = UiComponents.createTextField("Enter your password ", -620, -60, -200, 25, 200);
        TextField textRegisterEmail= UiComponents.createTextField("Enter your Name", -620, -100, -200, 25, 200);
        TextField textRegisterPass = UiComponents.createTextField("Enter your password again ", -620, -20, -200, 25, 200);
        TextField textLoginPass = UiComponents.createTextField("Enter your password", -620, -20, -200, 25, 200);
        TextField textLoginEmail = UiComponents.createTextField("Enter your email", -620, -70, -200, 25, 200);

        buttonLogin.onActionProperty().set((ActionEvent event) -> {
            universe.getChildren().removeAll(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin);
            universe.getChildren().addAll(textLoginEmail,textLoginPass,buttonRegister);
            buttonLogin.setVisible(false);
            buttonRegister.setVisible(true);
        });

        buttonRegister.onActionProperty().set((ActionEvent event) -> {
            universe.getChildren().removeAll(textLoginEmail,textLoginPass,buttonRegister);
            universe.getChildren().addAll(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin);
            buttonLogin.setVisible(true);
            buttonRegister.setVisible(false);
        });

        universe.getChildren().addAll(label1,label2,label3,textLoginEmail,textLoginPass,buttonRegister,buttonEnter);
        scene = new Scene(universe, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        buttonEnter.onActionProperty().set((ActionEvent event) -> {

            if(buttonRegister.isVisible()){
                //logowanie
                String email = textLoginEmail.getText();
                String password = textLoginPass.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    System.out.println("Fill all fields");
                    textLoginEmail.clear();
                    textLoginPass.clear();
                    return;
                }

                try {
                    JsonDatabase db = new JsonDatabase();
                    List<UserScheme> users = db.readUsers();

                    boolean loginSuccessful = users.stream()
                            .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(password));

                    if (loginSuccessful) {
                        System.out.println("success");
                        Set<Control> controls = new HashSet<>();
                        controls.addAll(List.of(label1,label2,label3,textLoginEmail,textLoginPass,textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonRegister,buttonEnter,buttonLogin));
                        controls.forEach(control -> {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), control);
                            translateTransition.setByX(-1000);
                            translateTransition.play();
                            translateTransition.setOnFinished(finish ->{
                                universe.getChildren().removeAll(controls);
                            });
                        });
                        animateCamera(camera,  1200, 400,primaryStage);
                    } else {
                        System.out.println("Invalid");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textLoginEmail.clear();
                textLoginPass.clear();

            }else{
                //rejestracja
                String email = textRegisterEmail.getText();
                String password = textRegisterPass.getText();
                String passwordRep = textRegisterPassRep.getText();

                if (email.isEmpty() || password.isEmpty() || passwordRep.isEmpty()) {
                    System.out.println("Fill all fields");
                    textRegisterEmail.clear();
                    textRegisterPass.clear();
                    textRegisterPassRep.clear();
                    return;
                }

                if (!password.equals(passwordRep)) {
                    System.out.println("Passwords do not match.");
                    textRegisterEmail.clear();
                    textRegisterPass.clear();
                    textRegisterPassRep.clear();
                    return;
                }

                try {
                    JsonDatabase db = new JsonDatabase();
                    List<UserScheme> users = db.readUsers();

                    UserScheme newUser = new UserScheme();
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    users.add(newUser);

                    db.writeUsers(users);
                    System.out.println("registration successful");
                    Set<Control> controls = new HashSet<>();
                    controls.addAll(List.of(label1,label2,label3,textLoginEmail,textLoginPass,textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonRegister,buttonEnter,buttonLogin));
                    controls.forEach(control -> {
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), control);
                        translateTransition.setByX(-1000);

                        translateTransition.play();

                        translateTransition.setOnFinished(finish ->{
                            universe.getChildren().removeAll(controls);
                        });

                    });
                    animateCamera(camera,  1200, 400,primaryStage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                textRegisterEmail.clear();
                textRegisterPass.clear();
                textRegisterPassRep.clear();
            }
        });

        primaryStage.setTitle("Main Window");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        prepareAnimation();
    }

    private void prepareAnimation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                sphere.rotateProperty().set(sphere.getRotate() + 0.2);
            }
        };
        timer.start();
    }

    private void animateCamera(Camera camera, int duration, int frames, Stage primaryStage) {


        double startX = camera.getTranslateX();
        double startZ = camera.getTranslateZ();
        double controlZ = startZ - 2000;
        Timeline timeline = new Timeline();
        for (int i = 0; i <= frames; i++) {
            double t = (double) i / frames;
            double x = (1 - t) * startX + t * 0.5;
            double z = (1 - t) * (1 - t) * startZ + 2 * (1 - t) * t * controlZ + t * t * (200);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(t * duration), event -> {
                camera.setTranslateX(x);
                camera.setTranslateZ(z);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(event -> {

            MapView mapView = new MapView();
            mapView.setMapType(MapType.OSM);
            mapView.setCenter(new Coordinate(37.39822, -121.9643936));
            mapView.setZoom(14);
            mapView.initialize(Configuration.builder()
                    .showZoomControls(true)
                    .build());

            BorderPane root = new BorderPane();
            root.setCenter(mapView);
            double RemX = primaryStage.getX();
            double RemY = primaryStage.getY();
            primaryStage.getY();
            Scene mapViewScene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setScene(mapViewScene);
            primaryStage.setX(RemX);
            primaryStage.setY(RemY);
            primaryStage.show();
        });


        timeline.play();

    }

    private Node prepareEarth() {
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResource("/Blue_Marble_2002.png").toExternalForm()));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResource("/earth-l.jpg").toExternalForm()));
        earthMaterial.setSpecularMap(new Image(getClass().getResource("/earth-s.jpg").toExternalForm()));
        earthMaterial.setBumpMap(new Image(getClass().getResource("/earth-n.jpg").toExternalForm()));

        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setMaterial(earthMaterial);
        sphere.setTranslateZ(200);

        return sphere;
    }

}