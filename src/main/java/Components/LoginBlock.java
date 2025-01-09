package Components;

import DB.JsonDatabase;
import DB.UserScheme;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Control;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LoginBlock {


    private final Components components;

    public LoginBlock(Components components) {
        this.components = components;
    }

    public void prepareFunctionalityForLoginBlock(){
        components.buttonLogin.onActionProperty().set((ActionEvent event) -> {
            components.universe.getChildren().removeAll(components.textRegisterEmail, components.textRegisterPass,
                    components.textRegisterPassRep, components.buttonLogin);
            components.universe.getChildren().addAll(components.textLoginEmail, components.textLoginPass,
                    components.buttonRegister);
            components.buttonLogin.setVisible(false);
            components.buttonRegister.setVisible(true);
        });
        components.buttonEnter.onActionProperty().set((ActionEvent event) -> {

            if (components.buttonRegister.isVisible()) {
                //logowanie
                String email = components.textLoginEmail.getText();
                String password = components.textLoginPass.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    System.out.println("Fill all fields");
                    components.textLoginEmail.clear();
                    components.textLoginPass.clear();
                    return;
                }

                try {
                    JsonDatabase db = components.getDb();
                    List<UserScheme> users = db.readUsers();

                    boolean loginSuccessful = users.stream()
                            .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(password));

                    if (loginSuccessful) {
                        System.out.println("success");
                        components.transision(components.universe, email, components.camera, components.primaryStage);
                    } else {
                        System.out.println("Invalid");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                components.textLoginEmail.clear();
                components.textLoginPass.clear();

            } else {
                //rejestracja
                String email = components.textRegisterEmail.getText();
                String password = components.textRegisterPass.getText();
                String passwordRep = components.textRegisterPassRep.getText();

                if (email.isEmpty() || password.isEmpty() || passwordRep.isEmpty()) {
                    System.out.println("Fill all fields");
                    components.textRegisterEmail.clear();
                    components.textRegisterPass.clear();
                    components.textRegisterPassRep.clear();
                    return;
                }

                if (!password.equals(passwordRep)) {
                    System.out.println("Passwords do not match.");
                    components.textRegisterEmail.clear();
                    components.textRegisterPass.clear();
                    components.textRegisterPassRep.clear();
                    return;
                }

                try {
                    JsonDatabase db = components.getDb();
                    List<UserScheme> users = db.readUsers();

                    UserScheme newUser = new UserScheme();
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setFavPlaces(new LinkedList<String>());
                    users.add(newUser);

                    db.writeUsers(users);
                    System.out.println("registration successful");
                    Set<Control> controls = new HashSet<>();
                    controls.addAll(List.of(components.label1, components.label2, components.label3, components.textLoginEmail, components.textLoginPass,
                            components.textRegisterEmail, components.textRegisterPass, components.textRegisterPassRep,
                            components.buttonRegister,components.buttonEnter, components.buttonLogin));
                    controls.forEach(control -> {
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), control);
                        translateTransition.setByX(-1000);
                        translateTransition.play();
                        translateTransition.setOnFinished(finish -> {
                            components.universe.getChildren().removeAll(controls);
                        });
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                components.textRegisterEmail.clear();
                components.textRegisterPass.clear();
                components.textRegisterPassRep.clear();
            }
        });


    }

}
