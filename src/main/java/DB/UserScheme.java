package DB;

import java.util.LinkedList;

public class UserScheme {
    private String email;
    private String password;

    private LinkedList<String> favPlaces;

    public UserScheme() {
    }

    public UserScheme(String text, String text2) {
        email = text;
        password = text2;
        favPlaces = new LinkedList<>();
    }

    public LinkedList<String> getFavPlaces() {
        return favPlaces;
    }

    public void setFavPlaces(LinkedList<String> favPlaces) {
        this.favPlaces = favPlaces;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
