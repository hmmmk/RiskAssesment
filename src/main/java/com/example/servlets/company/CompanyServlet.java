package com.example.servlets.company;

import com.example.database.DatabaseManager;
import com.example.database.models.Company;
import com.example.database.models.User;
import com.example.servlets.Utils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CompanyServlet extends HttpServlet {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson;
    private Connection connection;

    public CompanyServlet() {
        super();

        try {
            connection = databaseManager.connect();

            gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
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

        Company company;

        if (requestBody.getId() != 0) {
            company = databaseManager.editCompany(new Company(requestBody.getId(), requestBody.getOwnWorkingCapital(), requestBody.getOwnCapital(),
                    requestBody.getAssets(), requestBody.getStObligations(), requestBody.getStNetProfit(),
                    requestBody.getAssets(), requestBody.getObligation(), requestBody.getRevenue(), requestBody.getName(),
                    user.id));

        } else {
            company = databaseManager.addCompany(new Company(0, requestBody.getOwnWorkingCapital(), requestBody.getOwnCapital(),
                    requestBody.getAssets(), requestBody.getStObligations(), requestBody.getStNetProfit(),
                    requestBody.getAssets(), requestBody.getObligation(), requestBody.getRevenue(), requestBody.getName(),
                    user.id));
        }

        if (company != null) {
            Utils.printCustomResult(httpServletResponse, company);
        } else {
            Utils.printResult(httpServletResponse, "Something went wrong try again later");
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
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

        int companyId = Integer.parseInt(httpServletRequest.getParameter("company_id"));

        List<Company> companies;

        if (companyId == 0) {
            companies = databaseManager.getCompany(token);
        } else {
            companies = databaseManager.getCompany(companyId);
        }

        if (companies != null && !companies.isEmpty()) {
            Utils.printCustomResult(httpServletResponse, companies);
        } else {
            Utils.printResult(httpServletResponse, "Such company is not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
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

        int companyId = Integer.parseInt(httpServletRequest.getParameter("company_id"));

        if (companyId == 0) {
            Utils.printResult(httpServletResponse, "\"company_id\" must be present.");
            return;
        }

        if (databaseManager.deleteCompany(companyId)) {
            Utils.printResult(httpServletResponse, "Company has been deleted");
        } else {
            Utils.printResult(httpServletResponse, "Such company is not found.");
        }
    }
}
