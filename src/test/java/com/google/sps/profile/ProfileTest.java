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

package com.google.sps.profile;

import static org.mockito.Mockito.when;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.profile.Profile;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

@RunWith(JUnit4.class)
public final class ProfileTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    private ProfileServlet profileServlet;
    private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FNAME = "John";
    private static final String TEST_LNAME = "Doe";
    private static final String TEST_LOCATION = "Houston, TX";
    private static final String TEST_BIO = "I'm John Doe, and I'm from Houston, Texas.";
    private static final String EMPTY_STRING = "";

    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_FNAME = "fname";
    private static final String PARAMETER_LNAME = "lname";
    private static final String PARAMETER_LOCATION = "location";
    private static final String PARAMETER_BIO = "bio";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profileServlet = new ProfileServlet();
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGetRequestNewProfile() throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // set parameters
        when(request.getParameter(PARAMETER_EMAIL)).thenReturn(TEST_EMAIL);

        // set up for testing JSON getting printed
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when (response.getWriter()).thenReturn(writer);

        // confirm there's nothing in the datastore
        Assert.assertEquals(0, datastore.prepare(new Query("Profile")).countEntities(withDefaults()));

        // call the tested function
        profileServlet.doGetWithDatastore(request, response, datastore);

        // check it was added to datastore
        Assert.assertEquals(1, datastore.prepare(new Query("Profile")).countEntities(withDefaults()));
        // check the right JSON was printed
        Profile expectedProfile = new Profile(EMPTY_STRING, EMPTY_STRING, TEST_EMAIL, EMPTY_STRING, EMPTY_STRING);
        Assert.assertTrue(stringWriter.toString().equals(new Gson().toJson(expectedProfile)));
    }

    @Test
    public void testGetRequestExistingProfile() throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // set parameters
        when(request.getParameter(PARAMETER_EMAIL)).thenReturn(TEST_EMAIL);

        // set up for testing JSON getting printed
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when (response.getWriter()).thenReturn(writer);

        // set the existing entity
        Entity existingEntity = new Entity("Profile", TEST_EMAIL);
        existingEntity.setProperty(PARAMETER_FNAME, TEST_FNAME);
        existingEntity.setProperty(PARAMETER_LNAME, TEST_LNAME);
        existingEntity.setProperty(PARAMETER_EMAIL, TEST_EMAIL);
        existingEntity.setProperty(PARAMETER_LOCATION, TEST_LOCATION);
        existingEntity.setProperty(PARAMETER_BIO, TEST_BIO);
        datastore.put(existingEntity);

        // confirm it was added to the datastore
        Assert.assertEquals(1, datastore.prepare(new Query("Profile")).countEntities(withDefaults()));

        // run the tested function
        profileServlet.doGetWithDatastore(request, response, datastore);
        
        // check nothing else was added to datastore
        Assert.assertEquals(1, datastore.prepare(new Query("Profile")).countEntities(withDefaults()));
        // check the right JSON was printed
        Profile expectedProfile = new Profile(TEST_FNAME, TEST_LNAME, TEST_EMAIL, TEST_LOCATION, TEST_BIO);
        Assert.assertTrue(stringWriter.toString().equals(new Gson().toJson(expectedProfile)));
    }

    @Test
    public void testPostRequest() throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // set parameters
        when(request.getParameter(PARAMETER_FNAME)).thenReturn(TEST_FNAME);
        when(request.getParameter(PARAMETER_LNAME)).thenReturn(TEST_LNAME);
        when(request.getParameter(PARAMETER_EMAIL)).thenReturn(TEST_EMAIL);
        when(request.getParameter(PARAMETER_LOCATION)).thenReturn(TEST_LOCATION);
        when(request.getParameter(PARAMETER_BIO)).thenReturn(TEST_BIO);

        // set up for testing JSON getting printed
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when (response.getWriter()).thenReturn(writer);

        // set and add the existing entity with empty fields
        Entity existingEntity = new Entity("Profile", TEST_EMAIL);
        existingEntity.setProperty(PARAMETER_FNAME, EMPTY_STRING);
        existingEntity.setProperty(PARAMETER_LNAME, EMPTY_STRING);
        existingEntity.setProperty(PARAMETER_EMAIL, TEST_EMAIL);
        existingEntity.setProperty(PARAMETER_LOCATION, EMPTY_STRING);
        existingEntity.setProperty(PARAMETER_BIO, EMPTY_STRING);
        datastore.put(existingEntity);

        // confirm it was added to the datastore
        Assert.assertEquals(1, datastore.prepare(new Query("Profile")).countEntities(withDefaults()));

        profileServlet.doPostWithDatastore(request, response, datastore);

        // check that it was updated in the datastore
        Query profileQuery = new Query("Profile");
        PreparedQuery profiles = datastore.prepare(profileQuery);

        Assert.assertEquals(1, profiles.countEntities(withDefaults()));

        for(Entity profile : profiles.asIterable()) {
            Assert.assertEquals(TEST_FNAME, (String) profile.getProperty(PARAMETER_FNAME));
            Assert.assertEquals(TEST_LNAME, (String) profile.getProperty(PARAMETER_LNAME));
            Assert.assertEquals(TEST_EMAIL, (String) profile.getProperty(PARAMETER_EMAIL));
            Assert.assertEquals(TEST_LOCATION, (String) profile.getProperty(PARAMETER_LOCATION));
            Assert.assertEquals(TEST_BIO, (String) profile.getProperty(PARAMETER_BIO));
        }
        // check the right JSON was printed
        Profile expectedProfile = new Profile(TEST_FNAME, TEST_LNAME, TEST_EMAIL, TEST_LOCATION, TEST_BIO);
        Assert.assertTrue(stringWriter.toString().equals(new Gson().toJson(expectedProfile)));
    }

    @Test
    public void testGetEntityAsProfile() {
        // set the existing entity
        Entity entity = new Entity("Profile", TEST_EMAIL);
        entity.setProperty(PARAMETER_FNAME, TEST_FNAME);
        entity.setProperty(PARAMETER_LNAME, TEST_LNAME);
        entity.setProperty(PARAMETER_EMAIL, TEST_EMAIL);
        entity.setProperty(PARAMETER_LOCATION, TEST_LOCATION);
        entity.setProperty(PARAMETER_BIO, TEST_BIO);

        Profile actualProfile = profileServlet.getEntityAsProfile(entity);

        Assert.assertEquals(TEST_FNAME, actualProfile.getFname());
        Assert.assertEquals(TEST_LNAME, actualProfile.getLname());
        Assert.assertEquals(TEST_EMAIL, actualProfile.getEmail());
        Assert.assertEquals(TEST_BIO, actualProfile.getBio());
        Assert.assertEquals(TEST_LOCATION, actualProfile.getLocation());
    }
}