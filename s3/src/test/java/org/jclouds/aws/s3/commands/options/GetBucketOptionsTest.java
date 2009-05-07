/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.commands.options;

import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.delimiter;
import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.marker;
import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.maxKeys;
import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.prefix;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Tests possible uses of GetBucketOptions and GetBucketOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class GetBucketOptionsTest {

    @Test
    public void testPrefix() {
	GetBucketOptions options = new GetBucketOptions();
	options.prefix("test");
	assertEquals(options.getPrefix(), "test");
    }

    @Test
    public void testNoOptionsQueryString() {
	GetBucketOptions options = new GetBucketOptions();
	assertEquals(options.toQueryString(), "");
    }

    @Test
    public void testOneOptionQueryString() {
	GetBucketOptions options = new GetBucketOptions();
	options.prefix("test");
	assertEquals(options.toQueryString(), "?prefix=test");
    }

    @Test
    public void testTwoOptionQueryString() {
	GetBucketOptions options = new GetBucketOptions();
	options.prefix("test").maxKeys(1);
	try {
	    assertEquals(options.toQueryString(), "?prefix=test&max-keys=1");
	} catch (AssertionError e) {
	    assertEquals(options.toQueryString(), "?max-keys=1&prefix=test");

	}
    }

    @Test
    public void testNullPrefix() {
	GetBucketOptions options = new GetBucketOptions();
	assertNull(options.getPrefix());
    }

    @Test
    public void testPrefixStatic() {
	GetBucketOptions options = prefix("test");
	assertEquals(options.getPrefix(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPrefixNPE() {
	prefix(null);
    }

    @Test
    public void testMarker() {
	GetBucketOptions options = new GetBucketOptions();
	options.marker("test");
	assertEquals(options.getMarker(), "test");
    }

    @Test
    public void testNullMarker() {
	GetBucketOptions options = new GetBucketOptions();
	assertNull(options.getMarker());
    }

    @Test
    public void testMarkerStatic() {
	GetBucketOptions options = marker("test");
	assertEquals(options.getMarker(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMarkerNPE() {
	marker(null);
    }

    @Test
    public void testMaxKeys() {
	GetBucketOptions options = new GetBucketOptions();
	options.maxKeys(1000);
	assertEquals(options.getMaxKeys(), "1000");
    }

    @Test
    public void testNullMaxKeys() {
	GetBucketOptions options = new GetBucketOptions();
	assertNull(options.getMaxKeys());
    }

    @Test
    public void testMaxKeysStatic() {
	GetBucketOptions options = maxKeys(1000);
	assertEquals(options.getMaxKeys(), "1000");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMaxKeysNegative() {
	maxKeys(-1);
    }

    @Test
    public void testDelimiter() {
	GetBucketOptions options = new GetBucketOptions();
	options.delimiter("test");
	assertEquals(options.getDelimiter(), "test");
    }

    @Test
    public void testNullDelimiter() {
	GetBucketOptions options = new GetBucketOptions();
	assertNull(options.getDelimiter());
    }

    @Test
    public void testDelimiterStatic() {
	GetBucketOptions options = delimiter("test");
	assertEquals(options.getDelimiter(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDelimiterNPE() {
	delimiter(null);
    }
}
