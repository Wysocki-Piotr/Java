package Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
public class Localization {
    private static final String API_URL = "http://ip-api.com/json/";

    public static double[] getCurrentLocalizationByApi() {
        try {
            URL url = new URL(API_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            double latitude = jsonResponse.getDouble("lat");
            double longitude = jsonResponse.getDouble("lon");
            return new double[]{latitude,longitude,};
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while connecting to API", e);
        }
    }
}
