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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.s3.commands.options.ListBucketOptions.Builder.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.UnsupportedEncodingException;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of ListBucketOptions and ListBucketOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListBucketOptionsTest {

    @Test
    public void testPrefix() throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.withPrefix("test");
	assertEquals(options.getPrefix(), "test");
    }

    @Test
    public void testNoOptionsQueryString() {
	HttpRequestOptions options = new ListBucketOptions();
	assertEquals(options.buildQueryString(), "");
    }

    @Test
    public void testOneOptionQueryString() throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.withPrefix("test");
	assertEquals(options.buildQueryString(), "?prefix=test");
    }

    @Test
    public void testTwoOptionQueryString() throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.withPrefix("test").maxResults(1);
	String query = options.buildQueryString();
	checkQuery(query);
	checkQuery(checkNotNull(query));

    }

    private void checkQuery(String query) {
	try {
	    assertEquals(query, "?prefix=test&max-keys=1");
	} catch (AssertionError e) {
	    assertEquals(query, "?max-keys=1&prefix=test");
	}
    }

    @Test
    public void testPrefixAndDelimiterUrlEncodingQueryString()
	    throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.withPrefix("/test").delimiter("/");
	String query = options.buildQueryString();
	checkEncodedQuery(query);
	checkEncodedQuery(checkNotNull(query));

    }

    private void checkEncodedQuery(String query) {
	try {
	    assertEquals(query, "?prefix=%2Ftest&delimiter=%2F");
	} catch (AssertionError e) {
	    assertEquals(query, "?delimiter=%2F&prefix=%2Ftest");
	}
    }

    @Test
    public void testNullPrefix() {
	ListBucketOptions options = new ListBucketOptions();
	assertNull(options.getPrefix());
    }

    @Test
    public void testPrefixStatic() throws UnsupportedEncodingException {
	ListBucketOptions options = withPrefix("test");
	assertEquals(options.getPrefix(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPrefixNPE() throws UnsupportedEncodingException {
	withPrefix(null);
    }

    @Test
    public void testMarker() throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.afterMarker("test");
	assertEquals(options.getMarker(), "test");
    }

    @Test
    public void testNullMarker() {
	ListBucketOptions options = new ListBucketOptions();
	assertNull(options.getMarker());
    }

    @Test
    public void testMarkerStatic() throws UnsupportedEncodingException {
	ListBucketOptions options = afterMarker("test");
	assertEquals(options.getMarker(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMarkerNPE() throws UnsupportedEncodingException {
	afterMarker(null);
    }

    @Test
    public void testMaxKeys() {
	ListBucketOptions options = new ListBucketOptions();
	options.maxResults(1000);
	assertEquals(options.getMaxKeys(), "1000");
    }

    @Test
    public void testNullMaxKeys() {
	ListBucketOptions options = new ListBucketOptions();
	assertNull(options.getMaxKeys());
    }

    @Test
    public void testMaxKeysStatic() {
	ListBucketOptions options = maxResults(1000);
	assertEquals(options.getMaxKeys(), "1000");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMaxKeysNegative() {
	maxResults(-1);
    }

    @Test
    public void testDelimiter() throws UnsupportedEncodingException {
	ListBucketOptions options = new ListBucketOptions();
	options.delimiter("test");
	assertEquals(options.getDelimiter(), "test");
    }

    @Test
    public void testNullDelimiter() {
	ListBucketOptions options = new ListBucketOptions();
	assertNull(options.getDelimiter());
    }

    @Test
    public void testDelimiterStatic() throws UnsupportedEncodingException {
	ListBucketOptions options = delimiter("test");
	assertEquals(options.getDelimiter(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDelimiterNPE() throws UnsupportedEncodingException {
	delimiter(null);
    }
}
