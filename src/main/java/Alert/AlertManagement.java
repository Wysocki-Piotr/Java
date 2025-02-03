package Alert;

import java.io.IOException;
import java.util.*;

import static Serwer.PredictionService.*;

import Components.Components;
import Components.Components.*;
import javafx.application.Platform;

public class AlertManagement {
    protected Boolean [] lista = {false, false, false};
    protected Components components;
    private double lat;
    private double lon;
    public AlertManagement(Components components) {
        this.components = components;
    }

    public void checkAlert(){
        Timer timer = new Timer();
        double [] coord = Localization.getCurrentLocalizationByApi();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis() / 1000;
                lista[0] = checkTempAlert(coord[0], coord[1], currentTime);
                lista[1] = checkWindAlert(coord[0], coord[1], currentTime);
                lista[2] = checkRainAlert(coord[0], coord[1], currentTime);
                Platform.runLater(() -> components.updateLabels(lista));
                }
        }, 0, 300000);
    }
    public void scheduleTemperatureCheck(){
        checkAlert();
    }
}
