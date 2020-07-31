 
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

package com.google.sps.profile;

// Profile class for each user's profile
public final class Profile {

    private final String fname;
    private final String lname;
    private final String email;
    private final String location;
    private final String bio;

    public Profile(String fname, String lname, String email, String location, String bio) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.location = location;
        this.bio = bio;
    }
}