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

import java.lang.InterruptedException;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.activation.DataContentHandler;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import com.google.sps.testing.GoodDeed;
import com.google.sps.testing.GoodDeedsServlet;

@WebServlet("/email_users_cron")
public class EmailServlet extends HttpServlet {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "1deed1day@gmail.com";
    private static final String PASSWORD = "Shreerag";
    private static final String EMAIL_SUBJECT = "Complete your daily deed";
    private static final String HOST = "localhost";

    private GoodDeedsServlet deedServlet;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        deedServlet = new GoodDeedsServlet();
        
        GoodDeed daily_deed = deedServlet.fetchDailyDeed();
        List<String> emails = fetchEmails();
        for(String email : emails) {
            sendEmail(daily_deed, email);
        }
        System.out.println("Email Sent");
        response.sendRedirect("/index.html");
    }

    void sendEmail(GoodDeed deed, String email) {
        
        Properties properties = new Properties();
        
        // Enables Authentication
        properties.put("mail.smtp.auth", "true");
        
        // Starts TSL
        properties.put("mail.smtp.starttls.enable", "true");

        // Setup mail server 
        properties.put("mail.smtp.host", SMTP_SERVER);

        // TLS Port
        properties.put("mail.smtp.port", "587");
        
        // Create session with Authenticator
        Session session = Session.getInstance(properties,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            }
        );
        
        try {
            Message msg = new MimeMessage(session);

            // Send From
            msg.setFrom(new InternetAddress(USERNAME));

            // Send to all recipients
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

            // Subject
            msg.setSubject(EMAIL_SUBJECT);

            // Content
            String text = deed.getTitle() + ":\n" + deed.getDescription();
            msg.setText(text);

            // Get SMTPTransport
            Transport t = session.getTransport("smtp");

            // Connect
            t.connect(SMTP_SERVER, USERNAME, PASSWORD);

            // Send
            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("\nEnd of Send Function\n");

            t.close();
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Fetch Emails
    List<String> fetchEmails() {
        List<String> emails = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query("User");
        PreparedQuery prep_query = datastore.prepare(query);

        for (Entity user : prep_query.asIterable()) {
            String email = (String) user.getProperty("email");
            emails.add(email);
        }

        return emails;
    }
}