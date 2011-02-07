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

import static org.jclouds.cloudstack.options.ListServiceOfferingsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListServiceOfferingsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListServiceOfferingsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListServiceOfferingsOptions.Builder.virtualMachineId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListServiceOfferingsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListServiceOfferingsOptionsTest {

   public void testId() {
      ListServiceOfferingsOptions options = new ListServiceOfferingsOptions().id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListServiceOfferingsOptions options = id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testName() {
      ListServiceOfferingsOptions options = new ListServiceOfferingsOptions().name("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListServiceOfferingsOptions options = name("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("name"));
   }

   public void testDomainId() {
      ListServiceOfferingsOptions options = new ListServiceOfferingsOptions().domainId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListServiceOfferingsOptions options = domainId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("domainid"));
   }

   public void testVirtualMachineId() {
      ListServiceOfferingsOptions options = new ListServiceOfferingsOptions().virtualMachineId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testVirtualMachineIdStatic() {
      ListServiceOfferingsOptions options = virtualMachineId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("virtualmachineid"));
   }
}
