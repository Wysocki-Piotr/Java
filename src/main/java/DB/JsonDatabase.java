package DB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDatabase {
    private static final String FILE_PATH = "src/main/java/DB/database.json";
    private Gson gson = new Gson();

    public JsonDatabase() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            createDataBase();
        }
    }

    private void createDataBase() throws IOException {
        DatabaseWrapper dbWrapper = new DatabaseWrapper();
        dbWrapper.users = new ArrayList<>();
        writeDatabase(dbWrapper);
    }

    public List<UserScheme> readUsers() throws IOException {
        FileReader reader = new FileReader(FILE_PATH);
        Type dbWrapperType = new TypeToken<DatabaseWrapper>() {}.getType();
        DatabaseWrapper dbWrapper = gson.fromJson(reader, dbWrapperType);
        reader.close();
        return dbWrapper.users;
    }

    public void writeUsers(List<UserScheme> users) throws IOException {
        DatabaseWrapper dbWrapper = new DatabaseWrapper();
        dbWrapper.users = users;
        writeDatabase(dbWrapper);
    }

    private void writeDatabase(DatabaseWrapper dbWrapper) throws IOException {
        FileWriter writer = new FileWriter(FILE_PATH);
        gson.toJson(dbWrapper, writer);
        writer.close();
    }
    public void deleteUser(String email) throws IOException{
        JsonDatabase db = new JsonDatabase();
        List<UserScheme> users = db.readUsers();
        Iterator<UserScheme> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserScheme user = iterator.next();
            if (user.getEmail().equalsIgnoreCase(email)) {
                iterator.remove();
                break;
            }
        }
        writeUsers(users);
    }

    private static class DatabaseWrapper {
        List<UserScheme> users;
    }
}