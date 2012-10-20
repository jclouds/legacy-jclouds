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
package org.jclouds.snia.cdmi.v1.options;

import static org.jclouds.snia.cdmi.v1.options.ListContainersOptions.Builder.limit;
import static org.jclouds.snia.cdmi.v1.options.ListContainersOptions.Builder.marker;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of ListContainerOptions and ListContainerOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(testName = "ListContainersOptionsTest")
public class ListContainersOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListContainersOptions.class);
      assert !String.class.isAssignableFrom(ListContainersOptions.class);
   }

   @Test
   public void testNoOptionsQueryString() {
      HttpRequestOptions options = new ListContainersOptions();
      assertEquals(options.buildQueryParameters().size(), 0);
   }

   @Test
   public void testMarker() {
      ListContainersOptions options = new ListContainersOptions();
      options.marker("test");
      assertEquals(options.buildQueryParameters().get("marker"), ImmutableList.of("test"));
   }

   @Test
   public void testNullMarker() {
      ListContainersOptions options = new ListContainersOptions();
      assertEquals(options.buildQueryParameters().get("marker"), ImmutableList.of());
   }

   @Test
   public void testMarkerStatic() {
      ListContainersOptions options = marker("test");
      assertEquals(options.buildQueryParameters().get("marker"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() {
      marker(null);
   }

   @Test
   public void testLimit() {
      ListContainersOptions options = new ListContainersOptions();
      options.limit(1000);
      assertEquals(options.buildQueryParameters().get("limit"), ImmutableList.of("1000"));
   }

   @Test
   public void testNullLimit() {
      ListContainersOptions options = new ListContainersOptions();
      assertEquals(options.buildQueryParameters().get("limit"), ImmutableList.of());
   }

   @Test
   public void testLimitStatic() {
      ListContainersOptions options = limit(1000);
      assertEquals(options.buildQueryParameters().get("limit"), ImmutableList.of("1000"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testLimitNegative() {
      limit(-1);
   }
}
