package Components;

import DB.JsonDatabase;
import DB.UserScheme;
import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import org.example.demo.Potwierdzenie;

import java.io.IOException;
import java.util.*;

public class Components {

    protected GridPane gridPane = new GridPane();
    private JsonDatabase db;
    protected boolean Logged = false;
    protected boolean first = true;
    protected Stage primaryStage;
    protected Label label1 = UiComponents.createLabel("Global", Color.WHITE, 60, -700, -370, -200);
    protected Label label2 = UiComponents.createLabel("Weather", Color.WHITE, 60, -620, -300, -200);
    protected Label label3 = UiComponents.createLabel("App", Color.WHITE, 60, -660, -230, -200);
    protected Label label4 = UiComponents.createLabel("Dodawanie ulubionych miejsc", Color.WHITE, 30, -720, -100, -200);
    protected Label label5 = UiComponents.createLabel("ALERTY", Color.WHITE, 30, -720, 50, -200);
    //---------------Buttons-----------------
    protected Button buttonRegister = UiComponents.createButton("Create Account", -560, 5, -200, 200, 25, 10);
    protected Button buttonLogin = UiComponents.createButton("Log in", -560, 5, -200, 200, 25, 10);
    protected Button buttonEnter = UiComponents.createButton("Enter", -600, 30, -200, 200, 25, 20);
    protected Button buttonEnter2 = UiComponents.createButton("Enter", -620, -30, -200, 200, 25, 20);
    protected Button show = UiComponents.createButton("Pokaż ulubione", -720, 0, -200, 200, 25, 20);
    protected Button delete1 = UiComponents.createButton("Usuń", -780, -320, -200, 200, 25, 20);
    protected Button delete2 = UiComponents.createButton("Usuń", -630, -320, -200, 200, 25, 20);
    protected Button delete3 = UiComponents.createButton("Usuń", -480, -320, -200, 200, 25, 20);
    protected Button deleteAccount = UiComponents.createButton("Usuń konto", -700, -350, -200, 200, 25, 20);
    protected Button logOut = UiComponents.createButton("Wyloguj", -550, -350, -200, 200, 25, 20);

    //---------------TextFields-----------------
    protected TextField textRegisterPassRep = UiComponents.createTextField("Enter your password ", -620, -60, -200, 25, 200);
    protected TextField textRegisterEmail = UiComponents.createTextField("Enter your Name", -620, -100, -200, 25, 200);
    protected TextField textRegisterPass = UiComponents.createTextField("Enter your password again ", -620, -20, -200, 25, 200);
    protected TextField textLoginPass = UiComponents.createTextField("Enter your password", -620, -20, -200, 25, 200);
    protected TextField textLoginEmail = UiComponents.createTextField("Enter your email", -620, -70, -200, 25, 200);
    protected TextField textFavoritePlace = UiComponents.createTextField("Dodaj miejsce", -620, -60, -200, 25, 200);

    protected List<Control> registerBlock = new ArrayList<>(Arrays.asList(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin));
    protected List<Control> loginBlock = new ArrayList<>(Arrays.asList(textLoginEmail,textLoginPass,buttonRegister));
    protected List<Control> onStartBlock = new ArrayList<>(Arrays.asList(label1, label2, label3, textLoginEmail, textLoginPass, buttonRegister, buttonEnter));
    protected AnimationTimer timer;

    protected final Sphere earth = new Sphere(150);;

    public Camera camera = new PerspectiveCamera(true);

    public Group world = new Group(earth);
    public Group universe = new Group();


    private static final float WIDTH = 1400;
    private static final float HEIGHT = 1000;

    public Scene sceneMain = new Scene(universe,WIDTH, HEIGHT, true);
    //public Scene sceneMap = new Scene();
    public static float getHEIGHT() {
        return HEIGHT;
    }

    public static float getWIDTH() {
        return WIDTH;
    }

    public JsonDatabase getDb() {
        return db;
    }

    public Components(Stage primaryStage){
        this.primaryStage = primaryStage;
        universe.getChildren().addAll(label1, label2, label3, textLoginEmail, textLoginPass, buttonRegister, buttonEnter,world);
        prepareCamera();
        prepareEarth();
        prepareSceneMain();
        prepareAnimation();


        RegisterBlock registerFunctionality = new RegisterBlock(this);
        registerFunctionality.prepareFunctionalityForRegisterBlock();
        LoginBlock loginFunctionality = new LoginBlock(this);
        loginFunctionality.prepareFunctionalityForLoginBlock();
        try {
            db = new JsonDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepareEarth() {
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResource("/Blue_Marble_2002.png").toExternalForm()));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResource("/earth-l.jpg").toExternalForm()));
        earthMaterial.setSpecularMap(new Image(getClass().getResource("/earth-s.jpg").toExternalForm()));
        earthMaterial.setBumpMap(new Image(getClass().getResource("/earth-n.jpg").toExternalForm()));

        earth.setRotationAxis(Rotate.Y_AXIS);
        earth.setTranslateY(earth.getTranslateY() + 100);
        earth.setMaterial(earthMaterial);
        earth.setTranslateZ(200);
    }

    private void prepareSceneMain(){
        sceneMain.setFill(Color.BLACK);
        sceneMain.setCamera(camera);
    }

    private void prepareCamera(){
        camera.setNearClip(1);
        camera.setFarClip(1000000);
        camera.translateZProperty().set(-1600);
        camera.translateXProperty().set(-220);
        camera.translateYProperty().set(-20);
    }

    private void prepareAnimation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                earth.rotateProperty().set(earth.getRotate() + 0.2);
            }
        };
        timer.start();
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
        List<UserScheme> users = db.readUsers();
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(mail)).findAny().orElse(null);
        users.remove(user);
        user.getFavPlaces().remove(text);
        users.add(user);
        db.writeUsers(users);
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
        earth.setOnMouseClicked(click -> animateCamera(camera, 1200, 400, primaryStage,false));
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
        earth.setOnMouseClicked(null);
    }
    private void animateCamera(Camera camera, int duration, int frames, Stage primaryStage,boolean reverse) {

        Timeline timeline = new Timeline();

        if(reverse){
            double startX = earth.getTranslateX();
            double startZ = earth.getTranslateZ();
            double controlZ = startZ + 2000;
            for (int i = 0; i <= frames; i++) {
                double t = (double) i / frames;
                double x = (1 - t) * startX + t * 0.5;
                double z = (1 - t) * (1 - t) * startZ + 2 * (1 - t) * t * controlZ + t * t * (-1600);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(t * duration), event -> {
                    camera.setTranslateX(x);
                    camera.setTranslateZ(z);
                });
                timeline.getKeyFrames().add(keyFrame);
            }

        }else{
            double startX = camera.getTranslateX();
            double startZ = camera.getTranslateZ();
            double controlZ = startZ - 2000;
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
                Scene mapViewScene = new Scene(root, Components.getWIDTH(), Components.getHEIGHT());
                primaryStage.setScene(mapViewScene);
                primaryStage.setX(RemX);
                primaryStage.setY(RemY);
                primaryStage.show();
            });
            timeline.play();
        }


    }
}
