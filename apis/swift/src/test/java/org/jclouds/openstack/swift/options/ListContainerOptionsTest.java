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
package org.jclouds.openstack.swift.options;

import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.afterMarker;
import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.maxResults;
import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.underPath;
import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.openstack.swift.reference.SwiftConstants;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Tests possible uses of ListContainerOptions and ListContainerOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListContainerOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListContainerOptions.class);
      assert !String.class.isAssignableFrom(ListContainerOptions.class);
   }

   @Test
   public void testPrefix() {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PREFIX), ImmutableList.of("test"));
   }

   @Test
   public void testNoOptionsQueryString() {
      HttpRequestOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().size(), 0);
   }

   @Test
   public void testOneOptionQueryString() {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 1);
      assertEquals(map.get("prefix"), ImmutableList.of("test"));
   }

   @Test
   public void testTwoOptionQueryString() {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test").maxResults(1);
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), ImmutableList.of("test"));
      assertEquals(map.get("limit"), ImmutableList.of("1"));
   }

   @Test
   public void testPrefixAndPathUrlEncodingQueryString() {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("/cloudfiles/test").underPath("/");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), ImmutableList.of("/cloudfiles/test"));
      assertEquals(map.get("path"), ImmutableList.of("/"));

   }

   @Test
   public void testNullPrefix() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PREFIX), ImmutableList.of());
   }

   @Test
   public void testPrefixStatic() {
      ListContainerOptions options = withPrefix("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PREFIX), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPrefixNPE() {
      withPrefix(null);
   }

   @Test
   public void testMarker() {
      ListContainerOptions options = new ListContainerOptions();
      options.afterMarker("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.MARKER), ImmutableList.of("test"));
   }

   @Test
   public void testNullMarker() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(SwiftConstants.MARKER), ImmutableList.of());
   }

   @Test
   public void testMarkerStatic() {
      ListContainerOptions options = afterMarker("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.MARKER), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() {
      afterMarker(null);
   }

   @Test
   public void testMaxKeys() {
      ListContainerOptions options = new ListContainerOptions();
      options.maxResults(1000);
      assertEquals(options.buildQueryParameters().get(SwiftConstants.LIMIT), ImmutableList.of("1000"));
   }

   @Test
   public void testNullMaxKeys() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(SwiftConstants.LIMIT), ImmutableList.of());
   }

   @Test
   public void testMaxKeysStatic() {
      ListContainerOptions options = maxResults(1000);
      assertEquals(options.buildQueryParameters().get(SwiftConstants.LIMIT), ImmutableList.of("1000"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMaxKeysNegative() {
      maxResults(-1);
   }

   @Test
   public void testPath() {
      ListContainerOptions options = new ListContainerOptions();
      options.underPath("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PATH), ImmutableList.of("test"));
   }

   @Test
   public void testNullPath() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PATH), ImmutableList.of());
   }

   @Test
   public void testPathStatic() {
      ListContainerOptions options = underPath("test");
      assertEquals(options.buildQueryParameters().get(SwiftConstants.PATH), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPathNPE() {
      underPath(null);
   }
}
