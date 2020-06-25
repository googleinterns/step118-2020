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

package com.google.sps.servlet;
 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.GoodDeed;
 
// Servlet that access Good Deeds Database
 
@WebServlet("/goodDeeds")
public class GoodDeedsServlet extends HttpServlet {
    private static final String FALSE = "false";
    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String TIME_STAMP = "Timestamp";
    private static final String DEFAULT_VALUE = "";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String REDIRECT_HOMEPAGE = "/index.html";
    
    /*
    Testing:
    JS test send a mock do post, and read it back as, verify that they're the same

    Create a mock httpservlet object
    send it through the doPost
    send a doGet on the same object

    one test

    checks that it writes to the DB, and retrives it properly
    */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
        // Only selects postes that are marked as not being posted yet
        Filter propertyFilter = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, FALSE);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);
        System.out.println(query);
 
        PreparedQuery results = datastore.prepare(query);
        List<GoodDeed> GoodDeeds = new ArrayList<>();
 
        for (Entity deed : results.asIterable()) {
            long  id =  deed.getKey().getId();
            String title = (String) deed.getProperty(NAME);
            String description = (String) deed.getProperty(DESCRIPTION);
            String posted_yet_string = (String) deed.getProperty(POSTED_YET);
            boolean posted_yet_bool = Boolean.parseBoolean(posted_yet_string);
            long timestamp = (long) deed.getProperty(TIME_STAMP);
 
            GoodDeed deedOdbject = new GoodDeed(id, title, description, posted_yet_bool, timestamp);
            GoodDeeds.add(deedOdbject);
        }
 
        Gson gson = new Gson();
 
        response.setContentType(CONTENT_TYPE_JSON);
        response.getWriter().println(gson.toJson(GoodDeeds));
    }
 
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String name = getParameter(request, NAME, DEFAULT_VALUE);
        String description = getParameter(request, DESCRIPTION, DEFAULT_VALUE);
        long timestamp = System.currentTimeMillis();
 
        Entity deedEntity = new Entity(GOOD_DEED);
        deedEntity.setProperty(NAME, name);
        deedEntity.setProperty(DESCRIPTION, description);
        deedEntity.setProperty(POSTED_YET, FALSE);
        deedEntity.setProperty(TIME_STAMP, timestamp);
 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(deedEntity);
      
        response.sendRedirect(REDIRECT_HOMEPAGE);
    }
 
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}