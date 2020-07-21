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
    private static final Comment comment = new Comment(123, "comment", 456);
    
    /*Checks if the class complains when we input an improper comment*/
    @Test(expected = IllegalArgumentException.class)
    public void throwsNullName() {
        Comment comment = new Comment(123, null, 456);
    }

    /*Checks if the class returns the correct comment*/
    @Test
    public void testGetComment() {
        Assert.assertEquals("comment", comment.getComment());
    }

    /*Checks if the class returns the correct timestamp*/
    @Test
    public void testGetTimestamp() {
        Assert.assertEquals(456, comment.getTimestamp());
        Assert.assertNotEquals(123, comment.getTimestamp());
    }
}
