package org.example.demo;

import DB.JsonDatabase;
import DB.UserScheme;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
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

public class Main extends Application {

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 1000;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private final Sphere sphere = new Sphere(150);

    private boolean LOGGED = false;



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
        Scene scene = new Scene(universe, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        buttonEnter.onActionProperty().set((ActionEvent event) -> {

            if(buttonRegister.isVisible()){
                //logowanie
                String email = textLoginEmail.getText();
                String password = textLoginPass.getText();

                if (InputValidator.isEmpty(email) || InputValidator.isEmpty(password)) {
                    System.out.println("fill all fields");
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
                        LOGGED = true;
                        Set<Control> controls = new HashSet<>();
                        controls.addAll(List.of(label1,label2,label3,textLoginEmail,textLoginPass,textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonRegister,buttonEnter));
                        controls.forEach(control -> {
                            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), control);
                            translateTransition.setByX(-1000);

                            translateTransition.play();

                            translateTransition.setOnFinished(finish ->{
                                universe.getChildren().removeAll(controls);
                            });

                        });
                        initMouseControl(world, scene, primaryStage);
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

                if (InputValidator.isEmpty(email) || InputValidator.isEmpty(password) || InputValidator.isEmpty(passwordRep)) {
                    System.out.println("fill all fields");
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
                    LOGGED = true;
                    Set<Control> controls = new HashSet<>();
                    controls.addAll(List.of(label1,label2,label3,textLoginEmail,textLoginPass,textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonRegister,buttonEnter));
                    controls.forEach(control -> {
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), control);
                        translateTransition.setByX(-1000);

                        translateTransition.play();

                        translateTransition.setOnFinished(finish ->{
                            universe.getChildren().removeAll(controls);
                        });

                    });
                    initMouseControl(world, scene, primaryStage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textRegisterEmail.clear();
                textRegisterPass.clear();
                textRegisterPassRep.clear();
            }




        });






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
        if(!LOGGED){
            return;
        }else{
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
}