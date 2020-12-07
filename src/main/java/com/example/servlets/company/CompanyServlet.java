package com.example.servlets.company;

import com.example.database.DatabaseManager;
import com.example.database.models.Company;
import com.example.database.models.User;
import com.example.servlets.Utils;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CompanyServlet extends HttpServlet {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final Gson gson = new Gson();
    private Connection connection;

    public CompanyServlet() {
        super();

        try {
            connection = databaseManager.connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("application/json");
        httpServletRequest.setCharacterEncoding("UTF-8");

        String token = httpServletRequest.getHeader("Authorization");

        if (token == null) {
            Utils.printResult(httpServletResponse, "\"Authorization\" header must be present.");
            return;
        }

        User user = databaseManager.getUserByToken(token);

        if (user == null) {
            Utils.printResult(httpServletResponse, "\"Authorization\" has been expired.");
            return;
        }

        CreateCompanyRequest requestBody = gson.fromJson(httpServletRequest.getReader(), CreateCompanyRequest.class);

        if (requestBody == null) {
            Utils.printResult(httpServletResponse, "Body must be present");
            return;
        }

        if (requestBody.getName() == null) {
            Utils.printResult(httpServletResponse, "Company name must be present");
            return;
        }

        if (databaseManager.addCompany(new Company(0, requestBody.getOwnWorkingCapital(), requestBody.getOwnCapital(),
                requestBody.getAssets(), requestBody.getStObligations(), requestBody.getStNetProfit(),
                requestBody.getAssets(), requestBody.getObligation(), requestBody.getRevenue(), requestBody.getName(),
                user.id))) {
            Utils.printResult(httpServletResponse, "Everything is fine!");
        } else {
            Utils.printResult(httpServletResponse, "Something went wrong try again later");
        }
    }
}
