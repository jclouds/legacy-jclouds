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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.highlyAvailable;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.hostTags;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.isSystem;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.limitCpuUse;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.networkRateInMb;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.storageType;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.systemVmType;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.tags;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.domain.SystemVmType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateServiceOfferingOptions}
 * 
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class CreateServiceOfferingOptionsTest {

   public void testHostTags() {
      CreateServiceOfferingOptions options =
         new CreateServiceOfferingOptions().hostTags(ImmutableSet.of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("hosttags"));
   }

   public void testHostTagsStatic() {
      CreateServiceOfferingOptions options = hostTags(ImmutableSet.of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("hosttags"));
   }

   public void testIsSystem() {
      CreateServiceOfferingOptions options = new CreateServiceOfferingOptions().isSystem(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("issystem"));
   }

   public void testIsSystemStatic() {
      CreateServiceOfferingOptions options = isSystem(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("issystem"));
   }

   public void testLimitCpuUse() {
      CreateServiceOfferingOptions options = new CreateServiceOfferingOptions().limitCpuUse(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("limitcpuuse"));
   }

   public void testLimitCpuUseStatic() {
      CreateServiceOfferingOptions options = limitCpuUse(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("limitcpuuse"));
   }

   public void testNetworkRate() {
      CreateServiceOfferingOptions options = new CreateServiceOfferingOptions().networkRateInMb(200);
      assertEquals(ImmutableSet.of("200"), options.buildQueryParameters().get("networkrate"));
   }

   public void testNetworkRateStatic() {
      CreateServiceOfferingOptions options = networkRateInMb(200);
      assertEquals(ImmutableSet.of("200"), options.buildQueryParameters().get("networkrate"));
   }

   public void testHighlyAvailable() {
      CreateServiceOfferingOptions options = new CreateServiceOfferingOptions().highlyAvailable(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("offerha"));
   }

   public void testHighlyAvailableStatic() {
      CreateServiceOfferingOptions options = highlyAvailable(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("offerha"));
   }

   public void testStorageType() {
      CreateServiceOfferingOptions options = new CreateServiceOfferingOptions().storageType(StorageType.LOCAL);
      assertEquals(ImmutableSet.of("local"), options.buildQueryParameters().get("storagetype"));
   }

   public void testStorageTypeStatic() {
      CreateServiceOfferingOptions options = storageType(StorageType.LOCAL);
      assertEquals(ImmutableSet.of("local"), options.buildQueryParameters().get("storagetype"));
   }

   public void testSystemVmType() {
      CreateServiceOfferingOptions options =
         new CreateServiceOfferingOptions().systemVmType(SystemVmType.DOMAIN_ROUTER);
      assertEquals(ImmutableSet.of("domainrouter"), options.buildQueryParameters().get("systemvmtype"));
   }

   public void testSystemVmTypeStatic() {
      CreateServiceOfferingOptions options = systemVmType(SystemVmType.DOMAIN_ROUTER);
      assertEquals(ImmutableSet.of("domainrouter"), options.buildQueryParameters().get("systemvmtype"));
   }

   public void testTags() {
      CreateServiceOfferingOptions options =
         new CreateServiceOfferingOptions().tags(ImmutableSet.<String>of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("tags"));
   }

   public void testTagsStatic() {
      CreateServiceOfferingOptions options = tags(ImmutableSet.<String>of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("tags"));
   }
}
