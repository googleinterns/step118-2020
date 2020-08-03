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

package com.google.sps.profile;

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
import com.google.sps.profile.Profile;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/profile-servlet")
public class ProfileServlet extends HttpServlet {
    private static final String EMPTY_STRING = "";

    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_FNAME = "fname";
    private static final String PARAMETER_LNAME = "lname";
    private static final String PARAMETER_LOCATION = "location";
    private static final String PARAMETER_BIO = "bio";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // initialize the database
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        doGetWithDatastore(request, response, datastore);
    }

    public void doGetWithDatastore(HttpServletRequest request, HttpServletResponse response, DatastoreService datastore) throws IOException {
        String email = request.getParameter(PARAMETER_EMAIL);
        
        // create a filter to find the user
        Key emailKey = KeyFactory.createKey("Profile", email);
        Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, emailKey);

        // return number of users that have that key
        Query profileQuery = new Query("Profile").setFilter(keyFilter);
        PreparedQuery profiles = datastore.prepare(profileQuery);
        int numResults = profiles.countEntities(FetchOptions.Builder.withDefaults());

        // no user already in datastore
        if (numResults == 0) {
            // add the current user to the datastore
            Entity newProfileEntity = new Entity("Profile", email);
            newProfileEntity.setProperty(PARAMETER_FNAME, EMPTY_STRING);
            newProfileEntity.setProperty(PARAMETER_LNAME, EMPTY_STRING);
            newProfileEntity.setProperty(PARAMETER_EMAIL, email);
            newProfileEntity.setProperty(PARAMETER_LOCATION, EMPTY_STRING);
            newProfileEntity.setProperty(PARAMETER_BIO, EMPTY_STRING);

            datastore.put(newProfileEntity);
        }

        // pulls the current user from datastore
        try {
            Entity currProfileEntity = datastore.get(emailKey);

            Profile currProfile = getEntityAsProfile(currProfileEntity);

            response.setContentType("application/json");
            response.getWriter().print(new Gson().toJson(currProfile));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // initialize the database
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        doPostWithDatastore(request, response, datastore);
    }

    public void doPostWithDatastore(HttpServletRequest request, HttpServletResponse response, DatastoreService datastore) throws IOException {
        String fname = request.getParameter(PARAMETER_FNAME);
        String lname = request.getParameter(PARAMETER_LNAME);
        String email = request.getParameter(PARAMETER_EMAIL);
        String location = request.getParameter(PARAMETER_LOCATION);
        String bio = request.getParameter(PARAMETER_BIO);
        
        // create profile with email as key
        Entity currProfileEntity = new Entity("Profile", email);
        currProfileEntity.setProperty(PARAMETER_FNAME, fname);
        currProfileEntity.setProperty(PARAMETER_LNAME, lname);
        currProfileEntity.setProperty(PARAMETER_EMAIL, email);
        currProfileEntity.setProperty(PARAMETER_LOCATION, location);
        currProfileEntity.setProperty(PARAMETER_BIO, bio);

        // adds new profile or updates if profile with same email already exists
        datastore.put(currProfileEntity);

        Profile currProfile = getEntityAsProfile(currProfileEntity);

        response.setContentType("application/json");
        response.getWriter().print(new Gson().toJson(currProfile));
    }

    // setting visibility as package-private for testing purposes
    Profile getEntityAsProfile(Entity currProfileEntity) {
        String fname = (String) currProfileEntity.getProperty(PARAMETER_FNAME);
        String lname = (String) currProfileEntity.getProperty(PARAMETER_LNAME);
        String email = (String) currProfileEntity.getProperty(PARAMETER_EMAIL);
        String location = (String) currProfileEntity.getProperty(PARAMETER_LOCATION);
        String bio = (String) currProfileEntity.getProperty(PARAMETER_BIO);

        Profile currProfile = new Profile(fname, lname, email, location, bio);

        return currProfile;
    }
}