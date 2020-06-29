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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
    private static final String TRUE = "true";
    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String TIME_STAMP = "Timestamp";
    private static final String DEFAULT_VALUE = "";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String REDIRECT_HOMEPAGE = "/index.html";
    private static final int MINIMUM_QUERY_LENGTH = 1;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        //Pulls all datastore entries
        List<GoodDeed> GoodDeeds = PullDeedsFromDatastore();

        //List of deeds that haven't been posted
        List<GoodDeed> GoodDeeds_cleaned = cleanDeeds(GoodDeeds);

        if (GoodDeeds_cleaned.size() < MINIMUM_QUERY_LENGTH) {
            System.out.println("Query is below minimum size.");
            
            // Resets Posted Yet property of all posted deeds
            resetDatabase();
            GoodDeeds_cleaned = GoodDeeds;
            System.out.println(GoodDeeds_cleaned);
        }

        GoodDeed random_deed = select_random_deed(GoodDeeds_cleaned);
 
        Gson gson = new Gson();
 
        response.setContentType(CONTENT_TYPE_JSON);
        response.getWriter().println(gson.toJson(random_deed));

        // Marks the deed as posted
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity deedEntity = datastore.get(random_deed.getKey());
            deedEntity.setProperty(POSTED_YET, TRUE);
            datastore.put(deedEntity);
        }
        catch (EntityNotFoundException e) {
            System.out.println("Key not found");
        }
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

    // Randomly Selects a Good Deed Object
    private GoodDeed select_random_deed(List<GoodDeed> GoodDeeds) {
        Random rand = new Random();
        return GoodDeeds.get(rand.nextInt(GoodDeeds.size()));
    }

    private void resetDatabase() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter propertyFilter = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, TRUE);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);
 
        PreparedQuery results = datastore.prepare(query);
 
        for (Entity deed : results.asIterable()) {
            deed.setProperty(POSTED_YET, "false");
            datastore.put(deed);
        }
    }

    private List<GoodDeed> PullDeedsFromDatastore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
        // Only selects postes that are marked as not being posted yet
        Query query = new Query(GOOD_DEED);
        //System.out.println(query);
 
        PreparedQuery results = datastore.prepare(query);
        List<GoodDeed> GoodDeeds = new ArrayList<>();
 
        for (Entity deed : results.asIterable()) {
            Key key = deed.getKey();
            long  id =  deed.getKey().getId();
            String title = (String) deed.getProperty(NAME);
            String description = (String) deed.getProperty(DESCRIPTION);
            String posted_yet_string = (String) deed.getProperty(POSTED_YET);
            boolean posted_yet_bool = Boolean.parseBoolean(posted_yet_string);
            long timestamp = (long) deed.getProperty(TIME_STAMP);
 
            GoodDeed deedOdbject = new GoodDeed(key, id, title, description, posted_yet_bool, timestamp);
            GoodDeeds.add(deedOdbject);
        }

        return GoodDeeds;
    }

    private List<GoodDeed> cleanDeeds(List<GoodDeed> deeds) {
        List<GoodDeed> cleaned_deeds = new ArrayList<>();
        
        for (GoodDeed deed : deeds) {
            if (!deed.getPosted()) {
                cleaned_deeds.add(deed);
            }
        }

        //System.out.println("Clean:" + cleaned_deeds);
        return cleaned_deeds;
    }
    
}