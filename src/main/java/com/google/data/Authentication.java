package com.google.sps.data;

// Authentication class that stores whether the user is logged in and the redirect link
public final class Authentication {

    private final boolean loggedIn;
    private final String redirectLink;

    public Authentication(boolean loggedIn, String redirectLink) {
        this.loggedIn = loggedIn;
        this.redirectLink = redirectLink;
    }
}