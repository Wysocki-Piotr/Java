package Components;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;

public class UiComponents {


    public static PasswordField createPasswordField(String promptText, int X, int Y, int Z, int height, int width) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setTranslateX(X);
        passwordField.setTranslateY(Y);
        passwordField.setTranslateZ(Z);
        passwordField.setPrefHeight(height);
        passwordField.setPrefWidth(width);
        passwordField.setFocusTraversable(false);
        return passwordField;
    }


    public static TextField createTextField(String promopText,int X,int Y, int Z,int height,int width){
        TextField textLogin = new TextField();
        textLogin.setPromptText(promopText);
        textLogin.setTranslateX(X);
        textLogin.setTranslateY(Y);
        textLogin.setTranslateZ(Z);
        textLogin.setPrefHeight(height);
        textLogin.setPrefWidth(width);
        textLogin.setFocusTraversable(false);
        return textLogin;
    }

    public static Button createButton(String text,int X,int Y,int Z,int sizeX,int sizeY,int fontSize){
        Button button = new Button(text);
        button.setTranslateX(X);
        button.setTranslateY(Y);
        button.setTranslateZ(Z);
        button.setPrefSize(sizeX,sizeY);
        button.setWrapText(true);
        button.setStyle("-fx-font-size: "+fontSize+" px;-fx-background-color: transparent;-fx-text-fill: white;");
        return button;
    }

    public static Label createLabel(String text, Color color, int size, int X, int Y, int Z){
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", size));
        label.setStyle("-fx-font-weight: bold;");
        label.setTranslateX(X);
        label.setTranslateY(Y);
        label.setTranslateZ(Z);

        return label;
    }
    public static Object place(Node control, int x, int y, int z){
        control.setTranslateX(x);
        control.setTranslateY(y);
        control.setTranslateZ(z);
        return control;
    }
    public static ImageView createImage(int x,int y,int z){
        ImageView img = new ImageView();
        img.setFitWidth(20);
        img.setFitHeight(20);
        img.setTranslateX(x);
        img.setTranslateX(y);
        img.setTranslateX(z);
        return img;
    }
}
