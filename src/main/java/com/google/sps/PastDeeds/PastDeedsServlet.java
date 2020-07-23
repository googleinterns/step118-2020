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

package com.google.sps.testing;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.google.sps.testing.GoodDeed;

@WebServlet("/ghost-of-deeds-past")
public class PastDeedsServlet extends HttpServlet {
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String LINK = "Link";
    private static final String TIME_STAMP = "Timestamp";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fetch Posted Deeds
        List<GoodDeed> deeds = fetchPostedDeeds();

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(deeds));

    }

    List<GoodDeed> fetchPostedDeeds() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Filter posted_yet = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, TRUE);
        Query query = new Query(GOOD_DEED).setFilter(posted_yet);

        PreparedQuery results = datastore.prepare(query);

        List<GoodDeed> deeds = new ArrayList<>();

        for (Entity deed : results.asIterable()) {
            Key key = deed.getKey();
            long  id =  deed.getKey().getId();
            String title = (String) deed.getProperty(NAME);
            String description = (String) deed.getProperty(DESCRIPTION);
            String posted_yet_string = (String) deed.getProperty(POSTED_YET);
            boolean posted_yet_bool = Boolean.parseBoolean(posted_yet_string);
            long timestamp = (long) deed.getProperty(TIME_STAMP);
            String link = (String) deed.getProperty(LINK);

            GoodDeed deedObject = new GoodDeed(key, id, title, description, posted_yet_bool, timestamp, link);
            deeds.add(deedObject);
        }

        return deeds;
    }
}