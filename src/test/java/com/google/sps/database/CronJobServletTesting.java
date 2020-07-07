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

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertArrayEquals;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.asList;

import com.google.sps.data.GoodDeed;
import com.google.sps.servlet.CronServlet;

@RunWith(JUnit4.class)
public class CronJobServletTesting {
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
    private static final String TITLE_2 = "Deed Name #2";
    private static final String DESCRIPTION_INPUT_2 = "Deed Description #2";
    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    private CronServlet cron;

    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        cron = new CronServlet();
        helper.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDailyDeedReset() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(DAILY_DEED, TRUE_STRING);
        ds.put(testEntity);

        Filter propertyFilter = new FilterPredicate(DAILY_DEED, FilterOperator.EQUAL, TRUE_STRING);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);

        Assert.assertEquals(1, ds.prepare(query).countEntities(withLimit(10)));

        cron.resetDailyDeed(ds);

        Assert.assertEquals(0, ds.prepare(query).countEntities(withLimit(10)));
    }

    @Test
    public void testPostedYetReset() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(POSTED_YET, TRUE_STRING);
        ds.put(testEntity);

        Filter propertyFilter = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, TRUE_STRING);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);

        Assert.assertEquals(1, ds.prepare(query).countEntities(withLimit(10)));

        cron.resetPostedYet(ds);

        Assert.assertEquals(0, ds.prepare(query).countEntities(withLimit(10)));

    }

    @Test
    public void testDatabasePull() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, FALSE_STRING);
        testEntity.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity.setProperty(LINK, LINK);
        ds.put(testEntity);

        Entity testEntity2 = new Entity(GOOD_DEED);
        testEntity2.setProperty(NAME, TITLE_2);
        testEntity2.setProperty(DESCRIPTION, DESCRIPTION_INPUT_2);
        testEntity2.setProperty(POSTED_YET, FALSE_STRING);
        testEntity2.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity2.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity2.setProperty(LINK, LINK);
        ds.put(testEntity2);

        GoodDeed deed1 = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        GoodDeed deed2 = 
            new GoodDeed(testEntity2.getKey(), testEntity2.getKey().getId(), TITLE_2, DESCRIPTION_INPUT_2, FALSE, TIMESTAMP_INPUT, LINK);

        ArrayList<GoodDeed> actual = cron.PullDeedsFromDatastore(ds);

        Assert.assertEquals(2, actual.size());

        Assert.assertEquals(actual.get(0).getKey(), deed1.getKey());
        Assert.assertEquals(actual.get(1).getKey(), deed2.getKey());

    }

    @Test
    public void testSelectDailyDeed() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, FALSE_STRING);
        testEntity.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity.setProperty(LINK, LINK);
        ds.put(testEntity);

        Entity testEntity2 = new Entity(GOOD_DEED);
        testEntity2.setProperty(NAME, TITLE_2);
        testEntity2.setProperty(DESCRIPTION, DESCRIPTION_INPUT_2);
        testEntity2.setProperty(POSTED_YET, FALSE_STRING);
        testEntity2.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity2.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity2.setProperty(LINK, LINK);
        ds.put(testEntity2);

        GoodDeed deed1 = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        GoodDeed deed2 = 
            new GoodDeed(testEntity2.getKey(), testEntity2.getKey().getId(), TITLE_2, DESCRIPTION_INPUT_2, FALSE, TIMESTAMP_INPUT, LINK);
        
        List<GoodDeed> deeds = new ArrayList<>();
        deeds.add(deed1);
        deeds.add(deed2);

        // Reset Daily Deed and Posted Yet
        cron.resetPostedYet(ds);
        cron.resetDailyDeed(ds);
        
        // Function call
        cron.selectDailyDeed(deeds);

        // Checks that there is only one daily deed and posted deed after function call
        Filter propertyFilter = new FilterPredicate(DAILY_DEED, FilterOperator.EQUAL, TRUE_STRING);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);

        Assert.assertEquals(1, ds.prepare(query).countEntities(withLimit(10)));

        Filter propertyFilter2 = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, TRUE_STRING);
        Query query2 = new Query(GOOD_DEED).setFilter(propertyFilter2);

        Assert.assertEquals(1, ds.prepare(query2).countEntities(withLimit(10)));
        
    }

    @Test
    public void testCleanDeeds() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, FALSE_STRING);
        testEntity.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity.setProperty(LINK, LINK);
        ds.put(testEntity);

        Entity testEntity2 = new Entity(GOOD_DEED);
        testEntity2.setProperty(NAME, TITLE_2);
        testEntity2.setProperty(DESCRIPTION, DESCRIPTION_INPUT_2);
        testEntity2.setProperty(POSTED_YET, FALSE_STRING);
        testEntity2.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity2.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity2.setProperty(LINK, LINK);
        ds.put(testEntity2);

        // Posted Yet = false
        GoodDeed deed1 = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        // Posted Yet = true
        GoodDeed deed2 = 
            new GoodDeed(testEntity2.getKey(), testEntity2.getKey().getId(), TITLE_2, DESCRIPTION_INPUT_2, TRUE, TIMESTAMP_INPUT, LINK);
        
        List<GoodDeed> deeds = new ArrayList<>();
        deeds.add(deed1);
        deeds.add(deed2);

        List<GoodDeed> cleaned_deeds = cron.cleanDeeds(deeds);

        Assert.assertEquals(1, cleaned_deeds.size());
        Assert.assertEquals(deed1.getKey(), cleaned_deeds.get(0).getKey());
    }
    
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Test
    public void testDoGet() throws IOException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, FALSE_STRING);
        testEntity.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity.setProperty(LINK, LINK);
        ds.put(testEntity);

        Entity testEntity2 = new Entity(GOOD_DEED);
        testEntity2.setProperty(NAME, TITLE_2);
        testEntity2.setProperty(DESCRIPTION, DESCRIPTION_INPUT_2);
        testEntity2.setProperty(POSTED_YET, FALSE_STRING);
        testEntity2.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity2.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity2.setProperty(LINK, LINK);
        ds.put(testEntity2);

        cron.doGet(request, response);

        Filter propertyFilter = new FilterPredicate(DAILY_DEED, FilterOperator.EQUAL, TRUE_STRING);
        Query query = new Query(GOOD_DEED).setFilter(propertyFilter);

        Assert.assertEquals(1, ds.prepare(query).countEntities(withLimit(10)));

        Filter propertyFilter2 = new FilterPredicate(POSTED_YET, FilterOperator.EQUAL, TRUE_STRING);
        Query query2 = new Query(GOOD_DEED).setFilter(propertyFilter2);

        Assert.assertEquals(1, ds.prepare(query2).countEntities(withLimit(10)));
    }

}
