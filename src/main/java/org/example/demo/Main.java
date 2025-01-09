package org.example.demo;

import Components.Components;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {


    public Main() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Components components = new Components(primaryStage);
        primaryStage.setTitle("Global Weather App");
        primaryStage.setResizable(false);
        primaryStage.setScene(components.sceneMain);
        primaryStage.show();
    }

}