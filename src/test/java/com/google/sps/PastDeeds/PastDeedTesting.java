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

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.google.gson.Gson;

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
import com.google.sps.testing.PastDeedsServlet;

@RunWith(JUnit4.class)
public class PastDeedTesting {
    // Deed Properties
    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String TIMESTAMP = "Timestamp";
    private static final String LINK = "Link";

    // Property Inputs
    private static final String TITLE = "Deed Name";
    private static final String DESCRIPTION_INPUT = "Deed Description";
    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    private static final long TIMESTAMP_INPUT = 67890;
    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    private PastDeedsServlet pdServlet;

    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper( new LocalDatastoreServiceTestConfig());
    
    @Before
    public void setUp() {
        helper.setUp();
        pdServlet = new PastDeedsServlet();
    }

    @Test
    public void testFetchPosted() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity postedEntity = new Entity(GOOD_DEED);
        postedEntity.setProperty(NAME, TITLE);
        postedEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        postedEntity.setProperty(POSTED_YET, TRUE_STRING);
        postedEntity.setProperty(DAILY_DEED, FALSE_STRING);
        postedEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        postedEntity.setProperty(LINK, LINK);
        ds.put(postedEntity);

        Entity not_Posted_Entity = new Entity(GOOD_DEED);
        not_Posted_Entity.setProperty(NAME, TITLE);
        not_Posted_Entity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        not_Posted_Entity.setProperty(POSTED_YET, FALSE_STRING);
        not_Posted_Entity.setProperty(DAILY_DEED, FALSE_STRING);
        not_Posted_Entity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        not_Posted_Entity.setProperty(LINK, LINK);
        ds.put(not_Posted_Entity);

        GoodDeed posted_deed = 
            new GoodDeed(postedEntity.getKey(), postedEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, TRUE, TIMESTAMP_INPUT, LINK);
        GoodDeed not_posted_deed = 
            new GoodDeed(not_Posted_Entity.getKey(), not_Posted_Entity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        
        List<GoodDeed> actual = pdServlet.fetchPostedDeeds();

        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(posted_deed.getKey(), actual.get(0).getKey());
    }

    @Test
    public void testDoGet() throws IOException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity postedEntity = new Entity(GOOD_DEED);
        postedEntity.setProperty(NAME, TITLE);
        postedEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        postedEntity.setProperty(POSTED_YET, TRUE_STRING);
        postedEntity.setProperty(DAILY_DEED, FALSE_STRING);
        postedEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        postedEntity.setProperty(LINK, LINK);
        ds.put(postedEntity);

        Entity not_Posted_Entity = new Entity(GOOD_DEED);
        not_Posted_Entity.setProperty(NAME, TITLE);
        not_Posted_Entity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        not_Posted_Entity.setProperty(POSTED_YET, FALSE_STRING);
        not_Posted_Entity.setProperty(DAILY_DEED, FALSE_STRING);
        not_Posted_Entity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        not_Posted_Entity.setProperty(LINK, LINK);
        ds.put(not_Posted_Entity);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.thenReturn(pw).when(response).getWriter();

        Gson gson = new Gson();

        GoodDeed posted_deed = 
            new GoodDeed(postedEntity.getKey(), postedEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, TRUE, TIMESTAMP_INPUT, LINK);
        
        List<GoodDeed> expected = new ArrayList<>();
        expected.add(posted_deed);
        
        pdServlet.doGet(request, response);
        String actual = sw.getBuffer().toString().trim();

        Assert.assertEquals(actual, gson.toJson(expected));
    }
}