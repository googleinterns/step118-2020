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

package com.google.sps.register;

// RegisterResponse class that stores whether there was an error and the corresponding message, along with an optional link
public final class RegisterResponse {

    private final boolean error;
    private final String message;
    private final String link;

    // constructor if there's an error (don't need redirect link)
    public RegisterResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
        this.link = "";
    }

    // constructor if there's no error (need redirect link)
    public RegisterResponse(boolean error, String message, String link) {
        this.error = error;
        this.message = message;
        this.link = link;
    }

    public boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }
}