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
package org.jclouds.cloudservers.options;

import static org.jclouds.cloudservers.options.ListOptions.Builder.changesSince;
import static org.jclouds.cloudservers.options.ListOptions.Builder.maxResults;
import static org.jclouds.cloudservers.options.ListOptions.Builder.startAt;
import static org.jclouds.cloudservers.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListOptionsTest {

   public void testWithDetails() {
      ListOptions options = new ListOptions().withDetails();
      assertEquals(options.buildPathSuffix(), "/detail");
   }

   public void testWithDetailsStatic() {
      ListOptions options = withDetails();
      assertEquals(options.buildPathSuffix(), "/detail");
   }

   public void testChangesSince() {
      Date ifModifiedSince = new Date();
      ListOptions options = new ListOptions().changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAt() {
      long offset = 1;
      ListOptions options = new ListOptions().startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResults() {
      int limit = 1;
      ListOptions options = new ListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testChangesSinceStatic() {
      Date ifModifiedSince = new Date();
      ListOptions options = changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAtStatic() {
      long offset = 1;
      ListOptions options = startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      ListOptions options = maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
