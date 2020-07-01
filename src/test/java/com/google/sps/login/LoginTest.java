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

package com.google.sps.login;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

@RunWith(JUnit4.class)
public final class LoginTest {

	private Login login;

    @Before
    public void setUp() {
        // instantiate Login class
        login = new Login();
    }

    @Test
    public void testIsLoggedIn() {
        // set up testing environment where user is logged in
        LocalServiceTestHelper helperLoggedIn = new LocalServiceTestHelper(new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true);
        helperLoggedIn.setUp();
        UserService userService = UserServiceFactory.getUserService();

        // create expected authentication class that's logged in
        String expectedLogoutURL = userService.createLogoutURL("/");
        Authentication authExpected = new Authentication(true, expectedLogoutURL);

        // get actual return from Login.java
        Authentication authActual = login.getAuthentication(userService);

        // check values of expected and actual to make sure they align
        Assert.assertEquals(authExpected.getLoggedIn(), authActual.getLoggedIn());
        Assert.assertEquals(authExpected.getRedirectLink(), authActual.getRedirectLink());

        // tear down testing environment
        helperLoggedIn.tearDown();
    }

    @Test
    public void testIsLoggedOut() {
        // set up testing environment where user is logged in
        LocalServiceTestHelper helperLoggedOut = new LocalServiceTestHelper(new LocalUserServiceTestConfig()).setEnvIsLoggedIn(false);
        helperLoggedOut.setUp();
        UserService userService = UserServiceFactory.getUserService();

        // create expected authentication class that's logged in
        String expectedLogoutURL = userService.createLoginURL("/");
        Authentication authExpected = new Authentication(false, expectedLogoutURL);

        // get actual return from Login.java
        Authentication authActual = login.getAuthentication(userService);

        // check values of expected and actual to make sure they align
        Assert.assertEquals(authExpected.getLoggedIn(), authActual.getLoggedIn());
        Assert.assertEquals(authExpected.getRedirectLink(), authActual.getRedirectLink());

        // tear down testing environment
        helperLoggedOut.tearDown();
    }
}