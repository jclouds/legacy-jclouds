/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.openstack.v2_0.options;

import static org.jclouds.openstack.v2_0.options.BaseListOptions.Builder.changesSince;
import static org.jclouds.openstack.v2_0.options.BaseListOptions.Builder.maxResults;
import static org.jclouds.openstack.v2_0.options.BaseListOptions.Builder.startAt;
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
public class BaseListOptionsTest {

   public void testChangesSince() {
      Date ifModifiedSince = new Date();
      BaseListOptions options = new BaseListOptions().changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAt() {
      long offset = 1;
      BaseListOptions options = new BaseListOptions().startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResults() {
      int limit = 1;
      BaseListOptions options = new BaseListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testChangesSinceStatic() {
      Date ifModifiedSince = new Date();
      BaseListOptions options = changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getTime() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAtStatic() {
      long offset = 1;
      BaseListOptions options = startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      BaseListOptions options = maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
