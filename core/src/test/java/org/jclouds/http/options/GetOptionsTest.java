/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http.options;

import static org.jclouds.http.options.GetOptions.Builder.ifETagDoesntMatch;
import static org.jclouds.http.options.GetOptions.Builder.ifETagMatches;
import static org.jclouds.http.options.GetOptions.Builder.ifModifiedSince;
import static org.jclouds.http.options.GetOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.http.options.GetOptions.Builder.range;
import static org.jclouds.http.options.GetOptions.Builder.startAt;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Date;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests possible uses of GetOptions and GetOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetOptionsTest {

   private String etag;
   private Date now;
   private String nowExpected;

   @BeforeTest
   void setUp() {
      now = new Date();
      nowExpected = new SimpleDateFormatDateService().rfc822DateFormat(now);
      etag = "yrdy";
   }

   @Test
   public void testIfModifiedSince() {
      GetOptions options = new GetOptions();
      options.ifModifiedSince(now);
      assertEquals(options.getIfModifiedSince(), nowExpected);
   }

   @Test
   public void testNullIfModifiedSince() {
      GetOptions options = new GetOptions();
      assertNull(options.getIfModifiedSince());
   }

   @Test
   public void testIfModifiedSinceStatic() {
      GetOptions options = ifModifiedSince(now);
      assertEquals(options.getIfModifiedSince(), nowExpected);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfModifiedSinceNPE() {
      ifModifiedSince(null);
   }

   @Test
   public void testIfUnmodifiedSince() {
      GetOptions options = new GetOptions();
      options.ifUnmodifiedSince(now);
      isNowExpected(options);
   }

   @Test
   public void testNullIfUnmodifiedSince() {
      GetOptions options = new GetOptions();
      assertNull(options.getIfUnmodifiedSince());
   }

   @Test
   public void testIfUnmodifiedSinceStatic() {
      GetOptions options = ifUnmodifiedSince(now);
      isNowExpected(options);
   }

   private void isNowExpected(GetOptions options) {
      assertEquals(options.getIfUnmodifiedSince(), nowExpected);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfUnmodifiedSinceNPE() {
      ifUnmodifiedSince(null);
   }

   public void testModifiedSinceAndRange() {
      GetOptions options = new GetOptions();
      options.ifModifiedSince(now);
      options.range(0, 1024);
      assertEquals(options.getIfModifiedSince(), nowExpected);
      bytes1to1024(options);
   }

   @Test
   public void testRange() {
      GetOptions options = new GetOptions();
      options.range(0, 1024);
      bytes1to1024(options);
   }

   private void bytes1to1024(GetOptions options) {
      assertEquals(options.getRange(), "bytes=0-1024");
   }

   @Test
   public void testRangeZeroToFive() {
      GetOptions options = new GetOptions();
      options.range(0, 5);
      assertEquals(options.getRange(), "bytes=0-5");
   }

   @Test
   public void testTail() {
      GetOptions options = new GetOptions();
      options.tail(100);
      assertEquals(options.getRange(), "bytes=-100");
   }

   @Test
   public void testTailStatic() {
      GetOptions options = tail(100);
      assertEquals(options.getRange(), "bytes=-100");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testTailFail() {
      GetOptions options = new GetOptions();
      options.tail(0);
   }

   @Test
   public void testStartAt() {
      GetOptions options = new GetOptions();
      options.startAt(100);
      assertEquals(options.getRange(), "bytes=100-");
   }

   @Test
   public void testStartAtStatic() {
      GetOptions options = startAt(100);
      assertEquals(options.getRange(), "bytes=100-");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testStartAtFail() {
      GetOptions options = new GetOptions();
      options.startAt(-1);
   }

   @Test
   public void testRangeZeroToFiveAnd10through100() {
      GetOptions options = new GetOptions();
      options.range(0, 5).range(10, 100);
      assertEquals(options.getRange(), "bytes=0-5,10-100");
   }

   @Test
   public void testNullRange() {
      GetOptions options = new GetOptions();
      assertNull(options.getRange());
   }

   @Test
   public void testRangeStatic() {
      GetOptions options = range(0, 1024);
      bytes1to1024(options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative1() {
      range(-1, 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative2() {
      range(0, -1);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRangeNegative() {
      range(-1, -1);
   }

   @Test
   public void testIfETagMatches() {
      GetOptions options = new GetOptions();
      options.ifETagMatches(etag);
      matchesHex(options.getIfMatch());
   }

   @Test
   public void testNullIfETagMatches() {
      GetOptions options = new GetOptions();
      assertNull(options.getIfMatch());
   }

   @Test
   public void testIfETagMatchesStatic() {
      GetOptions options = ifETagMatches(etag);
      matchesHex(options.getIfMatch());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfETagMatchesNPE() {
      ifETagMatches(null);
   }

   @Test
   public void testIfETagDoesntMatch() {
      GetOptions options = new GetOptions();
      options.ifETagDoesntMatch(etag);
      matchesHex(options.getIfNoneMatch());
   }

   @Test
   public void testNullIfETagDoesntMatch() {
      GetOptions options = new GetOptions();
      assertNull(options.getIfNoneMatch());
   }

   @Test
   public void testIfETagDoesntMatchStatic() {
      GetOptions options = ifETagDoesntMatch(etag);
      matchesHex(options.getIfNoneMatch());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfETagDoesntMatchNPE() {
      ifETagDoesntMatch(null);
   }

   private void matchesHex(String match) {
      String expected = "\"" + etag + "\"";
      assertEquals(match, expected);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIfUnmodifiedAfterModified() {
      ifModifiedSince(now).ifUnmodifiedSince(now);

   }

   public void testIfUnmodifiedAfterETagMatches() {
      ifETagMatches(etag).ifUnmodifiedSince(now);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIfUnmodifiedAfterETagDoesntMatch() {
      ifETagDoesntMatch(etag).ifUnmodifiedSince(now);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIfModifiedAfterUnmodified() {
      ifUnmodifiedSince(now).ifModifiedSince(now);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIfModifiedAfterETagMatches() {
      ifETagMatches(etag).ifModifiedSince(now);

   }

   public void testIfModifiedAfterETagDoesntMatch() {
      ifETagDoesntMatch(etag).ifModifiedSince(now);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testETagMatchesAfterIfModified() {
      ifModifiedSince(now).ifETagMatches(etag);

   }

   public void testETagMatchesAfterIfUnmodified() {
      ifUnmodifiedSince(now).ifETagMatches(etag);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testETagMatchesAfterETagDoesntMatch() {
      ifETagDoesntMatch(etag).ifETagMatches(etag);
   }

   public void testETagDoesntMatchAfterIfModified() {
      ifModifiedSince(now).ifETagDoesntMatch(etag);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testETagDoesntMatchAfterIfUnmodified() {
      ifUnmodifiedSince(now).ifETagDoesntMatch(etag);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testETagDoesntMatchAfterETagMatches() {
      ifETagMatches(etag).ifETagDoesntMatch(etag);
   }

}
