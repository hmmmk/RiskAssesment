package com.example.servlets.logout;

import com.example.database.DatabaseManager;
import com.example.servlets.BaseResponse;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LogoutServlet extends HttpServlet {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final Gson gson = new Gson();
    private Connection connection;

    public LogoutServlet() {
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

        String token = httpServletRequest.getHeader("Authorization");

        if (token != null) {
            if (databaseManager.deleteAuthorization(token)) {
                httpServletResponse.getWriter().println("");
            } else {
                httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("Something went wrong. Try again later.")));
            }
        } else {
            httpServletResponse.getWriter().println(gson.toJson(new BaseResponse("\"Authorization\" header must be defined.")));
        }
    }
}
