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
 
import java.io.IOException;
 
/*
 * This servlet takes care of the voting functionality in the 
 * 1Deed1Day webapp, allows users to upvote and downvote on different ideas
 */
@WebServlet("/vote-input")
public class VoteServlet extends HttpServlet {
    private static final String USER_DEED_ENTITY = "UserDeed";
    private static final String USER_DEED_VOTES = "votes";

    private static final String IDEA_VOTE_VALUE = "voteValue";
    private static final String IDEA_ID = "ideaID";

    private static final String UP_VOTE = "up";
    private static final String DOWN_VOTE = "down";

    private static final String WEBAPP_SURVEY_INPUT = "/surveyInput.html";

    /*
     * Allows a user to vote on an idea, gets the idea from the datastore, and updates its 
     * vote counter accordingly
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query(USER_DEED_ENTITY);
        PreparedQuery userIdeas = datastore.prepare(query);

        String[] voteValues = request.getParameterValues(IDEA_VOTE_VALUE); //all of the submitted votes
        String[] ideaIds = request.getParameterValues(IDEA_ID); //all of the ids of the ideas voted on

        for(int i = 0; i < ideaIds.length; i++) { //traverse all the submitted votes

            String voteValue = voteValues[i]; //get the current vote value "up" or "down"
            Long ideaId = Long.parseLong(ideaIds[i]); //id to access the specific idea

            Entity userIdea = null;

            /*Note: Ideally this would be just one line getting the entity from 
            the datastore using its id, but I couldn't figure out how to do that.
            If anyone knows, please replace this for loop with that*/
            for (Entity entity : userIdeas.asIterable()) {
                if (entity.getKey().getId() == ideaId) {
                    userIdea = entity;
                    break;
                }
            }

            long currentVotes = (long) userIdea.getProperty(USER_DEED_VOTES);
            
            userIdea.setProperty(USER_DEED_VOTES, determineVoteValue(voteValue, currentVotes));
            datastore.put(userIdea);
        }
            

        response.sendRedirect(WEBAPP_SURVEY_INPUT);

    }

    /*Determines if the vote counter should go up, go down, or stay the same*/
    protected long determineVoteValue(String voteValue, long voteNumber) {
        if (voteValue.equalsIgnoreCase(UP_VOTE)) voteNumber++;
        else if (voteValue.equalsIgnoreCase(DOWN_VOTE)) voteNumber--;
        return voteNumber;
    }
}

