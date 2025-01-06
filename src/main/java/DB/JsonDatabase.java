package DB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    private static class DatabaseWrapper {
        List<UserScheme> users;
    }
}