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
 
import com.google.gson.Gson;
 
import java.io.IOException;
 
/*
 * This servlet takes care of the comment functionality in the 
 * 1Deed1Day webapp, allows users to input comments and also displays
 * all the previous comments
 */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {
    private static final String COMMENTS_PROPERTY = "Comments"; //The name of the property storing the comments
    private static final String JSON_CONTENT_TYPE = "application/json;";
    private static final String COMMENT_FORM_NAME = "newComment"; //The name of the form where users enter comments
    private static final String WEBAPP_HOME = "/index.html";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String TRUE = "true";
    private static final String GOOD_DEED = "GoodDeed";

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private Gson gson = new Gson();
 
 
    /*
     * Grabs any previous comments from the datastore and posts them as a Json
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Entity currentDeed = getCurrentDeed();
        List userComments = (List) currentDeed.getProperty(COMMENTS_PROPERTY);
        List<String> comments = new ArrayList<>();

        comments.addAll(userComments);
        
        response.setContentType(JSON_CONTENT_TYPE);       
        response.getWriter().println(gson.toJson(comments));
    }
 
    /*
     * Allows a user to enter a new comment, grabs the comment, and enters it into the current deed's
     * comments property
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newComment = request.getParameter(COMMENT_FORM_NAME);
        long timestamp = System.currentTimeMillis(); // Used in case we need to sort the comments by time
 
        addComment(newComment);
        response.sendRedirect(WEBAPP_HOME); // Sends users back to the home page after entering a comment
    }

    
    /*
     * Adds a comment to the current deed's comments property
     */
    private void addComment(String newComment) {
        Entity currentDeed = getCurrentDeed();
        List userComments = (List) currentDeed.getProperty(COMMENTS_PROPERTY); 

        if (userComments == null || userComments.isEmpty()) {
            userComments = new ArrayList<>();
        }
        
        userComments.add(newComment);

        currentDeed.setProperty(COMMENTS_PROPERTY, userComments);
        datastore.put(currentDeed);
    }

    /*
     * Gets the current Deed of the day
     * Note: copied over from GoodDeedServlet.fetchDailyDeed() but tweaked a little
     */
    protected Entity getCurrentDeed() {
        Filter propertyFilter = new FilterPredicate(DAILY_DEED, FilterOperator.EQUAL, TRUE);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);
        PreparedQuery results = datastore.prepare(query);

        Entity currentDeed = results.asSingleEntity();
        return currentDeed;
    }
}

