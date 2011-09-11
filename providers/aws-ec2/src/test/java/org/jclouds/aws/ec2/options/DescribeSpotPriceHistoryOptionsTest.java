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

import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.from;
import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.instanceType;
import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.productDescription;
import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.to;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of DescribeSpotPriceHistoryOptions and
 * DescribeSpotPriceHistoryOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class DescribeSpotPriceHistoryOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(DescribeSpotPriceHistoryOptions.class);
      assert !String.class.isAssignableFrom(DescribeSpotPriceHistoryOptions.class);
   }

   @Test
   public void testDescription() {
      DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
      options.productDescription("test");
      assertEquals(options.buildFormParameters().get("ProductDescription"), Collections.singletonList("test"));
   }

   @Test
   public void testDescriptionStatic() {
      DescribeSpotPriceHistoryOptions options = productDescription("test");
      assertEquals(options.buildFormParameters().get("ProductDescription"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testDescriptionNPE() {
      productDescription(null);
   }

   @Test
   public void testInstanceType() {
      DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
      options.instanceType("test");
      assertEquals(options.buildFormParameters().get("InstanceType.1"), Collections.singletonList("test"));
   }

   @Test
   public void testInstanceTypeStatic() {
      DescribeSpotPriceHistoryOptions options = instanceType("test");
      assertEquals(options.buildFormParameters().get("InstanceType.1"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInstanceTypeNPE() {
      instanceType(null);
   }

   @Test
   public void testFrom() {
      DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
      options.from(test);
      assertEquals(options.buildFormParameters().get("StartTime"), Collections.singletonList("1970-05-23T21:21:18.910Z"));
   }

   Date test = new Date(12345678910l);

   @Test
   public void testFromStatic() {
      DescribeSpotPriceHistoryOptions options = from(test);
      assertEquals(options.buildFormParameters().get("StartTime"), Collections.singletonList("1970-05-23T21:21:18.910Z"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFromNPE() {
      from(null);
   }

   @Test
   public void testTo() {
      DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
      options.to(test);
      assertEquals(options.buildFormParameters().get("EndTime"), Collections.singletonList("1970-05-23T21:21:18.910Z"));
   }

   @Test
   public void testToStatic() {
      DescribeSpotPriceHistoryOptions options = to(test);
      assertEquals(options.buildFormParameters().get("EndTime"), Collections.singletonList("1970-05-23T21:21:18.910Z"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testToNPE() {
      to(null);
   }

}
