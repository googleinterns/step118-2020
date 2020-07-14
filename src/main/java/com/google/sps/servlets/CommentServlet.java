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
 
package com.google.sps.servlets;
 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
 
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
 
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
 
import com.google.gson.Gson;
 
import java.io.IOException;
 
/*
 * This servlet takes care of the comment functionality in the 
 * 1Deed1Day webapp, allows users to input comments and also displays
 * all the previous comments
 */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {
 
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private Gson gson = new Gson();
 
 
    /*
     * Grabs any previous comments from the datastore and posts them as a Json
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Entity currentDeed = getCurrentDeed();
        Collection userComments = (Collection) currentDeed.getProperty("Comments");
        Iterator<String> commentsIterator = userComments.iterator();
  
        List<String> comments = new ArrayList<>();

        while (commentsIterator.hasNext()) {
            comments.add(commentsIterator.next());
        }
        
        response.setContentType("application/json;");       
        response.getWriter().println(gson.toJson(comments));
    }
 
    /*
     * Allows a user to enter a new comment, grabs the comment, and enters it into the current deed's
     * comments property
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newComment = request.getParameter("newComment");
        long timestamp = System.currentTimeMillis(); // Used in case we need to sort the comments by time
 
        addComment(newComment);
        response.sendRedirect("/index.html"); // Sends users back to the home page after entering a comment
    }

    
    /*
     * Adds a comment to the current deed's comments property
     */
    private void addComment(String newComment) {
        Entity currentDeed = getCurrentDeed();
        Collection userComments = (Collection) currentDeed.getProperty("Comments");

        if (userComments == null || userComments.isEmpty()) {
            userComments = new ArrayList<>();
            userComments.add(newComment);
        } else {
            userComments.add(newComment);
        }

        currentDeed.setProperty("Comments", userComments);
        datastore.put(currentDeed);
    }

    /*
     * Gets the current Deed of the day
     * Note: copied over from GoodDeedServlet.fetchDailyDeed() but tweaked a little
     */
    private Entity getCurrentDeed() {
        Filter propertyFilter = new FilterPredicate("Daily Deed", FilterOperator.EQUAL, "true");
        Query query = new Query("GoodDeed").setFilter(propertyFilter);
 
        PreparedQuery results = datastore.prepare(query);

        Entity currentDeed = results.asSingleEntity();
        return currentDeed;
    }
}

