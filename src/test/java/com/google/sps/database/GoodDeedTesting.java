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

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import com.google.sps.testing.GoodDeed;
import java.lang.IllegalArgumentException;



@RunWith(JUnit4.class)
public final class GoodDeedTesting {
    private static final String GOOD_DEED = "Good Deed";
    private static final String KEY = "Key";
    private static final long ID = 12345;
    private static final String TITLE = "Deed Name";
    private static final String DESCRIPTION = "Deed Description";
    private static final boolean POSTED_YET = true;
    private static final String LINK = "Link";
    private static final long TIMESTAMP = 67890;

    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsNullName() {
        Key k1 = KeyFactory.createKey(GOOD_DEED, KEY);
        GoodDeed deed = new GoodDeed(k1, ID, null, DESCRIPTION, POSTED_YET, TIMESTAMP, LINK);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsNullDescription() {
        Key k1 = KeyFactory.createKey(GOOD_DEED, KEY);
        GoodDeed deed = new GoodDeed(k1, ID, TITLE, null, POSTED_YET, TIMESTAMP, LINK);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsNullLink() {
        Key k1 = KeyFactory.createKey(GOOD_DEED, KEY);
        GoodDeed deed = new GoodDeed(k1, ID, TITLE, DESCRIPTION, POSTED_YET, TIMESTAMP, null);
    }
    @Test
    public void testGetKey() {
        Key k1 = KeyFactory.createKey(GOOD_DEED, KEY);
        GoodDeed deed = new GoodDeed(k1, ID, TITLE, DESCRIPTION, POSTED_YET, TIMESTAMP, LINK);
        Key actual = deed.getKey();

        Assert.assertEquals(k1, actual);
    }

    @Test
    public void testGetPosted() {
        Key k1 = KeyFactory.createKey(GOOD_DEED, KEY);
        GoodDeed deed = new GoodDeed(k1, ID, TITLE, DESCRIPTION, POSTED_YET, TIMESTAMP, LINK);
        boolean actual = deed.getPosted();

        Assert.assertEquals(POSTED_YET, actual);
    }
}