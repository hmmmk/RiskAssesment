package com.example.servlets;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Utils {

    private static final Gson gson = new Gson();

    public static void printResult(HttpServletResponse httpServletResponse, String message) throws IOException {
        httpServletResponse.getWriter().println(gson.toJson(
                new BaseResponse(message)
        ));
    }
}
