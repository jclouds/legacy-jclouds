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

import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.isDefault;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.isShared;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.isSystem;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.trafficType;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.type;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.TrafficType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListNetworksOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListNetworksOptionsTest {

   public void testIsDefault() {
      ListNetworksOptions options = new ListNetworksOptions().isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }

   public void testIsDefaultStatic() {
      ListNetworksOptions options = isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }

   public void testIsSystem() {
      ListNetworksOptions options = new ListNetworksOptions().isSystem(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("issystem"));
   }

   public void testIsSystemStatic() {
      ListNetworksOptions options = isSystem(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("issystem"));
   }

   public void testIsShared() {
      ListNetworksOptions options = new ListNetworksOptions().isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testIsSharedStatic() {
      ListNetworksOptions options = isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testId() {
      ListNetworksOptions options = new ListNetworksOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListNetworksOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainId() {
      ListNetworksOptions options = new ListNetworksOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListNetworksOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountId() {
      ListNetworksOptions options = new ListNetworksOptions().accountInDomain("moo", "1");
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountIdStatic() {
      ListNetworksOptions options = accountInDomain("moo", "1");
      assertEquals(ImmutableList.of("moo"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("domainid"));
   }

   public void testTrafficType() {
      ListNetworksOptions options = new ListNetworksOptions().trafficType(TrafficType.GUEST);
      assertEquals(ImmutableList.of("Guest"), options.buildQueryParameters().get("traffictype"));
   }

   public void testTrafficTypeStatic() {
      ListNetworksOptions options = trafficType(TrafficType.GUEST);
      assertEquals(ImmutableList.of("Guest"), options.buildQueryParameters().get("traffictype"));
   }

   public void testName() {
      ListNetworksOptions options = new ListNetworksOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testNameStatic() {
      ListNetworksOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testZoneId() {
      ListNetworksOptions options = new ListNetworksOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListNetworksOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testType() {
      ListNetworksOptions options = new ListNetworksOptions().type(NetworkType.ADVANCED);
      assertEquals(ImmutableList.of("Advanced"), options.buildQueryParameters().get("type"));
   }

   public void testTypeStatic() {
      ListNetworksOptions options = type(NetworkType.ADVANCED);
      assertEquals(ImmutableList.of("Advanced"), options.buildQueryParameters().get("type"));
   }
}
