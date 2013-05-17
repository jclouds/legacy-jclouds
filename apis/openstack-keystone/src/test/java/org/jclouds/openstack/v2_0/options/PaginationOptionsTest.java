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
package org.jclouds.openstack.v2_0.options;

import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.changesSince;
import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.limit;
import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.marker;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PaginationOptionsTest")
public class PaginationOptionsTest {

   public void testChangesSince() {
      Date ifModifiedSince = new Date();
      PaginationOptions options = new PaginationOptions().changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""),
               options.buildQueryParameters().get("changes-since"));
   }

   public void testMarker() {
      String marker = "52415800-8b69-11e0-9b19-734f6f006e54";
      PaginationOptions options = new PaginationOptions().marker(marker);
      assertEquals(ImmutableList.of(marker), options.buildQueryParameters().get("marker"));
   }

   public void testMaxResults() {
      int limit = 1;
      PaginationOptions options = new PaginationOptions().limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testChangesSinceStatic() {
      Date ifModifiedSince = new Date();
      PaginationOptions options = changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""),
               options.buildQueryParameters().get("changes-since"));
   }

   public void testMarkerStatic() {
      String marker = "52415800-8b69-11e0-9b19-734f6f006e54";
      PaginationOptions options = marker(marker);
      assertEquals(ImmutableList.of(marker), options.buildQueryParameters().get("marker"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      PaginationOptions options = limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
