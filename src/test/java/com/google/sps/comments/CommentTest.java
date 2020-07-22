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

package com.google.sps;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;

import java.lang.IllegalArgumentException;

import com.google.sps.data.Comment;

/*The Comment class has the following fields
 *  new Comment(long id, String comment, long timestamp)
 */
@RunWith(JUnit4.class)
public final class CommentTest {
    private static final int NEW_ID = 123;
    private static final String NEW_COMMENT = "comment";
    private static final int NEW_TIMESTAMP = 456;
    
    /*Checks if the class complains when we input an improper comment*/
    @Test(expected = IllegalArgumentException.class)
    public void throwsNullName() {
        Comment comment = new Comment(NEW_ID, null, NEW_TIMESTAMP);
    }

    /*Checks if the class returns the correct comment*/
    @Test
    public void testGetComment() {
        Comment comment = new Comment(NEW_ID, NEW_COMMENT, NEW_TIMESTAMP);
        Assert.assertEquals(NEW_COMMENT, comment.getComment());
    }

    /*Checks if the class returns the correct timestamp*/
    @Test
    public void testGetTimestamp() {
        Comment comment = new Comment(NEW_ID, NEW_COMMENT, NEW_TIMESTAMP);
        Assert.assertEquals(NEW_TIMESTAMP, comment.getTimestamp());
        Assert.assertNotEquals(NEW_ID, comment.getTimestamp());
    }
}
