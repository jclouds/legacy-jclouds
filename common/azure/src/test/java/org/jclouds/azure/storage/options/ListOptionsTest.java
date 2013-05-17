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
package org.jclouds.azure.storage.options;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListOptionsTest {
   public void testIncludeMetadata() {
      ListOptions options = new ListOptions().includeMetadata();
      assertEquals(ImmutableList.of("metadata"), options.buildQueryParameters().get("include"));
   }

   public void testIncludeMetadataStatic() {
      ListOptions options = ListOptions.Builder.includeMetadata();
      assertEquals(ImmutableList.of("metadata"), options.buildQueryParameters().get("include"));
   }

   public void testPrefix() {
      ListOptions options = new ListOptions().prefix("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("prefix"));
   }

   public void testMarker() {
      ListOptions options = new ListOptions().marker("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("marker"));
   }

   public void testMaxResults() {
      int limit = 1;
      ListOptions options = new ListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("maxresults"));
   }

   public void testPrefixStatic() {
      ListOptions options = ListOptions.Builder.prefix("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("prefix"));
   }

   public void testMarkerStatic() {
      ListOptions options = ListOptions.Builder.marker("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("marker"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      ListOptions options = ListOptions.Builder.maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("maxresults"));
   }
}
