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
import com.google.sps.Data.GoodDeed;

// Servlet that access Good Deeds Database

@WebServlet("/goodDeeds")
public class GoodDeedsServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Only selects postes that are marked as not being posted yet
        Filter propertyFilter = new FilterPredicate("Posted Yet", FilterOperator.EQUAL, "false");
        Query query = new Query("GoodDeed").setFilter(propertyFilter);

        PreparedQuery results = datastore.prepare(query);
        List<GoodDeed> GoodDeeds = new ArrayList<>();

        for (Entity deed : results) {
            long  id =  deed.getKey().getId();
            String title = (String) deed.getProperty("Name");
            String description = (String) deed.getProperty("Description");
            boolean seen = (boolean) deed.getProperty("Posted Yet");
            long timestamp = (long) deed.getProperty("Timestamp");

            GoodDeed deedOdbject = new GoodDeed(id, title, description, seen, timestamp);
            GoodDeeds.add(deedOdbject);
        }

        Gson gson = new Gson();

        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(GoodDeeds));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String name = getParameter(request, "Name", "");
        String description = getParameter(request, "Description", "");
        long timestamp = System.currentTimeMillis();

        Entity deedEntity = new Entity("GoodDeed");
        deedEntity.setProperty("Name", name);
        deedEntity.setProperty("Description", description);
        deedEntity.setProperty("Posted Yet", false);
        deedEntity.setProperty("Timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(deedEntity);
      
        response.sendRedirect("/index.html");
    }

    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}