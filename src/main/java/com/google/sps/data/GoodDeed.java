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

import com.google.appengine.api.datastore.Key;

public class GoodDeed {
    private final Key key;
    private final long id;
    private final String title;
    private final String description;
    private boolean posted_yet;
    private final long timestamp;
 
    public GoodDeed (Key key, long id, String title, String description, boolean posted_yet, long timestamp) {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
 
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.key = key;
        this.id = id;
        this.title = title;
        this.description = description;
        this.posted_yet = posted_yet;
        this.timestamp = timestamp;
    }

    public Key getKey() {
        return key;
    }

    public boolean getPosted() {
        return posted_yet;
    }
 
}