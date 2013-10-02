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
package org.jclouds.rackspace.cloudidentity.v2_0.options;

import static org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions.Builder.limit;
import static org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions.Builder.name;
import static org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions.Builder.offset;
import static org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions.Builder.queryParameters;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * @author Everett Toews
 */
@Test(groups = "unit", testName = "PaginationOptionsTest")
public class PaginationOptionsTest {

   public void testQueryParameters() {
      ImmutableSetMultimap<String, String> queryParameters = ImmutableSetMultimap.<String, String> of(
            "limit", "1",
            "offset", "1",
            "name", "foo");
      PaginationOptions options = new PaginationOptions().queryParameters(queryParameters);
      assertEquals(queryParameters, options.buildQueryParameters());
   }

   public void testMaxResults() {
      int limit = 1;
      PaginationOptions options = new PaginationOptions().limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testOffset() {
      int offset = 1;
      PaginationOptions options = new PaginationOptions().offset(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testNameFilter() {
      String nameFilter = "foo";
      PaginationOptions options = new PaginationOptions().name(nameFilter);
      assertEquals(ImmutableList.of("foo"), options.buildQueryParameters().get("name"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      PaginationOptions options = limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testOffsetStatic() {
      int offset = 1;
      PaginationOptions options = offset(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testNameFilterStatic() {
      String nameFilter = "foo";
      PaginationOptions options = name(nameFilter);
      assertEquals(ImmutableList.of("foo"), options.buildQueryParameters().get("name"));
   }

   public void testQueryParametersStatic() {
      ImmutableSetMultimap<String, String> queryParameters = ImmutableSetMultimap.<String, String> of(
            "limit", "1",
            "offset", "1",
            "name", "foo");
      PaginationOptions options = queryParameters(queryParameters);
      assertEquals(queryParameters, options.buildQueryParameters());
   }
}
