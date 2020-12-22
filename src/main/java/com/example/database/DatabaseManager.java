package com.example.database;

import com.example.database.models.Company;
import com.example.database.models.User;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.naming.spi.DirStateFactory;
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
    private Connection connection;

    private ScriptRunner runner;

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
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    //TODO: add checks for existing users
    public List<User> getUser(String login, String password) {
        String query = "SELECT * FROM Users WHERE login='" + login + "' AND password='" + password + "';";

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

    public User getUserByToken(String token) {
        String query = "SELECT * FROM Users WHERE Users.id = " +
                "(SELECT user_id FROM Authorizations WHERE token = '" + token + "');";

        User user = null;

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                user = new User(
                        result.getInt("id"),
                        result.getString("login"),
                        result.getString("password"));

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return user;
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
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public String addAuthorization(int userId) {
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
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return encoded;
    }

    public boolean deleteAuthorization(String token) {
        String query = "DELETE FROM Authorizations WHERE token='" + token + "';";

        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
            connection.commit();

            if (result == 0)
                return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public Company editCompany(Company company) {
        String query = "UPDATE Companies SET " +
                "own_working_capital = ?, " +
                "own_capital = ?, " +
                "st_assets = ?, " +
                "st_obligations = ?, " +
                "net_profit = ?, " +
                "assets = ?, " +
                "obligations = ?, " +
                "revenue = ?, " +
                "name = ? " +
                "WHERE id = " + company.getId() + ";";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setFloat(1, company.getOwnWorkingCapital());
            statement.setFloat(2, company.getOwnCapital());
            statement.setFloat(3, company.getStAssets());
            statement.setFloat(4, company.getStObligations());
            statement.setFloat(5, company.getStNetProfit());
            statement.setFloat(6, company.getAssets());
            statement.setFloat(7, company.getObligation());
            statement.setFloat(8, company.getRevenue());
            statement.setString(9, company.getName());

            statement.executeUpdate();
            connection.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return company;
    }

    public Company addCompany(Company company) {
        String query = "INSERT INTO Companies(own_working_capital, own_capital, st_assets, st_obligations, " +
                "net_profit, assets, obligations, revenue, name, user_id) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setFloat(1, company.getOwnWorkingCapital());
            statement.setFloat(2, company.getOwnCapital());
            statement.setFloat(3, company.getStAssets());
            statement.setFloat(4, company.getStObligations());
            statement.setFloat(5, company.getStNetProfit());
            statement.setFloat(6, company.getAssets());
            statement.setFloat(7, company.getObligation());
            statement.setFloat(8, company.getRevenue());
            statement.setString(9, company.getName());
            statement.setInt(10, company.getUserId());

            statement.executeUpdate();
            connection.commit();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    company.setId(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return company;
    }

    public List<Company> getCompany(int id) {
        String query = "SELECT * FROM Companies WHERE id=" + id + ";";

        ArrayList<Company> resultList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultList.add(
                        new Company(
                                result.getInt("id"),
                                result.getFloat("own_working_capital"),
                                result.getFloat("own_capital"),
                                result.getFloat("st_assets"),
                                result.getFloat("st_obligations"),
                                result.getFloat("net_profit"),
                                result.getFloat("assets"),
                                result.getFloat("obligations"),
                                result.getFloat("revenue"),
                                result.getString("name"),
                                result.getInt("user_id")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return resultList;
    }

    public List<Company> getCompany(String token) {
        String query = "SELECT * FROM Companies WHERE user_id=(SELECT user_id from Authorizations WHERE token='" + token + "')";

        ArrayList<Company> resultList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultList.add(
                        new Company(
                                result.getInt("id"),
                                result.getFloat("own_working_capital"),
                                result.getFloat("own_capital"),
                                result.getFloat("st_assets"),
                                result.getFloat("st_obligations"),
                                result.getFloat("net_profit"),
                                result.getFloat("assets"),
                                result.getFloat("obligations"),
                                result.getFloat("revenue"),
                                result.getString("name"),
                                result.getInt("user_id")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return resultList;
    }

    public boolean deleteCompany(int id) {
        String query = "DELETE FROM Companies WHERE id=" + id + ";";

        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
            connection.commit();

            if (result == 0)
                return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
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
