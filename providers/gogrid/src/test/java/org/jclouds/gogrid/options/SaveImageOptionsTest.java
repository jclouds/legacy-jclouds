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

import static org.jclouds.gogrid.options.SaveImageOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of SaveImageOptions and SaveImageOptions.Builder.*
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SaveImageOptionsTest")
public class SaveImageOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(SaveImageOptions.class);
      assert !String.class.isAssignableFrom(SaveImageOptions.class);
   }

   @Test
   public void testWithDescription() {
      SaveImageOptions options = new SaveImageOptions();
      options.withDescription("test");
      assertEquals(options.buildQueryParameters().get("description"),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWith501LengthDescription() {
      SaveImageOptions options = new SaveImageOptions();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 1 * 501; i++)
         builder.append('a');

      String description = builder.toString();

      options.withDescription(description);

   }

   @Test
   public void testWith500LengthDescription() {
      SaveImageOptions options = new SaveImageOptions();
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
      SaveImageOptions options = new SaveImageOptions();
      assertEquals(options.buildQueryParameters().get("description"), ImmutableList.of());
   }

   @Test
   public void testWithDescriptionStatic() {
      SaveImageOptions options = withDescription("test");
      assertEquals(options.buildQueryParameters().get("description"),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDescriptionNPE() {
      withDescription(null);
   }

}
