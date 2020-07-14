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

package com.google.sps.login;

// Authentication class that stores whether the user is logged in and the redirect link
public final class Authentication {

    private final boolean loggedIn;
    private final String redirectLink;

    public Authentication(boolean loggedIn, String redirectLink) {
        this.loggedIn = loggedIn;
        this.redirectLink = redirectLink;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public String getRedirectLink() {
        return redirectLink;
    }
}