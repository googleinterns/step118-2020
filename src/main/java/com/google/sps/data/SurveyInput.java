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
 
package com.google.sps.data;
 
/*
 * This class handles a user's Survey Input idea, gives it an id and timestamp
 */
public final class SurveyInput {
 
    private final long id;
    private final String title;
    private final String description;
    private final long votes;
    private final long timestamp;

  public SurveyInput(long id, String title, String description, long votes, long timestamp) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Invalid title, must have some text");
        }

        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Invalid description, must have some text");
        }

        this.id = id;
        this.title = title;
        this.description = description;
        this.votes = votes;
        this.timestamp = timestamp;
  }

  public String getTitle() {
      return this.title;
  }

  public String getDescription() {
      return this.description;
  }

  public long getVotes() {
      return this.votes;
  }

  public long getTimestamp()  {
      return this.timestamp;
  }
}

