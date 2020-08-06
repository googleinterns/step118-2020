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

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import java.io.IOException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import static org.mockito.Mockito.mock;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jvnet.mock_javamail.Mailbox;

import java.util.List;
import java.util.ArrayList;

import com.google.sps.testing.EmailServlet;
import com.google.sps.testing.GoodDeed;

@RunWith(JUnit4.class)
public class EmailTesting {
    private EmailServlet emailServlet;

    private static final String USER = "Profile";
    private static final String EMAIL = "email";
    private static final String EMAIL_INPUT = "test@gmail.com";
    private static final String EMAIL_SUBJECT = "Complete your daily deed";

    private static final String GOOD_DEED = "GoodDeed";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String POSTED_YET = "Posted Yet";
    private static final String DAILY_DEED = "Daily Deed";
    private static final String TIMESTAMP = "Timestamp";
    private static final String LINK = "Link";

    private static final String TITLE = "Deed Name";
    private static final String DESCRIPTION_INPUT = "Deed Description";
    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    private static final long TIMESTAMP_INPUT = 67890;
    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        emailServlet = new EmailServlet();
        helper.setUp();
        Mailbox.clearAll();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testFetchEmails() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity testEntity = new Entity(USER);
        testEntity.setProperty(EMAIL, EMAIL_INPUT);
        ds.put(testEntity);

        Query query = new Query(USER);

        Assert.assertEquals(1, ds.prepare(query).countEntities(withLimit(10)));

        List<String> emails = emailServlet.fetchEmails();
        String email = emails.get(0);

        Assert.assertTrue(email.equals(EMAIL_INPUT));
    }

    @Test
    public void testsendEmail() throws IOException, AddressException, MessagingException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        Entity testEntity = new Entity(GOOD_DEED);
        testEntity.setProperty(NAME, TITLE);
        testEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        testEntity.setProperty(POSTED_YET, FALSE_STRING);
        testEntity.setProperty(DAILY_DEED, FALSE_STRING);
        testEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        testEntity.setProperty(LINK, LINK);
        ds.put(testEntity);

        GoodDeed deed = 
            new GoodDeed(testEntity.getKey(), testEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        
        String content = deed.getTitle() + ":\n" + deed.getDescription();

        List<String> emails = new ArrayList<>();
        emails.add(EMAIL_INPUT);
        
        emailServlet.sendEmail(deed, emails);

        List<Message> inbox = Mailbox.get(EMAIL_INPUT);
  
        Assert.assertTrue(inbox.size() == 1);  
        Assert.assertEquals(EMAIL_SUBJECT, inbox.get(0).getSubject());
        Assert.assertEquals(content, inbox.get(0).getContent());
    }


    @Test
    public void testDoGet() throws IOException, AddressException, MessagingException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Entity deedEntity = new Entity(GOOD_DEED);
        deedEntity.setProperty(NAME, TITLE);
        deedEntity.setProperty(DESCRIPTION, DESCRIPTION_INPUT);
        deedEntity.setProperty(POSTED_YET, TRUE_STRING);
        deedEntity.setProperty(DAILY_DEED, TRUE_STRING);
        deedEntity.setProperty(TIMESTAMP, TIMESTAMP_INPUT);
        deedEntity.setProperty(LINK, LINK);
        ds.put(deedEntity);

        Entity emailEntity = new Entity(USER);
        emailEntity.setProperty(EMAIL, EMAIL_INPUT);
        ds.put(emailEntity);

        GoodDeed deed = 
            new GoodDeed(deedEntity.getKey(), deedEntity.getKey().getId(), TITLE, DESCRIPTION_INPUT, FALSE, TIMESTAMP_INPUT, LINK);
        
        String content = deed.getTitle() + ":\n" + deed.getDescription();

        emailServlet.doGet( mock(HttpServletRequest.class), mock(HttpServletResponse.class) );

        List<Message> inbox = Mailbox.get(EMAIL_INPUT);
  
        Assert.assertTrue(inbox.size() == 1);  
        Assert.assertEquals(EMAIL_SUBJECT, inbox.get(0).getSubject());
        Assert.assertEquals(content, inbox.get(0).getContent());
    }
}