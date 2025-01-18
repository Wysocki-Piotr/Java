package Components;

import java.util.*;

import static Serwer.PredictionService.*;
import Components.Components.*;
import javafx.application.Platform;

public class AlertManagement {
    protected Boolean [] lista = {false, false, false};
    protected Components components;

    public AlertManagement(Components components) {
        this.components = components;
    }

    public void checkAlert(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                lista[0] = checkTempAlert();
                lista[1] = checkWindAlert();
                lista[2] = checkRainAlert();
                Platform.runLater(() -> components.updateLabels(lista));
                }
        }, 9000, 3000);
    }
    public void scheduleTemperatureCheck(){
        checkAlert();
    }
}
