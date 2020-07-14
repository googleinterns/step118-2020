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

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Properties;

import java.io.IOException;

@WebServlet("/email_users_cron")
public class EmailServlet {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "1deed1day@gmail.com";
    private static final String PASSWORD = "gmail password";
    private static final String EMAIL_SUBJECT = "Complete your daily deed";
    private static final String EMAIL_TEXT = "Temporary Text";
    private static final String HOST = "localhost";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    void sendEmail() {
        
        Properties properties = System.getProperties();
        
        // Enables Authentication
        properties.put("mail.smtp.auth", HOST);
        
        // Starts TSL
        properties.put("mail.smtp.starttls.enable", "true");

        // Setup mail server 
        properties.put("mail.smtp.host", SMTP_SERVER);

        // TLS Port
        properties.put("mail.smtp.port", "587");
        
        // Create seeion with Authenticator
        Session session = Session.getInstance(properties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            }
        );
        
        Message msg = new MimeMessage(session);

        try {
            // Send From
            msg.setFrom(new InternetAddress(USERNAME));

            // Send To
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(TO_ADDRESS, false));

            // Subject
            msg.setSubject(EMAIL_SUBJECT);

            // Content
            msg.setText(EMAIL_TEXT);
            msg.setSendDate( new Date() );

            // Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

            // Connect
            t.connect(SMTP_SERVER, USERNAME, PASSWORD);

            // Send
            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println( "Response: " + t.getLastServerResponse() );
            t.close();

        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Fetch Daily Deed

    //
}