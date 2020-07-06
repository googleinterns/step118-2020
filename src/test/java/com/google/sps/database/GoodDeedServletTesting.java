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
package com.google.sps.database;

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

import com.google.sps.data.GoodDeed;
import com.google.sps.data.servlets.GoodDeedsServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.google.gson.Gson;

@RunWith(JUnit4.class)
public class GoodDeedServletTesting{
    // Deed Properties
    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String TIMESTAMP = "Timestamp";

    // Property Inputs
    private static final String TITLE = "Deed Name";
    private static final String DESCRIPTION_INPUT = "Deed Description";
    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    private static final long TIMESTAMP_INPUT = 67890;
    private static final String TITLE_2 = "Deed Name #2";
    private static final String DESCRIPTION_INPUT_2 = "Deed Description #2";
    private static final boolean TRUE = true;
    private static final boolean FALSE = false;
    private static final String DEFAULT_VALUE = "";

    private GoodDeedsServlet deedServlet;
    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        deedServlet = new GoodDeedsServlet();
        helper.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Test
    public void testFetch() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, TRUE_STRING);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        datastore.put(testEntity);

        GoodDeed expected = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, TRUE, TIMESTAMP_INPUT);
        
        GoodDeed actual = deedServlet.FetchDailyDeed();

        Assert.assertEquals(expected.getKey(), actual.getKey());
        
    }

    @Test
    public void testParameter() {
        // Sets getParameter to return null
        Mockito.doReturn(null).when(request).getParameter(NAME);

        String expected_1 = deedServlet.getParameter(request, NAME, DEFAULT_VALUE);
        Assert.assertEquals(expected_1, "");

        // Sets getParameter to return a valid string
        Mockito.doReturn(TITLE).when(request).getParameter(NAME);

        String expected_2 = deedServlet.getParameter(request, NAME, DEFAULT_VALUE);
        Assert.assertEquals(expected_2, TITLE);
    }

    @Test
    public void testPost() throws IOException {
        Mockito.doReturn(TITLE).when(request).getParameter(NAME);
        Mockito.doReturn(DESCRIPTION_INPUT).when(request).getParameter(DESCRIPTION);

        // Creates and puts deed entity in datastore
        deedServlet.doPost(request, response);

        // Fetches deeds from datastore based on title
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query(GOOD_DEED);

        Entity deedEntity = ds.prepare(query).asSingleEntity();

        String actual = (String) deedEntity.getProperty(NAME);
        Assert.assertEquals(TITLE, actual);

    }

    @Test
    public void testGet() throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, TRUE_STRING);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        datastore.put(testEntity);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.doReturn(pw).when(response).getWriter();

        Gson gson = new Gson();

        GoodDeed expected = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, TRUE, TIMESTAMP_INPUT);
        
        deedServlet.doGet(request, response);
        String actual = sw.getBuffer().toString().trim();
        Assert.assertEquals(actual, gson.toJson(expected));
    }

}