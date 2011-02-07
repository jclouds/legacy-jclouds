/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.availability;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.displayText;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.isDefault;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.isShared;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.specifyVLAN;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.trafficType;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.TrafficType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListNetworkOfferingsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListNetworkOfferingsOptionsTest {

   public void testIsDefault() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }

   public void testIsDefaultStatic() {
      ListNetworkOfferingsOptions options = isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }


   public void testIsShared() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testIsSharedStatic() {
      ListNetworkOfferingsOptions options = isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testId() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListNetworkOfferingsOptions options = id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testSpecifyVLAN() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().specifyVLAN(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("specifyvlan"));
   }

   public void testSpecifyVLANStatic() {
      ListNetworkOfferingsOptions options = specifyVLAN(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("specifyvlan"));
   }

   public void testAvailability() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().availability("moo");
      assertEquals(ImmutableList.of("moo"), options.buildQueryParameters().get("availability"));
   }

   public void testAvailabilityStatic() {
      ListNetworkOfferingsOptions options = availability("moo");
      assertEquals(ImmutableList.of("moo"), options.buildQueryParameters().get("availability"));
   }

   public void testTrafficType() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().trafficType(TrafficType.GUEST);
      assertEquals(ImmutableList.of("Guest"), options.buildQueryParameters().get("traffictype"));
   }

   public void testTrafficTypeStatic() {
      ListNetworkOfferingsOptions options = trafficType(TrafficType.GUEST);
      assertEquals(ImmutableList.of("Guest"), options.buildQueryParameters().get("traffictype"));
   }

   public void testName() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testNameStatic() {
      ListNetworkOfferingsOptions options = id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testDisplayText() {
      ListNetworkOfferingsOptions options = new ListNetworkOfferingsOptions().displayText("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("displaytext"));
   }

   public void testDisplayTextStatic() {
      ListNetworkOfferingsOptions options = displayText("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("displaytext"));
   }

}
