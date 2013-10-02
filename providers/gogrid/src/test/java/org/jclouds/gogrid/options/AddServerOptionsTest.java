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
package org.jclouds.gogrid.options;

import static org.jclouds.gogrid.options.AddServerOptions.Builder.asSandboxType;
import static org.jclouds.gogrid.options.AddServerOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of AddServerOptions and AddServerOptions.Builder.*
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AddServerOptionsTest")
public class AddServerOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(AddServerOptions.class);
      assert !String.class.isAssignableFrom(AddServerOptions.class);
   }

   @Test
   public void testWithDescription() {
      AddServerOptions options = new AddServerOptions();
      options.withDescription("test");
      assertEquals(options.buildQueryParameters().get("description"),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWith501LengthDescription() {
      AddServerOptions options = new AddServerOptions();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 1 * 501; i++)
         builder.append('a');

      String description = builder.toString();

      options.withDescription(description);

   }

   @Test
   public void testWith500LengthDescription() {
      AddServerOptions options = new AddServerOptions();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 1 * 500; i++)
         builder.append('a');

      String description = builder.toString();

      options.withDescription(description);
      assertEquals(options.buildQueryParameters().get("description"),
               ImmutableList.of(description));
   }

   @Test
   public void testNullWithDescription() {
      AddServerOptions options = new AddServerOptions();
      assertEquals(options.buildQueryParameters().get("description"), ImmutableList.of());
   }

   @Test
   public void testWithDescriptionStatic() {
      AddServerOptions options = withDescription("test");
      assertEquals(options.buildQueryParameters().get("description"),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDescriptionNPE() {
      withDescription(null);
   }

   @Test
   public void testAsSandboxType() {
      AddServerOptions options = new AddServerOptions();
      options.asSandboxType();
      assertEquals(options.buildQueryParameters().get("isSandbox"),
               ImmutableList.of("true"));
   }

   @Test
   public void testAsSandboxTypeStatic() {
      AddServerOptions options = asSandboxType();
      assertEquals(options.buildQueryParameters().get("isSandbox"),
               ImmutableList.of("true"));
   }

}
