package com.example.servlets.register;

import com.example.database.DatabaseManager;
import com.example.database.models.User;
import com.example.servlets.BaseResponse;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RegisterUserServlet extends HttpServlet {

    private Connection connection;

    private final Gson gson = new Gson();

    public RegisterUserServlet() {
        super();

        try {
            connection =  databaseManager.connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json");
        httpServletRequest.setCharacterEncoding("UTF-8");

        List<User> users = databaseManager.getAllUsers();

        if (users != null) {
            httpServletResponse.getWriter().println(gson.toJson(users));
        } else {
            httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("Something went wrong. Try again later")));
        }
    }


    //TODO: add checks for existing users
    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json");
        httpServletRequest.setCharacterEncoding("UTF-8");

        String login = httpServletRequest.getParameter("login");
        String password = httpServletRequest.getParameter("password");
        boolean isDelete = Boolean.parseBoolean(httpServletRequest.getParameter("isDelete"));

        if ((login != null && !login.isEmpty()) && (password != null && !password.isEmpty())) {
            if (isDelete) {
                databaseManager.deleteAllUsers();
            } else {
                databaseManager.addUser(login, password);
                User user = databaseManager.getUser(login, password).get(0);
                String token = databaseManager.addAuthorization(user.id);

                RegisterResponse response = new RegisterResponse(user, token);

                httpServletResponse.getWriter().println(gson.toJson(response));
            }
        } else {
            httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("Login and password should be not null!")));
        }
    }
}
