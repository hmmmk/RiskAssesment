package com.example.database;

import com.example.database.models.User;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DatabaseManager {

    private static final String USER = "root";
    private static final String PASSWORD = "admin";
    private static DatabaseManager instance;
    public ScriptRunner runner;
    private Connection connection;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    public Connection connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/risk_assessment?useUnicode=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true", USER, PASSWORD);
        runner = new ScriptRunner(connection);

        initializeDatabase();

        return connection;
    }

    private void initializeDatabase() {
        Reader reader = new InputStreamReader(getFileFromResourceAsStream("sql/schema.sql"));
        runner.runScript(reader);
    }

    public boolean addUser(String login, String password) {
        String query = "INSERT INTO Users(login, password) VALUES ('" + login + "', '" + password + "');";

        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public List<User> getUser(String login) {
        String query = "SELECT * FROM Users WHERE login='" + login + "';";

        ArrayList<User> resultList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultList.add(
                        new User(
                                result.getInt("id"),
                                result.getString("login"),
                                result.getString("password")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return resultList;
    }

    public List<User> getAllUsers() {
        String query = "SELECT * FROM Users;";

        ArrayList<User> resultList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultList.add(
                        new User(
                                result.getInt("id"),
                                result.getString("login"),
                                result.getString("password")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return resultList;
    }

    public boolean deleteAllUsers() {
        String query = "DELETE FROM Users;";

        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public String addAuthorization(int userId)  {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = digest.digest((System.currentTimeMillis() + "").getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);

        String query = "INSERT INTO Authorizations(token, user_id) VALUES('" + encoded + "', " + userId + ");";

        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return encoded;
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
