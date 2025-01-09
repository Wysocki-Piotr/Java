package Components;

import javafx.event.ActionEvent;

public class RegisterBlock {

    private final Components components;

    public RegisterBlock(Components components) {
        this.components = components;
    }

    public void prepareFunctionalityForRegisterBlock() {
        components.buttonRegister.onActionProperty().set((ActionEvent event) -> {
            components.universe.getChildren().removeAll(components.textLoginEmail, components.textLoginPass, components.buttonRegister);
            components.universe.getChildren().addAll(components.textRegisterEmail, components.textRegisterPass, components.textRegisterPassRep, components.buttonLogin);
            components.buttonLogin.setVisible(true);
            components.buttonRegister.setVisible(false);
        });
    }
}
