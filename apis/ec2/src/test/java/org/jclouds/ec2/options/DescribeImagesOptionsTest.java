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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.ownedBy;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of DescribeImagesOptions and DescribeImagesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class DescribeImagesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(DescribeImagesOptions.class);
      assert !String.class.isAssignableFrom(DescribeImagesOptions.class);
   }

   @Test
   public void testExecutableBy() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"),
               ImmutableList.of("test"));
   }

   @Test
   public void testNullExecutableBy() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ExecutableBy"), ImmutableList.of());
   }

   @Test
   public void testExecutableByStatic() {
      DescribeImagesOptions options = executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"),
               ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExecutableByNPE() {
      executableBy(null);
   }

   @Test
   public void testOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
   }

   @Test
   public void testMultipleOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.ownedBy("test", "trouble");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
      assertEquals(options.buildFormParameters().get("Owner.2"),
               ImmutableList.of("trouble"));
   }

   @Test
   public void testNullOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of());
   }

   @Test
   public void testOwnersStatic() {
      DescribeImagesOptions options = ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
   }

   public void testNoOwners() {
      ownedBy();
   }

   @Test
   public void testImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"),
               ImmutableList.of("test"));
   }

   @Test
   public void testMultipleImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.imageIds("test", "trouble");
      assertEquals(options.buildFormParameters().get("ImageId.1"),
               ImmutableList.of("test"));
      assertEquals(options.buildFormParameters().get("ImageId.2"),
               ImmutableList.of("trouble"));
   }

   @Test
   public void testNullImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ImageId.1"), ImmutableList.of());
   }

   @Test
   public void testImageIdsStatic() {
      DescribeImagesOptions options = imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"),
               ImmutableList.of("test"));
   }

   public void testNoImageIds() {
      imageIds();
   }
}
