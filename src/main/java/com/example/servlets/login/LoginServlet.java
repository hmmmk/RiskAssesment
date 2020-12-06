package com.example.servlets.login;

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

public class LoginServlet extends HttpServlet {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final Gson gson = new Gson();
    private Connection connection;

    public LoginServlet() {
        super();

        try {
            connection = databaseManager.connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json");
        httpServletRequest.setCharacterEncoding("UTF-8");

        String login = httpServletRequest.getParameter("login");
        String password = httpServletRequest.getParameter("password");

        if ((login != null && !login.isEmpty()) && (password != null && !password.isEmpty())) {
            List<User> users = databaseManager.getUser(login, password);

            if (users.isEmpty()) {
                httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("There are no users with such login and password")));
            } else {
                String token = databaseManager.addAuthorization(users.get(0).id);

                httpServletResponse.getWriter().println(gson.toJson(new LoginResponse(users.get(0), token)));
            }
        } else {
            httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("Login and password should be not null!")));
        }
    }
}
