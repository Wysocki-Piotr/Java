package Components;

import DB.JsonDatabase;
import DB.UserScheme;
import Exceptions.Credentials;
import Exceptions.DBError;
import Exceptions.FileWithCountriesError;
import Exceptions.PageNotFoundException;
import Serwer.PredictionService;
import Serwer.WeatherForecast;
import Serwer.WeatherService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo.Potwierdzenie;
import javafx.scene.image.ImageView;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import static Components.UiComponents.place;
import static Serwer.WeatherService.filterWeather;
import static Alert.Localization.getCurrentLocalizationByApi;

public class Components {

    protected GridPane gridPane = new GridPane();
    protected GridPane gridPane2 = new GridPane();

    private final JsonDatabase db;

    protected boolean Logged = false;

    protected boolean first = true;
    protected Stage primaryStage;
    protected Label label1 = UiComponents.createLabel("Global", Color.WHITE, 60, -700, -370, -200);
    protected Label label2 = UiComponents.createLabel("Weather", Color.WHITE, 60, -620, -300, -200);
    protected Label label3 = UiComponents.createLabel("App", Color.WHITE, 60, -660, -230, -200);
    protected Label label4 = UiComponents.createLabel("Dodawanie ulubionych miejsc", Color.WHITE, 30, -720, -100, -200);
    protected Label label5 = UiComponents.createLabel("ALERTY", Color.WHITE, 30, -720, 50, -200);
    protected Label label6 = UiComponents.createLabel("Filtrowanie", Color.WHITE, 25, -300, -350,-200);
    protected Label label7 = UiComponents.createLabel("Zakres temperatur", Color.WHITE, 10, -100, -345,-200);
    protected Label result1 = UiComponents.createLabel("",Color.WHITE, 10, -200, -225, -200);
    protected Label result2 = UiComponents.createLabel("", Color.WHITE, 10, -200, -200, -200);
    protected Label result3 = UiComponents.createLabel("", Color.WHITE, 10, -200, -175, -200);
    protected Label alert1 = UiComponents.createLabel("Brak alertów z temperaturą",Color.WHITE, 20, -700,100, -200);
    protected Label alert2 = UiComponents.createLabel("Brak alertów z wiatrem",Color.WHITE, 20, -700,150, -200);
    protected Label alert3 = UiComponents.createLabel("Brak alertów z opadami",Color.WHITE, 20, -700,200, -200);
    protected Label noInternet = UiComponents.createLabel("Brak internetu, odpal program ponownie!", Color.WHITE, 30, -300, -330, -200);
    protected Label noResults = UiComponents.createLabel("Brak krajów spełniających warunków", Color.WHITE, 20, -50, -225, -200);
    //---------------Buttons-----------------
    protected Button buttonRegister = UiComponents.createButton("Create Account", -500, 5, -200, (int)Region.USE_COMPUTED_SIZE, (int)Region.USE_COMPUTED_SIZE, 10);
    protected Button buttonLogin = UiComponents.createButton("Log in", -470, 5, -200, (int)Region.USE_COMPUTED_SIZE, (int)Region.USE_COMPUTED_SIZE, 10);
    protected Button buttonEnter = UiComponents.createButton("Enter", -550, 30, -200, (int)Region.USE_COMPUTED_SIZE, (int)Region.USE_COMPUTED_SIZE, 20);
    protected Button buttonEnter2 = UiComponents.createButton("Enter", -620, -30, -200, (int)Region.USE_COMPUTED_SIZE, (int)Region.USE_COMPUTED_SIZE, 20);
    protected Button show = UiComponents.createButton("Pokaż ulubione", -720, 0, -200, 200, 25, 20);
    protected Button deleteAccount = UiComponents.createButton("Usuń konto", -700, -350, -200, 200, 25, 20);
    protected Button logOut = UiComponents.createButton("Wyloguj", -550, -350, -200, 200, 25, 20);
    protected Button filter = UiComponents.createButton("Filtruj", 200, -300,-200, 100, 40, 15);
    protected Button save = UiComponents.createButton("Zapisz prognozę do pliku pdf", -150, -150, -200, 250, 40, 15);

    //---------------TextFields-----------------
    protected PasswordField textRegisterPassRep = UiComponents.createPasswordField("Enter your password ", -620, -60, -200, 25, 200);
    protected TextField textRegisterEmail = UiComponents.createTextField("Enter your Name", -620, -100, -200, 25, 200);
    protected PasswordField textRegisterPass = UiComponents.createPasswordField("Enter your password again ", -620, -20, -200, 25, 200);
    protected PasswordField textLoginPass = UiComponents.createPasswordField("Enter your password", -620, -20, -200, 25, 200);
    protected TextField textLoginEmail = UiComponents.createTextField("Enter your email", -620, -70, -200, 25, 200);
    protected TextField textFavoritePlace = UiComponents.createTextField("Dodaj miejsce", -620, -60, -200, 25, 200);
    protected TextField textMin = UiComponents.createTextField("Podaj temperaturę min", -200, -320, -200, 25,150);
    protected TextField textMax = UiComponents.createTextField("Podaj temperaturę max", -25, -320, -200, 25,150);

    //-------------ComboBox-----------------
    ObservableList<String> options = FXCollections.observableArrayList("Clear","Rain","Clouds");
    ComboBox combo = new ComboBox<>(options);
    Control comb = (Control) combo;
    ComboBox comboBox = (ComboBox) place(comb,-300,-320,-200);
    ObservableList<String> options2 = FXCollections.observableArrayList( "Australia", "Iran", "China", "Canada");
    ComboBox combo2 = new ComboBox<>(options2);
    Control comb2 = (Control) combo2;
    ComboBox comboBoxCountries = (ComboBox) place(comb2,150,-320,-200);
    //---------------------images------------------------
    ImageView img1 = UiComponents.createImage(-50,-225,-200);
    ImageView img2 = UiComponents.createImage(-50,-200,-200);
    ImageView img3 = UiComponents.createImage(-50,-175,-200);

    private Image image = new Image(getClass().getResource("/clippy.jpg").toExternalForm());;
    private ImageView imageView = new ImageView(image);
    private Label clippyLabel = new Label("Kliknij na globus!!");

    protected List<Control> registerBlock = new ArrayList<>(Arrays.asList(textRegisterEmail,textRegisterPass,textRegisterPassRep,buttonLogin));
    protected List<Control> loginBlock = new ArrayList<>(Arrays.asList(textLoginEmail,textLoginPass,buttonRegister));
    protected List<Control> onStartBlock = new ArrayList<>(Arrays.asList(label1, label2, label3, textLoginEmail, textLoginPass, buttonRegister, buttonEnter));

    protected List<Node> secondBlock = new ArrayList<>(Arrays.asList(label4, textFavoritePlace, buttonEnter2, show,
            deleteAccount, label5, logOut, comboBox, label6, label7, comboBoxCountries, gridPane2, save,
            filter, textMin, textMax, result1, result2, result3, img1, img2, img3, alert1, alert2, alert3, gridPane, imageView, clippyLabel));

    protected AnimationTimer timer;

    protected final Sphere earth = new Sphere(150);

    public Camera camera = new PerspectiveCamera(true);

    public Group world = new Group(earth);
    public Group universe = new Group();

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 1000;

    public Scene sceneMain = new Scene(universe,WIDTH, HEIGHT, true);
    public static float getHEIGHT() {
        return HEIGHT;
    }

    public static float getWIDTH() {
        return WIDTH;
    }

    public JsonDatabase getDb() {
        return db;
    }

    public Components(Stage primaryStage) throws DBError {
        this.primaryStage = primaryStage;
        prepareClippy();
        universe.getChildren().addAll(label1, label2, label3, textLoginEmail, textLoginPass, buttonRegister, buttonEnter, world);

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
            throw new DBError("Problemy z utworzeniem bazy danych!");
        }
    }

    public void prepareClippy(){ // nie lepiej do UiComponents?


        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setTranslateZ(800);
        imageView.setTranslateX(460);
        imageView.setTranslateY(460);


        clippyLabel.setTextFill(Color.WHITE);
        clippyLabel.setFont(Font.font("Arial", 20));
        clippyLabel.setTranslateZ(800);
        clippyLabel.setTranslateX(460);
        clippyLabel.setTranslateY(560);
    }

    public void noInternetAvalaible(ScheduledExecutorService scheduler){
        if(Logged)
        transisionReverse(universe);
        Platform.runLater(() -> universe.getChildren().add(noInternet));
        buttonEnter.setMouseTransparent(true);
        buttonRegister.setMouseTransparent(true);
        scheduler.shutdown();
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


    public void buttonFunctionalities(String email, Group universe) {
        buttonEnter2.onActionProperty().set((ActionEvent e) -> {
            try {
                favorite(textFavoritePlace.getText(), email, universe);
            } catch (PageNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (DBError ex) {
                throw new RuntimeException(ex);
            }
        });
        show.onActionProperty().set((ActionEvent e) -> {
            try {
                favorite("a", email, universe);
            } catch (PageNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (DBError ex) {
                throw new RuntimeException(ex);
            }
            show.setVisible(false);
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
        filter.onActionProperty().set((ActionEvent e) ->{
            double wartosc1 = -100;
            double wartosc2 = 100;
            if (!textMin.getText().equals(""))
                wartosc1 = Double.parseDouble(textMin.getText());
            if(!textMax.getText().equals(""))
                wartosc2 = Double.parseDouble(textMax.getText());
            try {
                if (wartosc2 <= wartosc1)
                    throw new Exception("Podaj poprawny zakres!");
            } catch (NumberFormatException ex) {
                System.out.println("Podaj wartość liczbową!");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            Map mapa = null;
            try {
                mapa = filterWeather((String) comboBox.getValue(),(String) comboBoxCountries.getValue(), wartosc1, wartosc2);
            } catch (FileWithCountriesError ex) {
                throw new RuntimeException(ex);
            } catch (Credentials ex) {
                throw new RuntimeException(ex);
            } catch (PageNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            result1.setVisible(true);
            result2.setVisible(true);
            result3.setVisible(true);
            img1.setVisible(false);
            img2.setVisible(false);
            img3.setVisible(false);
            String r1 = (String) mapa.keySet().stream().skip(0).findFirst().orElse("");
            String r2 = (String) mapa.keySet().stream().skip(1).findFirst().orElse("");
            String r3 = (String) mapa.keySet().stream().skip(2).findFirst().orElse("");
            result1.setText(r1);
            result2.setText(r2);
            result3.setText(r3);
            img1.setImage(new Image((String) mapa.values().stream().findFirst().orElse("https://via.placeholder.com/1x1/000000")));
            img2.setImage(new Image((String) mapa.values().stream().skip(1).findFirst().orElse("https://via.placeholder.com/1x1/000000")));
            img3.setImage(new Image((String) mapa.values().stream().skip(2).findFirst().orElse("https://via.placeholder.com/1x1/000000")));
            place(img1, -250, -225, -200);
            place(img2, -250, -200, -200);
            place(img3, -250, -175, -200);
            img1.setVisible(true);
            img2.setVisible(true);
            img3.setVisible(true);
        });
        save.onActionProperty().set((ActionEvent e) ->{
            try {
                PredictionService.save(getCurrentLocalizationByApi());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void updateLabels(Boolean[] lista){
        if (lista[0].equals(false))
            alert1.setText("Brak alertów z temperaturą!");
        else
            alert1.setText("Jutro temperatura poniżej zera!");
        if(lista[1].equals(false))
            alert2.setText("Brak alertów z wiatrem!");
        else
            alert2.setText("Jutro mocny wiatr!");
        if(lista[2].equals(false))
            alert3.setText("Brak alertów z opadami!");
        else
            alert3.setText("Jutro silne opady deszczu!");
    }

    private void favorite(String text, String email, Group universe) throws PageNotFoundException, DBError {
        if (text.isEmpty())
            return;
        List<String> results = WeatherService.exist(text, email);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setLayoutX(-800);
        gridPane.setLayoutY(-300);
        gridPane.getChildren().clear();
        gridPane2.setHgap(10);
        gridPane2.setLayoutX(-800);
        gridPane2.setLayoutY(-340);
        gridPane2.getChildren().clear();
        ArrayList<Button> del = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            String place = results.get(i);
            Button tile = new Button(place);
            tile.setPrefSize(150, 150);
            tile.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #39FF14;" +
                    " -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1E88E5");
            tile.setOnAction(event -> {
                System.out.println("Selected place: " + place);
            });
            Button deleteButton = new Button("Usuń");
            deleteButton.setPrefSize(150, 30);
            deleteButton.setStyle("-fx-background-color: lightblue; -fx-border-color: red;" +
                    "-fx-text-fill: white; -fx-border-width: 2;");
            del.add(deleteButton);
            gridPane.add(tile, i, 0);
            gridPane2.add(deleteButton, i, 0);
            deleteButton.setOnAction((ActionEvent e) -> {
                try {
                    Button clickedButton = (Button) e.getSource();
                    delete(del.indexOf(clickedButton), email);
                    gridPane2.getChildren().remove(clickedButton);
                    del.remove(clickedButton);
                } catch (DBError ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public void delete(int ind, String mail) throws DBError {
        Node ulub = gridPane.getChildren().get(ind);
        Button tekst = (Button) ulub;
        String text = tekst.getText();
        gridPane.getChildren().remove(ulub);
        List<UserScheme> users = null;
        try {
            users = db.readUsers();
        } catch (IOException e) {
            throw new DBError("Problem z wczytywaniem użytkowników!");
        }
        UserScheme user = users.stream().filter(u -> u.getEmail().equals(mail)).findAny().orElse(null);
        users.remove(user);
        user.getFavPlaces().remove(text);
        users.add(user);
        try {
            db.writeUsers(users);
        } catch (IOException e) {
            throw new DBError("Problem z zapisywaniem użytkowników!");
        }
    }

    public void transision(Group universe, String email, Camera camera, Stage primaryStage) {
        Logged = true;
        Set<Control> controls = new HashSet<>();
        controls.addAll(onStartBlock);
        controls.addAll(registerBlock);
        controls.addAll(loginBlock);
        controls.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            translateTransition.setByX(-1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> universe.getChildren().remove(control));
        });
        secondBlock.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            if (!first)
                translateTransition.setByX(1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> {
                universe.getChildren().add(control);

            });
        });
        PauseTransition pauseForClippy = new PauseTransition(Duration.seconds(1.3));
        pauseForClippy.setOnFinished(event -> prepareClippy());


        buttonFunctionalities(email, universe);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(event -> earth.setOnMouseClicked(click ->{
            if(earth.getUserData() == null){
                earth.setUserData(true);
                animateCamera(camera, 1200, 400, false);


            }
        }));


        pause.play();
        pauseForClippy.play();

        first = false;
        result1.setVisible(false);
        result1.setVisible(false);
        result1.setVisible(false);

    }
    public void transisionReverse(Group universe) {
        Logged = false;
        secondBlock.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            if(control.equals(imageView) || control.equals(clippyLabel))
                translateTransition.setByX(1000);
            else
                translateTransition.setByX(-1000);
            translateTransition.play();
            translateTransition.setOnFinished(finish -> universe.getChildren().remove(control));
        });
        Set<Control> controls = new HashSet<>();
        controls.addAll(registerBlock);
        controls.addAll(onStartBlock);
        controls.forEach(control -> {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), control);
            translateTransition.setByX(1000);
            translateTransition.play();
            if (onStartBlock.contains(control))
                translateTransition.setOnFinished(finish -> universe.getChildren().add(control));
        });
        earth.setOnMouseClicked(null);
    }
    public void animateCamera(Camera camera, int duration, int frames,boolean reverse) {

        Timeline timeline = new Timeline();
        if (reverse) {
            double startX = 0;
            double startY = 100;
            double startZ = 200;
            double controlZ = startZ+2000;
            for (int i = 0; i <= frames; i++) {
                double t = (double) i / frames;
                double x = (1 - t) * startX + t * (-220);
                double y = (1 - t) * startY + t * (-20);
                double z = (1 - t) * (1 - t) * startZ + 2 * (1 - t) * t * controlZ + t * t * (-1600);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(t * duration), event1 -> {
                    camera.setTranslateX(x);
                    camera.setTranslateY(y);
                    camera.setTranslateZ(z);
                });
                timeline.getKeyFrames().add(keyFrame);
            }
            timeline.play();

        }
        else{

            double startX = camera.getTranslateX();
            double startY = camera.getTranslateY();
            double startZ = camera.getTranslateZ();
            double controlZ = startZ - 2000;
            for (int i = 0; i <= frames; i++) {

                double t = (double) i / frames;
                double x = (1 - t) * startX + t * earth.getTranslateX();
                double y = (1 - t) * startY + t * earth.getTranslateY();
                double z = (1 - t) * (1 - t) * startZ + 2 * (1 - t) * t * controlZ + t * t * earth.getTranslateY();
                KeyFrame keyFrame = new KeyFrame(Duration.millis(t * duration), event -> {
                    camera.setTranslateX(x);
                    camera.setTranslateZ(z);
                    camera.setTranslateY(y);
                });
                timeline.getKeyFrames().add(keyFrame);
            }
            System.out.println(earth.getTranslateZ());
            System.out.println(earth.getTranslateX());
            System.out.println(earth.getTranslateY());
            timeline.setOnFinished(event -> {

                MapViewComponents mapViewComponents = new MapViewComponents(this);
                earth.setUserData(null);
            });
            timeline.play();
        }
    }

}
