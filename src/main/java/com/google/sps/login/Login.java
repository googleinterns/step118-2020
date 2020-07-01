// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.login;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.sps.login.Authentication;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class Login extends HttpServlet {

	// Redirects user to default page
    private static final String REDIRECT_LOGIN = "/";
    private static final String REDIRECT_LOGOUT = "/";
    
    // Passes in the actual userService to getAuthentication
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        
        // Get the login status and link
        Authentication auth = getAuthentication(userService);

        Gson gson = new Gson();
        String json = gson.toJson(auth);

        response.getWriter().println(json);

    }

    // Return Authentication class that includes login status and login/logout link
    public Authentication getAuthentication(UserService userService) {
        Authentication auth;

        if (userService.isUserLoggedIn()) {
            String logoutUrl = userService.createLogoutURL(REDIRECT_LOGOUT);

            auth = new Authentication(true, logoutUrl);
        }
        else {
            String loginUrl = userService.createLoginURL(REDIRECT_LOGIN);

            auth = new Authentication(false, loginUrl);
        }

        return auth;
    }
}