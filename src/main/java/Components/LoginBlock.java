package Components;

import DB.JsonDatabase;
import DB.UserScheme;
import Exceptions.DBError;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LoginBlock {

    private final Components components;

    public LoginBlock(Components components) {
        this.components = components;
    }
    public void clearRegister (){
        components.textRegisterEmail.clear();
        components.textRegisterPass.clear();
        components.textRegisterPassRep.clear();
    }
    public void clearEmail(){
        components.textLoginEmail.clear();
        components.textLoginPass.clear();
    }

    public void prepareFunctionalityForLoginBlock(){
        components.buttonLogin.onActionProperty().set((ActionEvent event) -> {
            components.universe.getChildren().removeAll(components.textRegisterEmail, components.textRegisterPass,
                    components.textRegisterPassRep, components.buttonLogin);
            components.universe.getChildren().addAll(components.textLoginEmail, components.textLoginPass,
                    components.buttonRegister);
            components.buttonLogin.setVisible(false);
            components.buttonRegister.setVisible(true);

            clearRegister();
        });
        components.buttonEnter.onActionProperty().set((ActionEvent event) -> {

            if (components.buttonRegister.isVisible()) {

                String email = components.textLoginEmail.getText();
                String password = components.textLoginPass.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    clearEmail();
                    return;
                }

                try {
                    JsonDatabase db = components.getDb();
                    List<UserScheme> users = db.readUsers();

                    boolean loginSuccessful = users.stream()
                            .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(password));

                    if (loginSuccessful) {
                        components.transision(components.universe, email, components.camera, components.primaryStage);

                    } else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clearEmail();

            } else {
                //rejestracja
                String email = components.textRegisterEmail.getText();
                String password = components.textRegisterPass.getText();
                String passwordRep = components.textRegisterPassRep.getText();

                if (email.isEmpty() || password.isEmpty() || passwordRep.isEmpty()) {
                    clearRegister();
                    return;
                }

                if (!password.equals(passwordRep)) {
                    clearRegister();
                    return;
                }

                try {
                    JsonDatabase db = components.getDb();
                    List<UserScheme> users = db.readUsers();

                    for (UserScheme user : users) {
                        if (user.getEmail().equals(email)) {
                            clearRegister();
                            return;
                        }
                    }

                    UserScheme newUser = new UserScheme();
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setFavPlaces(new LinkedList<String>());
                    users.add(newUser);

                    db.writeUsers(users);
                    components.universe.getChildren().removeAll(components.textRegisterEmail, components.textRegisterPass,
                            components.textRegisterPassRep, components.buttonLogin);

                    components.universe.getChildren().addAll(components.textLoginEmail, components.textLoginPass,
                            components.buttonRegister);

                    components.transision(components.universe, email, components.camera, components.primaryStage);

                    components.buttonLogin.setVisible(false);
                    components.buttonRegister.setVisible(true);

                } catch (IOException e) {
                    try {
                        throw new DBError("Problem z pobraniem bazy danych");
                    } catch (DBError ex) {
                        System.out.println(ex.getMessage());
                    }
                }

            }
        });


    }

}
