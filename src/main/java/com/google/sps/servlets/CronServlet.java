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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.GoodDeed;


@WebServlet("/cronjob")
public class CronServlet extends HttpServlet {
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String TIME_STAMP = "Timestamp";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String GOOD_DEED = "GoodDeed";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final int MINIMUM_QUERY_LENGTH = 1;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Clears the Daily Deed Property for all elements
        resetDailyDeed(datastore);

        // Pulls all Database Entries
        ArrayList<GoodDeed> GoodDeeds = PullDeedsFromDatastore(datastore);

        // Filters out Posted Deeds
        List<GoodDeed> GoodDeeds_cleaned = cleanDeeds(GoodDeeds);

        if (GoodDeeds_cleaned.size() < MINIMUM_QUERY_LENGTH) {
            System.out.println("Query is below minimum size.");
            
            // Resets Posted Yet property of all posted deeds
            resetPostedYet(datastore);
            GoodDeeds_cleaned = GoodDeeds;
        }

        // Selects a random Good Deed
        selectDailyDeed(GoodDeeds_cleaned);

        response.sendRedirect("/index.html");

    }

    public void resetDailyDeed(DatastoreService datastore) {
        Query query = new Query(GOOD_DEED);
 
        PreparedQuery results = datastore.prepare(query);
 
        for (Entity deed : results.asIterable()) {
            deed.setProperty(DAILY_DEED, FALSE);
            datastore.put(deed);
        }
    }

    public void resetPostedYet(DatastoreService datastore) {

        Query query = new Query(GOOD_DEED);
        PreparedQuery results = datastore.prepare(query);
 
        for (Entity deed : results.asIterable()) {
            deed.setProperty(POSTED_YET, FALSE);
            datastore.put(deed);
        }
    }

    public ArrayList<GoodDeed> PullDeedsFromDatastore(DatastoreService datastore) {
 
        // Only selects postes that are marked as not being posted yet
        Query query = new Query(GOOD_DEED);
 
        PreparedQuery results = datastore.prepare(query);
        ArrayList<GoodDeed> GoodDeeds = new ArrayList<>();
 
        for (Entity deed : results.asIterable()) {
            Key key = deed.getKey();
            long  id =  deed.getKey().getId();
            String title = (String) deed.getProperty(NAME);
            String description = (String) deed.getProperty(DESCRIPTION);
            String posted_yet_string = (String) deed.getProperty(POSTED_YET);
            boolean posted_yet_bool = Boolean.parseBoolean(posted_yet_string);
            long timestamp = (long) deed.getProperty(TIME_STAMP);
 
            GoodDeed deedObject = new GoodDeed(key, id, title, description, posted_yet_bool, timestamp);
            GoodDeeds.add(deedObject);
        }

        return GoodDeeds;
    }

    public void selectDailyDeed(List<GoodDeed> GoodDeeds) {
        Random rand = new Random();
        GoodDeed daily_deed = GoodDeeds.get(rand.nextInt(GoodDeeds.size()));

        // Marks Deed as Posted and Daily Deed
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity deedEntity = datastore.get(daily_deed.getKey());
            deedEntity.setProperty(POSTED_YET, TRUE);
            deedEntity.setProperty(DAILY_DEED, TRUE);
            datastore.put(deedEntity);
        }
        catch (EntityNotFoundException e) {
            System.out.println("Key not found");
        }
    }

    public List<GoodDeed> cleanDeeds(List<GoodDeed> deeds) {
        List<GoodDeed> cleaned_deeds = new ArrayList<>();
        for (GoodDeed deed : deeds) {
            if (!deed.getPosted()) {
                cleaned_deeds.add(deed);
            }
        }

        return cleaned_deeds;
    }


}