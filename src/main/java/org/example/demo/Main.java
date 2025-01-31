package org.example.demo;

import Components.Components;
import Exceptions.DBError;
import Serwer.Config;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import Alert.AlertManagement;
import static Serwer.Config.isInternetAvailable;


public class Main extends Application {

    public Main(){
    }

    @Override
    public void start(Stage primaryStage) throws DBError {
        Components components = new Components(primaryStage);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Global Weather App");

        primaryStage.setResizable(false);
        primaryStage.setScene(components.sceneMain);

        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        Serwer.Config internetChecker = new Config(components);
        internetChecker.startChecking();
    }

}