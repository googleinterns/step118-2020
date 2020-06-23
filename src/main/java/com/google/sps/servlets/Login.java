package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Authentication;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


@WebServlet("/login")
public class Login extends HttpServlet {

    private static final String REDIRECT_LOGIN = "/";
    private static final String REDIRECT_LOGOUT = "/";
    
    // Print json of whether user is logged in and the respective login/logout link
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Authentication auth;
        
        response.setContentType("application/json");

        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            String logoutUrl = userService.createLogoutURL(REDIRECT_LOGOUT);
            String userEmail = userService.getCurrentUser().getEmail();

            auth = new Authentication(true, logoutUrl);

            Gson gson = new Gson();
            String json = gson.toJson(auth);

            response.getWriter().println(json);
        }
        else {
            String loginUrl = userService.createLoginURL(REDIRECT_LOGIN);

            auth = new Authentication(false, loginUrl);

            Gson gson = new Gson();
            String json = gson.toJson(auth);

            response.getWriter().println(json);
        }
    }
}