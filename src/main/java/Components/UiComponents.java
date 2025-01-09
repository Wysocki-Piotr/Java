package Components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UiComponents {

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

}
