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

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import com.google.sps.testing.GoodDeed;
import com.google.sps.testing.GoodDeedsServlet;
import com.google.sps.data.Comment;
import com.google.sps.servlets.CommentServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.google.gson.Gson;

import java.util.List;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class CommentServletTest {
    //Properties and Entities 
    private static final String COMMENTS = "Comments";
    private static final String COMMENT_FORM_NAME = "newComment";
    private static final String DEED_ENTITY = "GoodDeed";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String DESCRIPTION = "Description";
    private static final String LINK = "Link";
    private static final String NAME = "Name";
    
    //Inputs for the properties and entities
    private static final List COMMENTS_LIST = new ArrayList<>();
    private static final String DESCRIPTION_INPUT = "Deed Description";
    private static final String NEW_COMMENT = "new comment";
    private static final long TIMESTAMP_INPUT = 67890;
    private static final String TITLE = "Deed Name";
    private static final boolean TRUE = true;
    private static final String TRUE_STRING = "true";

    //Mock HTTPServlet Request and Response
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;

    private CommentServlet commentServlet;
    private GoodDeedsServlet deedServlet;
    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        commentServlet = new CommentServlet();
        deedServlet = new GoodDeedsServlet();
        helper.setUp();
        MockitoAnnotations.initMocks(this);
        COMMENTS_LIST.add(NEW_COMMENT);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    /*Tests if the post method adds a new comment to the deed of the day*/
    @Test
    public void testPost() throws IOException {
        Mockito.doReturn(NEW_COMMENT).when(request).getParameter(COMMENT_FORM_NAME);

        /*Since we post comments to a good deed entity on the datastore, we first must
          create one of these entities*/
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity testEntity = new Entity(DEED_ENTITY);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        datastore.put(testEntity);

        /*Adds comment 'new comment' to the current deed*/
        commentServlet.doPost(request, response);

        // Fetches deeds from datastore based on title
        Query query = new Query(DEED_ENTITY);
        Entity deedEntity = datastore.prepare(query).asSingleEntity();

        List comments = (List) deedEntity.getProperty(COMMENTS); //Gets the list of comments
        String actual = (String) comments.get(0); //Gets the latest comment
        Assert.assertEquals(NEW_COMMENT, actual);
    }

    /*Tests if the get method returns a list containing the comments*/
    @Test
    public void testGet() throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(DEED_ENTITY);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        testEntity.setProperty(COMMENTS, COMMENTS_LIST);
        datastore.put(testEntity);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.doReturn(pw).when(response).getWriter();

        Gson gson = new Gson();

        commentServlet.doGet(request, response);
        String actual = sw.getBuffer().toString().trim();
        Assert.assertEquals(actual, gson.toJson(COMMENTS_LIST));
    }

    /*Tests if the getCurrentDeed() method gets the current deed*/
    @Test
    public void testGetCurrentDeed() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(DEED_ENTITY);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        datastore.put(testEntity);

        GoodDeed expected = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, TRUE, TIMESTAMP_INPUT, LINK);
        
        Entity actual = commentServlet.getCurrentDeed();

        Assert.assertEquals(expected.getTitle(), actual.getProperty(NAME));
        
    }

}