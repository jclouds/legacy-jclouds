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
package org.jclouds.s3.options;

import static org.jclouds.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.s3.reference.S3Constants;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Tests possible uses of ListBucketOptions and ListBucketOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListBucketOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListBucketOptions.class);
      assert !String.class.isAssignableFrom(ListBucketOptions.class);
   }

   @Test
   public void testPrefix() {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX),
               ImmutableList.of("test"));
   }

   @Test
   public void testNoOptionsQueryString() {
      HttpRequestOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().size(), 0);
   }

   @Test
   public void testOneOptionQueryString() {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 1);
      assertEquals(map.get("prefix"), ImmutableList.of("test"));
   }

   @Test
   public void testTwoOptionQueryString() {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test").maxResults(1);
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), ImmutableList.of("test"));
      assertEquals(map.get("max-keys"), ImmutableList.of("1"));
   }

   @Test
   public void testPrefixAndDelimiterUrlEncodingQueryString() {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("/test").delimiter("/");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), ImmutableList.of("/test"));
      assertEquals(map.get("delimiter"), ImmutableList.of("/"));

   }

   @Test
   public void testNullPrefix() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX), ImmutableList.of());
   }

   @Test
   public void testPrefixStatic() {
      ListBucketOptions options = withPrefix("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPrefixNPE() {
      withPrefix(null);
   }

   @Test
   public void testMarker() {
      ListBucketOptions options = new ListBucketOptions();
      options.afterMarker("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER),
               ImmutableList.of("test"));
   }

   @Test
   public void testNullMarker() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER), ImmutableList.of());
   }

   @Test
   public void testMarkerStatic() {
      ListBucketOptions options = afterMarker("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() {
      afterMarker(null);
   }

   @Test
   public void testMaxKeys() {
      ListBucketOptions options = new ListBucketOptions();
      options.maxResults(1000);
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS),
               ImmutableList.of("1000"));
   }

   @Test
   public void testNullMaxKeys() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS), ImmutableList.of());
   }

   @Test
   public void testMaxKeysStatic() {
      ListBucketOptions options = maxResults(1000);
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS),
               ImmutableList.of("1000"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMaxKeysNegative() {
      maxResults(-1);
   }

   @Test
   public void testDelimiter() {
      ListBucketOptions options = new ListBucketOptions();
      options.delimiter("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER),
               ImmutableList.of("test"));
   }

   @Test
   public void testNullDelimiter() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER),
               ImmutableList.of());
   }

   @Test
   public void testDelimiterStatic() {
      ListBucketOptions options = delimiter("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testDelimiterNPE() {
      delimiter(null);
   }
}
