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

import static org.jclouds.ec2.options.RegisterImageOptions.Builder.asArchitecture;
import static org.jclouds.ec2.options.RegisterImageOptions.Builder.withDescription;
import static org.jclouds.ec2.options.RegisterImageOptions.Builder.withKernelId;
import static org.jclouds.ec2.options.RegisterImageOptions.Builder.withRamdisk;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of RegisterImageOptions and RegisterImageOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class RegisterImageOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(RegisterImageOptions.class);
      assert !String.class.isAssignableFrom(RegisterImageOptions.class);
   }

   @Test
   public void testWithDescription() {
      RegisterImageOptions options = new RegisterImageOptions();
      options.withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithDescription() {
      RegisterImageOptions options = new RegisterImageOptions();
      assertEquals(options.buildFormParameters().get("Description"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithDescriptionStatic() {
      RegisterImageOptions options = withDescription("test");
      assertEquals(options.buildFormParameters().get("Description"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDescriptionNPE() {
      withDescription(null);
   }

   @Test
   public void testWithArchitecture() {
      RegisterImageOptions options = new RegisterImageOptions();
      options.asArchitecture(Architecture.I386);
      assertEquals(options.buildFormParameters().get("Architecture"), Collections
               .singletonList("i386"));
   }

   @Test
   public void testNullWithArchitecture() {
      RegisterImageOptions options = new RegisterImageOptions();
      assertEquals(options.buildFormParameters().get("Architecture"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithArchitectureStatic() {
      RegisterImageOptions options = asArchitecture(Architecture.I386);
      assertEquals(options.buildFormParameters().get("Architecture"), Collections
               .singletonList("i386"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithArchitectureNPE() {
      asArchitecture(null);
   }

   @Test
   public void testWithKernelId() {
      RegisterImageOptions options = new RegisterImageOptions();
      options.withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithKernelId() {
      RegisterImageOptions options = new RegisterImageOptions();
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKernelIdStatic() {
      RegisterImageOptions options = withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKernelIdNPE() {
      withKernelId(null);
   }

   @Test
   public void testWithRamdisk() {
      RegisterImageOptions options = new RegisterImageOptions();
      options.withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithRamdisk() {
      RegisterImageOptions options = new RegisterImageOptions();
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithRamdiskStatic() {
      RegisterImageOptions options = withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithRamdiskNPE() {
      withRamdisk(null);
   }

}
