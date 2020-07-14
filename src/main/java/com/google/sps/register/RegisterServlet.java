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

package com.google.sps.register;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.register.RegisterResponse;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register-servlet")
public class RegisterServlet extends HttpServlet {

    private static final String REQUEST_PARAMETER_NAME = "name";
    private static final String REQUEST_PARAMETER_EMAIL = "email";

    private static final String MESSAGE_LOGIN = "Login to start doing good deeds!";
    private static final String ERROR_MESSAGE_EXISTING = "A user with this email already exists";
    private static final String ERROR_MESSAGE_DATABASE = "Internal error with database";
    private static final String ERROR_MESSAGE_NAME = "Please enter a name";
    private static final String ERROR_MESSAGE_EMAIL = "Please enter an email";

    private static final String REDIRECT_LOGIN = "/";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get the appropriate response
        RegisterResponse registerResponse = getRegisterResponse(request, response);

        // print as json
        response.setContentType("application/json");
        response.getWriter().println(new Gson().toJson(registerResponse));
    }

    // return the appropriate registerResponse
    RegisterResponse getRegisterResponse(HttpServletRequest request, HttpServletResponse response) {
        RegisterResponse registerResponse;
        
        // get input parameters
        String name = request.getParameter(REQUEST_PARAMETER_NAME);
        String email = request.getParameter(REQUEST_PARAMETER_EMAIL);
        
        if (name.isEmpty()) {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_NAME);
        }
        else if (email.isEmpty()) {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_EMAIL);
        }
        else {
            // initialize datastore
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            registerResponse = getRegisterResponseValidInput(name, email, datastore);
        }

        return registerResponse;
    }

    // return the registerResponse if there's a valid name and a valid email
    RegisterResponse getRegisterResponseValidInput(String name, String email, DatastoreService datastore) {
        RegisterResponse registerResponse;

        // create a filter to find the user
        Key emailKey = KeyFactory.createKey("User", email);
        Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, emailKey);

        // return number of users that have that key
        Query usersQuery = new Query("User").setFilter(keyFilter);
        PreparedQuery users = datastore.prepare(usersQuery);
        int numResults = users.countEntities(FetchOptions.Builder.withDefaults());

        // no user already in datastore
        if (numResults == 0) {
            // create login URL
            UserService userService = UserServiceFactory.getUserService();
            String loginLink = userService.createLoginURL(REDIRECT_LOGIN);

            registerResponse = new RegisterResponse(false, MESSAGE_LOGIN, loginLink);

            addUserToDatastore(name, email, datastore);
        }
        // user already in datastore
        else if (numResults == 1) {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_EXISTING);
        }
        // more than 1 or less than 0 users already in datastore (should never happen)
        else {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_DATABASE);
        }

        return registerResponse;
    }

    // add a new user to the users datastore
    void addUserToDatastore(String name, String email, DatastoreService datastore) {
        // create new entity of type User and with key of email
        Entity userEntity = new Entity("User", email);

        userEntity.setProperty("name", name);
        userEntity.setProperty("email", email);

        // add generated entity to datastore
        datastore.put(userEntity);
    }

    // duplicated getRegisterResponseValidInput but with numResults input to mock in case of error in datastore
    RegisterResponse getRegisterResponseValidInputWithNumResults(String name, String email, DatastoreService datastore, int numResults) {
        RegisterResponse registerResponse;

        // create a filter to find the user
        Key emailKey = KeyFactory.createKey("User", email);
        Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, emailKey);

        // return number of users that have that key
        Query usersQuery = new Query("User").setFilter(keyFilter);
        PreparedQuery users = datastore.prepare(usersQuery);

        // no user already in datastore
        if (numResults == 0) {
            // create login URL
            UserService userService = UserServiceFactory.getUserService();
            String loginLink = userService.createLoginURL(REDIRECT_LOGIN);

            registerResponse = new RegisterResponse(false, MESSAGE_LOGIN, loginLink);

            addUserToDatastore(name, email, datastore);
        }
        // user already in datastore
        else if (numResults == 1) {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_EXISTING);
        }
        // more than 1 or less than 0 users already in datastore (should never happen)
        else {
            registerResponse = new RegisterResponse(true, ERROR_MESSAGE_DATABASE);
        }

        return registerResponse;
    }
}