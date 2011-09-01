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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.CreateImageOptions.Builder.noReboot;
import static org.jclouds.ec2.options.CreateImageOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CreateImageOptions and CreateImageOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class CreateImageOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(CreateImageOptions.class);
      assert !String.class.isAssignableFrom(CreateImageOptions.class);
   }

   @Test
   public void testWithDescription() {
      CreateImageOptions options = new CreateImageOptions();
      options.withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithDescription() {
      CreateImageOptions options = new CreateImageOptions();
      assertEquals(options.buildFormParameters().get("Description"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithDescriptionStatic() {
      CreateImageOptions options = withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDescriptionNPE() {
      withDescription(null);
   }

   @Test
   public void testNoReboot() {
      CreateImageOptions options = new CreateImageOptions();
      options.noReboot();
      assertEquals(options.buildFormParameters().get("NoReboot"), Collections.singletonList("true"));
   }

   @Test
   public void testNoRebootStatic() {
      CreateImageOptions options = noReboot();
      assertEquals(options.buildFormParameters().get("NoReboot"), Collections.singletonList("true"));
   }

}
