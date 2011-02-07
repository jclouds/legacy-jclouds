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

import static org.jclouds.cloudstack.options.ListDiskOfferingsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListDiskOfferingsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListDiskOfferingsOptions.Builder.name;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListDiskOfferingsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListDiskOfferingsOptionsTest {

   public void testId() {
      ListDiskOfferingsOptions options = new ListDiskOfferingsOptions().id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListDiskOfferingsOptions options = id("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("id"));
   }

   public void testName() {
      ListDiskOfferingsOptions options = new ListDiskOfferingsOptions().name("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListDiskOfferingsOptions options = name("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("name"));
   }

   public void testDomainId() {
      ListDiskOfferingsOptions options = new ListDiskOfferingsOptions().domainId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListDiskOfferingsOptions options = domainId("goo");
      assertEquals(ImmutableList.of("goo"), options.buildQueryParameters().get("domainid"));
   }

}
