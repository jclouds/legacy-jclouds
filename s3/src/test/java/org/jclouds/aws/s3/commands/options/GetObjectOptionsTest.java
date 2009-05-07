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

import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.ifMd5DoesntMatch;
import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.ifMd5Matches;
import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.ifModifiedSince;
import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.UnsupportedEncodingException;

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests possible uses of GetObjectOptions and GetObjectOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class GetObjectOptionsTest {

    private byte[] testBytes;
    private DateTime now;
    private String nowExpected;

    @BeforeTest
    void setUp() {
	now = new DateTime();
	nowExpected = new DateService().toHeaderString(now);
	testBytes = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
    }

    @Test
    public void testIfModifiedSince() {
	GetObjectOptions options = new GetObjectOptions();
	options.ifModifiedSince(now);
	assertEquals(options.getIfModifiedSince(), nowExpected);
    }

    @Test
    public void testNullIfModifiedSince() {
	GetObjectOptions options = new GetObjectOptions();
	assertNull(options.getIfModifiedSince());
    }

    @Test
    public void testIfModifiedSinceStatic() {
	GetObjectOptions options = ifModifiedSince(now);
	assertEquals(options.getIfModifiedSince(), nowExpected);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIfModifiedSinceNPE() {
	ifModifiedSince(null);
    }

    @Test
    public void testIfUnmodifiedSince() {
	GetObjectOptions options = new GetObjectOptions();
	options.ifUnmodifiedSince(now);
	isNowExpected(options);
    }

    @Test
    public void testNullIfUnmodifiedSince() {
	GetObjectOptions options = new GetObjectOptions();
	assertNull(options.getIfUnmodifiedSince());
    }

    @Test
    public void testIfUnmodifiedSinceStatic() {
	GetObjectOptions options = ifUnmodifiedSince(now);
	isNowExpected(options);
    }

    private void isNowExpected(GetObjectOptions options) {
	assertEquals(options.getIfUnmodifiedSince(), nowExpected);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIfUnmodifiedSinceNPE() {
	ifUnmodifiedSince(null);
    }

    public void testModifiedSinceAndRange() {
	GetObjectOptions options = new GetObjectOptions();
	options.ifModifiedSince(now);
	options.range(0, 1024);
	isNowExpected(options);
	bytes1to1024(options);
    }

    @Test
    public void testRange() {
	GetObjectOptions options = new GetObjectOptions();
	options.range(0, 1024);
	bytes1to1024(options);
    }

    private void bytes1to1024(GetObjectOptions options) {
	assertEquals(options.getRange(), "bytes=0-1024");
    }

    @Test
    public void testNullRange() {
	GetObjectOptions options = new GetObjectOptions();
	assertNull(options.getRange());
    }

    @Test
    public void testRangeStatic() {
	GetObjectOptions options = range(0, 1024);
	bytes1to1024(options);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testRangeNegative1() {
	range(-1, 0);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testRangeNegative2() {
	range(0, -1);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testRangeNegative() {
	range(-1, -1);
    }

    @Test
    public void testIfMd5Matches() throws UnsupportedEncodingException {
	GetObjectOptions options = new GetObjectOptions();
	options.ifMd5Matches(testBytes);
	matchesHex(options.getIfMatch());
    }

    @Test
    public void testNullIfMd5Matches() {
	GetObjectOptions options = new GetObjectOptions();
	assertNull(options.getIfMatch());
    }

    @Test
    public void testIfMd5MatchesStatic() throws UnsupportedEncodingException {
	GetObjectOptions options = ifMd5Matches(testBytes);
	matchesHex(options.getIfMatch());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIfMd5MatchesNPE() throws UnsupportedEncodingException {
	ifMd5Matches(null);
    }

    @Test
    public void testIfMd5DoesntMatch() throws UnsupportedEncodingException {
	GetObjectOptions options = new GetObjectOptions();
	options.ifMd5DoesntMatch(testBytes);
	matchesHex(options.getIfNoneMatch());
    }

    @Test
    public void testNullIfMd5DoesntMatch() {
	GetObjectOptions options = new GetObjectOptions();
	assertNull(options.getIfNoneMatch());
    }

    @Test
    public void testIfMd5DoesntMatchStatic()
	    throws UnsupportedEncodingException {
	GetObjectOptions options = ifMd5DoesntMatch(testBytes);
	matchesHex(options.getIfNoneMatch());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIfMd5DoesntMatchNPE() throws UnsupportedEncodingException {
	ifMd5DoesntMatch(null);
    }

    private void matchesHex(String match) throws UnsupportedEncodingException {
	String expected = "\"" + S3Utils.toHexString(testBytes) + "\"";
	assertEquals(match, expected);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testIfUnmodifiedAfterModified() {
	ifModifiedSince(now).ifUnmodifiedSince(now);

    }

    public void testIfUnmodifiedAfterMd5Matches()
	    throws UnsupportedEncodingException {
	ifMd5Matches(testBytes).ifUnmodifiedSince(now);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testIfUnmodifiedAfterMd5DoesntMatch()
	    throws UnsupportedEncodingException {
	ifMd5DoesntMatch(testBytes).ifUnmodifiedSince(now);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testIfModifiedAfterUnmodified() {
	ifUnmodifiedSince(now).ifModifiedSince(now);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testIfModifiedAfterMd5Matches()
	    throws UnsupportedEncodingException {
	ifMd5Matches(testBytes).ifModifiedSince(now);

    }

    public void testIfModifiedAfterMd5DoesntMatch()
	    throws UnsupportedEncodingException {
	ifMd5DoesntMatch(testBytes).ifModifiedSince(now);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMd5MatchesAfterIfModified()
	    throws UnsupportedEncodingException {
	ifModifiedSince(now).ifMd5Matches(testBytes);

    }

    public void testMd5MatchesAfterIfUnmodified()
	    throws UnsupportedEncodingException {
	ifUnmodifiedSince(now).ifMd5Matches(testBytes);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMd5MatchesAfterMd5DoesntMatch()
	    throws UnsupportedEncodingException {
	ifMd5DoesntMatch(testBytes).ifMd5Matches(testBytes);
    }

    public void testMd5DoesntMatchAfterIfModified()
	    throws UnsupportedEncodingException {
	ifModifiedSince(now).ifMd5DoesntMatch(testBytes);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMd5DoesntMatchAfterIfUnmodified()
	    throws UnsupportedEncodingException {
	ifUnmodifiedSince(now).ifMd5DoesntMatch(testBytes);

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMd5DoesntMatchAfterMd5Matches()
	    throws UnsupportedEncodingException {
	ifMd5Matches(testBytes).ifMd5DoesntMatch(testBytes);
    }

}
