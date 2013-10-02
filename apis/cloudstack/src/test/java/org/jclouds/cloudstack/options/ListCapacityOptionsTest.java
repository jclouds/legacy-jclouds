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

import static org.jclouds.cloudstack.options.ListCapacityOptions.Builder.hostId;
import static org.jclouds.cloudstack.options.ListCapacityOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListCapacityOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListCapacityOptions.Builder.type;
import static org.jclouds.cloudstack.options.ListCapacityOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.Capacity;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListCapacityOptions}
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListCapacityOptionsTest {

   public void testHostId() {
      ListCapacityOptions options = new ListCapacityOptions().hostId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("hostid"));
   }

   public void testHostIdStatic() {
      ListCapacityOptions options = hostId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("hostid"));
   }

   public void testKeyword() {
      ListCapacityOptions options = new ListCapacityOptions().keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListCapacityOptions options = keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testPodId() {
      ListCapacityOptions options = new ListCapacityOptions().podId("5");
      assertEquals(ImmutableList.of("5"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListCapacityOptions options = podId("5");
      assertEquals(ImmutableList.of("5"), options.buildQueryParameters().get("podid"));
   }

   public void testType() {
      ListCapacityOptions options = new ListCapacityOptions().type(Capacity.Type.PUBLIC_IP_ADDRESSES);
      assertEquals(ImmutableList.of("4"), options.buildQueryParameters().get("type"));
   }

   public void testTypeStatic() {
      ListCapacityOptions options = type(Capacity.Type.PUBLIC_IP_ADDRESSES);
      assertEquals(ImmutableList.of("4"), options.buildQueryParameters().get("type"));
   }

   public void testZoneId() {
      ListCapacityOptions options = new ListCapacityOptions().zoneId("4");
      assertEquals(ImmutableList.of("4"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListCapacityOptions options = zoneId("4");
      assertEquals(ImmutableList.of("4"), options.buildQueryParameters().get("zoneid"));
   }
}
