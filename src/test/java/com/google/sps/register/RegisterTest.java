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

package com.google.sps.register;

import static org.mockito.Mockito.when;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withDefaults;

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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;


@RunWith(JUnit4.class)
public final class RegisterTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    private RegisterServlet registerServlet;
    private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig());

    private static final String TEST_NAME = "John Doe";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME_ALT = "Jane Doe";
    private static final String TEST_EMAIL_ALT = "alt@example.com";
    private static final String EMPTY_STRING = "";
    private static final String MESSAGE_LOGIN = "Login to start doing good deeds!";
    private static final String ERROR_MESSAGE_EXISTING = "A user with this email already exists";
    private static final String ERROR_MESSAGE_DATABASE = "Internal error with database";
    private static final String ERROR_MESSAGE_NAME = "Please enter a name";
    private static final String ERROR_MESSAGE_EMAIL = "Please enter an email";
    private static final int MORE_THAN_ONE_RESULT = 2;
    private static final boolean YES_ERROR = true;
    private static final boolean NO_ERROR = false;

    private static String EXPECTED_LOGOUT_URL;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registerServlet = new RegisterServlet();
        helper.setUp();

        UserService userService = UserServiceFactory.getUserService();
        EXPECTED_LOGOUT_URL = userService.createLoginURL("/");
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // test if both inputs are empty
	@Test
    public void testGetRegisterResponseNameAndEmailEmpty() {
        when(request.getParameter("name")).thenReturn(EMPTY_STRING);
        when(request.getParameter("email")).thenReturn(EMPTY_STRING);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponse(request, response);

        // expected response when both are empty because it first sees that there is no name and returns error
        RegisterResponse registerResponseExpected = new RegisterResponse(YES_ERROR, ERROR_MESSAGE_NAME, EMPTY_STRING);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if only name input is empty
    @Test
    public void testGetRegisterResponseNameEmpty() {
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("email")).thenReturn(TEST_EMAIL);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponse(request, response);

        // expected response when both are empty because it first sees that there is no name and returns error
        RegisterResponse registerResponseExpected = new RegisterResponse(YES_ERROR, ERROR_MESSAGE_NAME, EMPTY_STRING);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if only email input is empty
    @Test
    public void testGetRegisterResponseEmailEmpty() {
        when(request.getParameter("name")).thenReturn(TEST_NAME);
        when(request.getParameter("email")).thenReturn(EMPTY_STRING);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponse(request, response);

        // expected response when both are empty because it first sees that there is no name and returns error
        RegisterResponse registerResponseExpected = new RegisterResponse(YES_ERROR, ERROR_MESSAGE_EMAIL, EMPTY_STRING);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if addUserToDatastore() works
    @Test
    public void testAddUserToDatastore() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        registerServlet.addUserToDatastore(TEST_NAME, TEST_EMAIL, datastore);

        Query usersQuery = new Query("User");
        PreparedQuery users = datastore.prepare(usersQuery);

        Assert.assertEquals(1, users.countEntities(withDefaults()));

        for (Entity user : users.asIterable()) {
            Assert.assertEquals(TEST_NAME, (String) user.getProperty("name"));
            Assert.assertEquals(TEST_EMAIL, (String) user.getProperty("email"));
        }
    }

    // test if getRegisterResponseValidInput() works, no other users
    @Test
    public void testGetRegisterResponseValidInputNoUsers() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        // create expected RegisterResponse
        RegisterResponse registerResponseExpected = new RegisterResponse(NO_ERROR, MESSAGE_LOGIN, EXPECTED_LOGOUT_URL);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponseValidInput(TEST_NAME, TEST_EMAIL, datastore);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if getRegisterResponseValidInput() works, existing user
    @Test
    public void testGetRegisterResponseValidInputExistingUser() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // add existing entity to datastore
        Entity userEntity = new Entity("User", TEST_EMAIL);
        userEntity.setProperty("name", TEST_NAME);
        userEntity.setProperty("email", TEST_EMAIL);
        datastore.put(userEntity);
        
        // create expected RegisterResponse
        RegisterResponse registerResponseExpected = new RegisterResponse(YES_ERROR, ERROR_MESSAGE_EXISTING, EMPTY_STRING);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponseValidInput(TEST_NAME, TEST_EMAIL, datastore);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if getRegisterResponseValidInput() works, existing different user
    @Test
    public void testGetRegisterResponseValidInputExistingDifferentUser() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // add existing entity to datastore
        Entity userEntity = new Entity("User", TEST_EMAIL_ALT);
        userEntity.setProperty("name", TEST_NAME_ALT);
        userEntity.setProperty("email", TEST_EMAIL_ALT);
        datastore.put(userEntity);
        
        // create expected RegisterResponse
        RegisterResponse registerResponseExpected = new RegisterResponse(NO_ERROR, MESSAGE_LOGIN, EXPECTED_LOGOUT_URL);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponseValidInput(TEST_NAME, TEST_EMAIL, datastore);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }

    // test if getRegisterResponseValidInput() works, multiple different users
    @Test
    public void testGetRegisterResponseValidInputExistingMultipleUsers() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        // create expected RegisterResponse
        RegisterResponse registerResponseExpected = new RegisterResponse(YES_ERROR, ERROR_MESSAGE_DATABASE, EMPTY_STRING);

        RegisterResponse registerResponseActual = registerServlet.getRegisterResponseValidInputWithNumResults(TEST_NAME, TEST_EMAIL, datastore, MORE_THAN_ONE_RESULT);

        Assert.assertEquals(registerResponseExpected.getError(), registerResponseActual.getError());
        Assert.assertEquals(registerResponseExpected.getMessage(), registerResponseActual.getMessage());
        Assert.assertEquals(registerResponseExpected.getLink(), registerResponseActual.getLink());
    }
} 