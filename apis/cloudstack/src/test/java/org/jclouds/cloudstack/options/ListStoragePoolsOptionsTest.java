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

import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.clusterId;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.ipAddress;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.path;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListStoragePoolsOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Test for the ListStoragePoolsOptions class.
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListStoragePoolsOptionsTest {

   public void testClusterId() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().clusterId("14");
      assertEquals(ImmutableList.of(14 + ""), options.buildQueryParameters().get("clusterid"));
   }

   public void testClusterIdStatic() {
      ListStoragePoolsOptions options = clusterId("14");
      assertEquals(ImmutableList.of(14 + ""), options.buildQueryParameters().get("clusterid"));
   }

   public void testId() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().id("15");
      assertEquals(ImmutableList.of(15 + ""), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListStoragePoolsOptions options = id("15");
      assertEquals(ImmutableList.of(15 + ""), options.buildQueryParameters().get("id"));
   }

   public void testIpAddress() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().ipAddress("192.168.42.42");
      assertEquals(ImmutableList.of("192.168.42.42"), options.buildQueryParameters().get("ipaddress"));
   }

   public void testIpAddressStatic() {
      ListStoragePoolsOptions options = ipAddress("192.168.42.42");
      assertEquals(ImmutableList.of("192.168.42.42"), options.buildQueryParameters().get("ipaddress"));
   }

   public void testKeyword() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListStoragePoolsOptions options = keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testName() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().name("bob");
      assertEquals(ImmutableList.of("bob"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListStoragePoolsOptions options = name("bob");
      assertEquals(ImmutableList.of("bob"), options.buildQueryParameters().get("name"));
   }

   public void testPath() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().path("/foo/bar");
      assertEquals(ImmutableList.of("/foo/bar"), options.buildQueryParameters().get("path"));
   }

   public void testPathStatic() {
      ListStoragePoolsOptions options = path("/foo/bar");
      assertEquals(ImmutableList.of("/foo/bar"), options.buildQueryParameters().get("path"));
   }

   public void testPodId() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().podId("16");
      assertEquals(ImmutableList.of(16 + ""), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListStoragePoolsOptions options = podId("16");
      assertEquals(ImmutableList.of(16 + ""), options.buildQueryParameters().get("podid"));
   }

   public void testZoneId() {
      ListStoragePoolsOptions options = new ListStoragePoolsOptions().zoneId("17");
      assertEquals(ImmutableList.of(17 + ""), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListStoragePoolsOptions options = zoneId("17");
      assertEquals(ImmutableList.of(17 + ""), options.buildQueryParameters().get("zoneid"));
   }

}
