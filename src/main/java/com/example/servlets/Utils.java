package com.example.servlets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Utils {

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static void printResult(HttpServletResponse httpServletResponse, String message) throws IOException {
        httpServletResponse.getWriter().println(gson.toJson(new BaseResponse(message)));
    }

    public static void printCustomResult(HttpServletResponse httpServletResponse, Object body) throws IOException {
        httpServletResponse.getWriter().println(gson.toJson(body));
    }
}
