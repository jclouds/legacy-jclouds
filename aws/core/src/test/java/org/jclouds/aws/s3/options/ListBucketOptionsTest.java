/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.s3.options;

import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.afterMarker;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.delimiter;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.maxResults;
import static org.jclouds.aws.s3.options.ListBucketOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

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
   public void testPrefix() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX), Collections
               .singletonList("test"));
   }

   @Test
   public void testNoOptionsQueryString() {
      HttpRequestOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().size(), 0);
   }

   @Test
   public void testOneOptionQueryString() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 1);
      assertEquals(map.get("prefix"), Collections.singletonList("test"));
   }

   @Test
   public void testTwoOptionQueryString() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("test").maxResults(1);
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), Collections.singletonList("test"));
      assertEquals(map.get("max-keys"), Collections.singletonList("1"));
   }

   @Test
   public void testPrefixAndDelimiterUrlEncodingQueryString() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.withPrefix("/test").delimiter("/");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), Collections.singletonList("/test"));
      assertEquals(map.get("delimiter"), Collections.singletonList("/"));

   }

   @Test
   public void testNullPrefix() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX), Collections.EMPTY_LIST);
   }

   @Test
   public void testPrefixStatic() throws UnsupportedEncodingException {
      ListBucketOptions options = withPrefix("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.PREFIX), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPrefixNPE() throws UnsupportedEncodingException {
      withPrefix(null);
   }

   @Test
   public void testMarker() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.afterMarker("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullMarker() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER), Collections.EMPTY_LIST);
   }

   @Test
   public void testMarkerStatic() throws UnsupportedEncodingException {
      ListBucketOptions options = afterMarker("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.MARKER), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() throws UnsupportedEncodingException {
      afterMarker(null);
   }

   @Test
   public void testMaxKeys() {
      ListBucketOptions options = new ListBucketOptions();
      options.maxResults(1000);
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS), Collections
               .singletonList("1000"));
   }

   @Test
   public void testNullMaxKeys() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS), Collections.EMPTY_LIST);
   }

   @Test
   public void testMaxKeysStatic() {
      ListBucketOptions options = maxResults(1000);
      assertEquals(options.buildQueryParameters().get(S3Constants.MAX_KEYS), Collections
               .singletonList("1000"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMaxKeysNegative() {
      maxResults(-1);
   }

   @Test
   public void testDelimiter() throws UnsupportedEncodingException {
      ListBucketOptions options = new ListBucketOptions();
      options.delimiter("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullDelimiter() {
      ListBucketOptions options = new ListBucketOptions();
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER),
               Collections.EMPTY_LIST);
   }

   @Test
   public void testDelimiterStatic() throws UnsupportedEncodingException {
      ListBucketOptions options = delimiter("test");
      assertEquals(options.buildQueryParameters().get(S3Constants.DELIMITER), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testDelimiterNPE() throws UnsupportedEncodingException {
      delimiter(null);
   }
}
