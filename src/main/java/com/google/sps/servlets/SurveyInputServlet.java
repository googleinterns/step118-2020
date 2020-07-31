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
import com.google.sps.data.SurveyInput;
 
import java.io.IOException;
 
/*
 * This servlet takes care of the survey input functionality in the 
 * 1Deed1Day webapp, allows users to input ideas and also displays
 * all the previous ideas
 */
@WebServlet("/survey-input")
public class SurveyInputServlet extends HttpServlet {
    private static final String FORM_IDEA_TITLE = "ideaTitle";
    private static final String FORM_IDEA_DESCRIPTION = "ideaDescription";

    private static final String USER_DEED_ENTITY = "UserDeed";
    private static final String USER_DEED_TITLE = "title";
    private static final String USER_DEED_DESCRIPTION = "description";
    private static final String USER_DEED_VOTES = "votes";
    private static final String USER_DEED_TIMESTAMP = "timestamp";

    private static final String WEBAPP_SURVEY_INPUT = "/surveyInput.html";
    private static final String JSON_CONTENT_TYPE = "application/json;";
    private static final long ZERO = 0;

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private Gson gson = new Gson();
 
 
    /*
     * Grabs any previous ideas from the datastore and posts them as a Json
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Query query = new Query(USER_DEED_ENTITY).addSort(USER_DEED_VOTES, SortDirection.DESCENDING);
      
        PreparedQuery userIdeas = datastore.prepare(query);

        List<SurveyInput> ideasList = new ArrayList<>();
        for (Entity entity : userIdeas.asIterable()) {
            
            long id = entity.getKey().getId();
            String newIdea = (String) entity.getProperty(USER_DEED_TITLE);
            String newIdeaDescription = (String) entity.getProperty(USER_DEED_DESCRIPTION);
            long newIdeaVotes = (long) entity.getProperty(USER_DEED_VOTES);
            long timestamp = (long) entity.getProperty(USER_DEED_TIMESTAMP);

            SurveyInput ideaObject = new SurveyInput(id, newIdea, newIdeaDescription, newIdeaVotes, timestamp);
            ideasList.add(ideaObject);
        }

        response.setContentType(JSON_CONTENT_TYPE);
        response.getWriter().println(gson.toJson(ideasList));
    }
 
    /*
     * Allows a user to enter a new idea, makes a new entity for that idea, and puts it in the 
     * datastore
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newIdeaTitle = request.getParameter(FORM_IDEA_TITLE);
        String newIdeaDescription = request.getParameter(FORM_IDEA_DESCRIPTION);
        long timestamp = System.currentTimeMillis(); 

        if (isValid(newIdeaTitle)) {
            if (isValid(newIdeaDescription)) {
                Entity ideaEntity = new Entity(USER_DEED_ENTITY);
                ideaEntity.setProperty(USER_DEED_TITLE, newIdeaTitle);
                ideaEntity.setProperty(USER_DEED_DESCRIPTION, newIdeaDescription);
                ideaEntity.setProperty(USER_DEED_VOTES, ZERO);
                ideaEntity.setProperty(USER_DEED_TIMESTAMP, timestamp);

                datastore.put(ideaEntity);
            }
      }
        response.sendRedirect(WEBAPP_SURVEY_INPUT); // Sends users back to the survey input page
    }

    /**
     * Checks if the user's string is not empty
     */
    public boolean isValid(String text) {
      return text != null && !text.isEmpty() && !text.isBlank();
   }
}

