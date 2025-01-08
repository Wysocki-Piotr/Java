package org.example.demo;

import Serwer.Obsluga;
import DB.JsonDatabase;
import DB.UserScheme;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


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
    private boolean Logged = false;
    private boolean first = true;

    //---------------Labels-----------------
    private Label label1 = UiComponents.createLabel("Global", Color.WHITE, 60, -700, -370, -200);
    private Label label2 = UiComponents.createLabel("Weather", Color.WHITE, 60, -620, -300, -200);
    private Label label3 = UiComponents.createLabel("App", Color.WHITE, 60, -660, -230, -200);
    private Label label4 = UiComponents.createLabel("Dodawanie ulubionych miejsc", Color.WHITE, 30, -720, -100, -200);
    private Label label5 = UiComponents.createLabel("ALERTY", Color.WHITE, 30, -720, 50, -200);
    //---------------Buttons-----------------
    private Button buttonRegister = UiComponents.createButton("Create Account", -560, 5, -200, 200, 25, 10);
    private Button buttonLogin = UiComponents.createButton("Log in", -560, 5, -200, 200, 25, 10);
    private Button buttonEnter = UiComponents.createButton("Enter", -600, 30, -200, 200, 25, 20);
    private Button buttonEnter2 = UiComponents.createButton("Enter", -620, -30, -200, 200, 25, 20);
    private Button show = UiComponents.createButton("Pokaż ulubione", -720, 0, -200, 200, 25, 20);
    private Button delete1 = UiComponents.createButton("Usuń", -780, -320, -200, 200, 25, 20);
    private Button delete2 = UiComponents.createButton("Usuń", -630, -320, -200, 200, 25, 20);
    private Button delete3 = UiComponents.createButton("Usuń", -480, -320, -200, 200, 25, 20);
    private Button deleteAccount = UiComponents.createButton("Usuń konto", -700, -350, -200, 200, 25, 20);
    private Button logOut = UiComponents.createButton("Wyloguj", -550, -350, -200, 200, 25, 20);

    //---------------TextFields-----------------
    private TextField textRegisterPassRep = UiComponents.createTextField("Enter your password ", -620, -60, -200, 25, 200);
    private TextField textRegisterEmail = UiComponents.createTextField("Enter your Name", -620, -100, -200, 25, 200);
    private TextField textRegisterPass = UiComponents.createTextField("Enter your password again ", -620, -20, -200, 25, 200);
    private TextField textLoginPass = UiComponents.createTextField("Enter your password", -620, -20, -200, 25, 200);
    private TextField textLoginEmail = UiComponents.createTextField("Enter your email", -620, -70, -200, 25, 200);
    private TextField textFavoritePlace = UiComponents.createTextField("Dodaj miejsce", -620, -60, -200, 25, 200);
    GridPane gridPane = new GridPane();

    public Main() throws IOException {
    }

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
        world.getChildren().addAll(prepareEarth(primaryStage));
        universe.getChildren().addAll(world);

        buttonLogin.onActionProperty().set((ActionEvent event) -> switchToLog(universe));

        buttonRegister.onActionProperty().set((ActionEvent event) -> switchToRegister(universe));

        universe.getChildren().addAll(label1, label2, label3, textLoginEmail, textLoginPass, buttonRegister, buttonEnter);
        scene = new Scene(universe, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        primaryStage.setTitle("Main Window");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        prepareAnimation();

        buttonEnter.onActionProperty().set((ActionEvent event) -> {

            if (buttonRegister.isVisible()) {
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
                        transision(universe, email, camera, primaryStage);
                    } else {
                        System.out.println("Invalid");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textLoginEmail.clear();
                textLoginPass.clear();

            } else {
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
                    register(email, password, universe);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                textRegisterEmail.clear();
                textRegisterPass.clear();
                textRegisterPassRep.clear();
            }
        });

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
            mapView.setCenter(new Coordinate(52.2312, 20.9897));
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

    private Node prepareEarth(Stage primaryStage) {
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResource("/Blue_Marble_2002.png").toExternalForm()));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResource("/earth-l.jpg").toExternalForm()));
        earthMaterial.setSpecularMap(new Image(getClass().getResource("/earth-s.jpg").toExternalForm()));
        earthMaterial.setBumpMap(new Image(getClass().getResource("/earth-n.jpg").toExternalForm()));

        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setTranslateY(sphere.getTranslateY() + 100);
        sphere.setMaterial(earthMaterial);
        sphere.setTranslateZ(200);

        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(1000000);
        camera.translateZProperty().set(-1600);
        camera.translateXProperty().set(-220);
        camera.translateYProperty().set(50);

        return sphere;
    }

    private void favorite(String text, String email, Group universe) throws IOException {
        if (text.isEmpty())
            return;
        List<String> results = Serwer.Obsluga.exist(text, email);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setLayoutX(-800);
        gridPane.setLayoutY(-300);
        gridPane.getChildren().clear();
        for (int i = 0; i < results.size(); i++) {
            String place = results.get(i);
            Button tile = new Button(place);
            tile.setPrefSize(150, 150);
            tile.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #39FF14;" +
                    " -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1E88E5");
            tile.setOnAction(event -> {
                System.out.println("Selected place: " + place);
            });
            gridPane.add(tile, i, 0);
        }
        new Thread(() -> {
            Platform.runLater(() -> universe.getChildren().add(gridPane));
        }).start();
    }

    public void delete(int ind, String mail) throws IOException {
        Node ulub = gridPane.getChildren().get(ind);
        Button tekst = (Button) ulub;
        String text = tekst.getText();
        System.out.println(text);
        gridPane.getChildren().remove(ulub);
        JsonDatabase db = new JsonDatabase();
        List<UserScheme> users = db.readUsers();
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(mail)).findAny().orElse(null);
        users.remove(user);
        user.getFavPlaces().remove(text);
        users.add(user);
        db.writeUsers(users);
    }

    public void ButtonFunctionalities(String email, Group universe) {
        buttonEnter2.onActionProperty().set((ActionEvent e) -> {
            try {
                favorite(textFavoritePlace.getText(), email, universe);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        show.onActionProperty().set((ActionEvent e) -> {
            try {
                favorite("a", email, universe);
                show.setVisible(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        delete1.onActionProperty().set((ActionEvent e) -> {
            try {
                delete(0, email);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        delete2.onActionProperty().set((ActionEvent e) -> {
            try {
                delete(1, email);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        delete3.onActionProperty().set((ActionEvent e) -> {
            try {
                delete(2, email);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteAccount.onActionProperty().set((ActionEvent e) -> {
            try {
                boolean confirmed = Potwierdzenie.show("Czy na pewno chcesz usunąć konto?");
                if (confirmed) {
                    System.out.println("Usunięte");
                    JsonDatabase db = new JsonDatabase();
                    db.deleteUser(email);
                    transisionReverse(universe);
                } else {
                    System.out.println("Anulowane");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        logOut.onActionProperty().set((ActionEvent e) -> {
            transisionReverse(universe);
        });
    }

    public void transision(Group universe, String email, Camera camera, Stage primaryStage) {
        Logged = true;
        Set<Control> controls = new HashSet<>();
        controls.addAll(List.of(label1, label2, label3, textLoginEmail, textLoginPass, textRegisterEmail, textRegisterPass, textRegisterPassRep, buttonRegister, buttonEnter, buttonLogin));
        controls.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            translateTransition.setByX(-1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> universe.getChildren().remove(control));
        });
        Set<Control> controlsToShow = Set.of(
                label4, textFavoritePlace, buttonEnter2, show,
                delete1, delete2, delete3, deleteAccount, label5, logOut
        );
        controlsToShow.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            if (!first)
                translateTransition.setByX(1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> universe.getChildren().add(control));
        });
        ButtonFunctionalities(email, universe);
        sphere.setOnMouseClicked(click -> animateCamera(camera, 1200, 400, primaryStage));
        first = false;
    }

    public void transisionReverse(Group universe) {
        Logged = false;
        Set<Control> controlsToRemove = Set.of(
                label4, textFavoritePlace, buttonEnter2, show,
                delete1, delete2, delete3, deleteAccount, label5, logOut
        );
        controlsToRemove.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            translateTransition.setByX(-1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> universe.getChildren().remove(control));
        });
        Set<Control> controlsToAdd = new HashSet<>();
        controlsToAdd.addAll(List.of(label1, label2, label3, textLoginEmail, textLoginPass, buttonEnter, buttonRegister));
        Set<Control> controls = new HashSet<>();
        controls.addAll(List.of(label1, label2, label3, textLoginEmail, textLoginPass, textRegisterEmail, textRegisterPass, textRegisterPassRep, buttonRegister, buttonEnter, buttonLogin));
        controls.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            translateTransition.setByX(1000);
            translateTransition.play();
            if (controlsToAdd.contains(control))
            translateTransition.setOnFinished(finish -> universe.getChildren().add(control));
        });
        sphere.setOnMouseClicked(null);
    }
    public void register(String email, String password, Group universe) throws IOException {
        JsonDatabase db = new JsonDatabase();
        List<UserScheme> users = db.readUsers();
        Iterator<UserScheme> iterator = users.iterator();
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        boolean isValid = Pattern.matches(emailRegex, email);
        if(!isValid) {
            System.out.println("Niepoprawny format maila!");
            return;
        }
        while(iterator.hasNext()){
            if (iterator.next().getEmail() == email) {
                System.out.println("Podany użytkownik isnieje!");
                return;
            }
        }
        users.add(new UserScheme(email, password));
        db.writeUsers(users);
        switchToLog(universe);
    }
    public void switchToRegister(Group universe){
        universe.getChildren().removeAll(textLoginEmail, textLoginPass, buttonRegister);
        universe.getChildren().addAll(textRegisterEmail, textRegisterPass, textRegisterPassRep, buttonLogin);
        buttonLogin.setVisible(true);
        buttonRegister.setVisible(false);
    }
    public void switchToLog(Group universe){
            universe.getChildren().removeAll(textRegisterEmail, textRegisterPass, textRegisterPassRep, buttonLogin);
            universe.getChildren().addAll(textLoginEmail, textLoginPass, buttonRegister);
            buttonLogin.setVisible(false);
            buttonRegister.setVisible(true);
    }
}