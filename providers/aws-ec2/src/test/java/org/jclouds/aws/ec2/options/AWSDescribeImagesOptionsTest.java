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
package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.executableBy;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.imageIds;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.ownedBy;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests possible uses of AWSDescribeImagesOptions and AWSDescribeImagesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class AWSDescribeImagesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(AWSDescribeImagesOptions.class);
      assert !String.class.isAssignableFrom(AWSDescribeImagesOptions.class);
   }

   @Test
   public void testExecutableBy() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"), ImmutableList.of("test"));
   }

   @Test
   public void testNullExecutableBy() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ExecutableBy"), ImmutableList.of());
   }

   @Test
   public void testExecutableByStatic() {
      AWSDescribeImagesOptions options = executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"), ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExecutableByNPE() {
      executableBy(null);
   }

   @Test
   public void testOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
   }

   @Test
   public void testMultipleOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.ownedBy("test", "trouble");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
      assertEquals(options.buildFormParameters().get("Owner.2"), ImmutableList.of("trouble"));
   }

   @Test
   public void testNullOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of());
   }

   @Test
   public void testOwnersStatic() {
      AWSDescribeImagesOptions options = ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), ImmutableList.of("test"));
   }

   public void testNoOwners() {
      ownedBy();
   }

   @Test
   public void testImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), ImmutableList.of("test"));
   }

   @Test
   public void testMultipleImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.imageIds("test", "trouble");
      assertEquals(options.buildFormParameters().get("ImageId.1"), ImmutableList.of("test"));
      assertEquals(options.buildFormParameters().get("ImageId.2"), ImmutableList.of("trouble"));
   }

   @Test
   public void testNullImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ImageId.1"), ImmutableList.of());
   }

   @Test
   public void testImageIdsStatic() {
      AWSDescribeImagesOptions options = imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), ImmutableList.of("test"));
   }

   public void testNoImageIds() {
      imageIds();
   }

   @Test
   public void testMapFilters() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.filters(ImmutableMap.of("is-public", "true", "architecture", "x86_64", "platform", "windows"));
      testMapFilters(options);
   }

   private void testMapFilters(AWSDescribeImagesOptions options) {
      assertEquals(options.buildFormParameters().get("Filter.1.Name"), ImmutableList.of("is-public"));
      assertEquals(options.buildFormParameters().get("Filter.1.Value.1"), ImmutableList.of("true"));
      assertEquals(options.buildFormParameters().get("Filter.2.Name"), ImmutableList.of("architecture"));
      assertEquals(options.buildFormParameters().get("Filter.2.Value.1"), ImmutableList.of("x86_64"));
      assertEquals(options.buildFormParameters().get("Filter.3.Name"), ImmutableList.of("platform"));
      assertEquals(options.buildFormParameters().get("Filter.3.Value.1"), ImmutableList.of("windows"));
   }

   @Test
   public void testMapFiltersStatic() {
      AWSDescribeImagesOptions options = filters(ImmutableMap.of("is-public", "true", "architecture", "x86_64",
               "platform", "windows"));
      testMapFilters(options);
   }
   

   @Test
   public void testMultimapFilters() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.filters(ImmutableMultimap.of("is-public", "true", "architecture", "x86_64", "platform", "windows"));
      testMultimapFilters(options);
   }

   private void testMultimapFilters(AWSDescribeImagesOptions options) {
      assertEquals(options.buildFormParameters().get("Filter.1.Name"), ImmutableList.of("is-public"));
      assertEquals(options.buildFormParameters().get("Filter.1.Value.1"), ImmutableList.of("true"));
      assertEquals(options.buildFormParameters().get("Filter.2.Name"), ImmutableList.of("architecture"));
      assertEquals(options.buildFormParameters().get("Filter.2.Value.1"), ImmutableList.of("x86_64"));
      assertEquals(options.buildFormParameters().get("Filter.3.Name"), ImmutableList.of("platform"));
      assertEquals(options.buildFormParameters().get("Filter.3.Value.1"), ImmutableList.of("windows"));
   }

   @Test
   public void testMultimapFiltersStatic() {
      AWSDescribeImagesOptions options = filters(ImmutableMultimap.of("is-public", "true", "architecture", "x86_64",
               "platform", "windows"));
      testMultimapFilters(options);
   }
}
