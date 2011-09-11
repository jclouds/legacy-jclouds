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
package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.executableBy;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.imageIds;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.ownedBy;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

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
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections.singletonList("test"));
   }

   @Test
   public void testNullExecutableBy() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections.EMPTY_LIST);
   }

   @Test
   public void testExecutableByStatic() {
      AWSDescribeImagesOptions options = executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExecutableByNPE() {
      executableBy(null);
   }

   @Test
   public void testOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   @Test
   public void testMultipleOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.ownedBy("test", "trouble");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
      assertEquals(options.buildFormParameters().get("Owner.2"), Collections.singletonList("trouble"));
   }

   @Test
   public void testNullOwners() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testOwnersStatic() {
      AWSDescribeImagesOptions options = ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   public void testNoOwners() {
      ownedBy();
   }

   @Test
   public void testImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections.singletonList("test"));
   }

   @Test
   public void testMultipleImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      options.imageIds("test", "trouble");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections.singletonList("test"));
      assertEquals(options.buildFormParameters().get("ImageId.2"), Collections.singletonList("trouble"));
   }

   @Test
   public void testNullImageIds() {
      AWSDescribeImagesOptions options = new AWSDescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testImageIdsStatic() {
      AWSDescribeImagesOptions options = imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections.singletonList("test"));
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
      assertEquals(options.buildFormParameters().get("Filter.1.Name"), Collections.singletonList("is-public"));
      assertEquals(options.buildFormParameters().get("Filter.1.Value.1"), Collections.singletonList("true"));
      assertEquals(options.buildFormParameters().get("Filter.2.Name"), Collections.singletonList("architecture"));
      assertEquals(options.buildFormParameters().get("Filter.2.Value.1"), Collections.singletonList("x86_64"));
      assertEquals(options.buildFormParameters().get("Filter.3.Name"), Collections.singletonList("platform"));
      assertEquals(options.buildFormParameters().get("Filter.3.Value.1"), Collections.singletonList("windows"));
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
      assertEquals(options.buildFormParameters().get("Filter.1.Name"), Collections.singletonList("is-public"));
      assertEquals(options.buildFormParameters().get("Filter.1.Value.1"), Collections.singletonList("true"));
      assertEquals(options.buildFormParameters().get("Filter.2.Name"), Collections.singletonList("architecture"));
      assertEquals(options.buildFormParameters().get("Filter.2.Value.1"), Collections.singletonList("x86_64"));
      assertEquals(options.buildFormParameters().get("Filter.3.Name"), Collections.singletonList("platform"));
      assertEquals(options.buildFormParameters().get("Filter.3.Value.1"), Collections.singletonList("windows"));
   }

   @Test
   public void testMultimapFiltersStatic() {
      AWSDescribeImagesOptions options = filters(ImmutableMultimap.of("is-public", "true", "architecture", "x86_64",
               "platform", "windows"));
      testMultimapFilters(options);
   }
}
