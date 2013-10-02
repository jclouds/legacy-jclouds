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
package org.jclouds.ultradns.ws.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.AccountLevelGroup;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalGroupCoordinates;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "DirectionalGroupApiLiveTest")
public class DirectionalGroupApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   @Test
   public void testListAccountLevelGroups() {
      for (AccountLevelGroup group : api().listAccountLevelGroups()) {
         checkGroup(group);
      }
   }

   private void checkGroup(AccountLevelGroup group) {
      assertNotNull(group.getId(), "Id cannot be null " + group);
      assertNotNull(group.getName(), "Name cannot be null " + group);
      assertNotNull(group.getType(), "Type cannot be null " + group);
      assertTrue(group.getRecordCount() >= 0, "RecordCount must be positive " + group);
   }

   @Test
   public void testListRecordsByAccountLevelGroup() {
      for (AccountLevelGroup group : api().listAccountLevelGroups()) {
         for (DirectionalPoolRecordDetail rr : api().listRecordsByAccountLevelGroup(group.getId())) {
            DirectionalPoolApiLiveTest.checkDirectionalRecordDetail(rr);
         }
      }
   }

   @Test
   public void testGetDirectionalGroup() {
      for (AccountLevelGroup group : api().listAccountLevelGroups()) {
         DirectionalGroup withRegions = api().get(group.getId());
         assertEquals(withRegions.getName(), group.getName());
         assertTrue(withRegions.size() > 0);
      }
   }

   Set<DirectionalGroupCoordinates> allGroups = Sets.newLinkedHashSet();

   @Test
   public void testListGroupNamesByRecordNameAndType() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (DirectionalPool pool : api.getDirectionalPoolApiForZone(zone.getName()).list()) {
            for (RecordType type : EnumSet.allOf(RecordType.class)) {
               for (String groupName : api().listGroupNamesByDNameAndType(pool.getDName(), type.getCode())) {
                  allGroups.add(DirectionalGroupCoordinates.builder()
                                                           .zoneName(zone.getName())
                                                           .recordName(pool.getDName())
                                                           .recordType(type.getCode())
                                                           .groupName(groupName).build());
               }
            }
         }
      }
   }

   @Test(dependsOnMethods = "testListGroupNamesByRecordNameAndType")
   public void testListRecordsByGroupCoordinates() {
      for (DirectionalGroupCoordinates group : allGroups) {
         for (DirectionalPoolRecordDetail rr : api().listRecordsByGroupCoordinates(group)) {
            DirectionalPoolApiLiveTest.checkDirectionalRecordDetail(rr);
         }
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Group does not exist.")
   public void testListRecordsByAccountLevelGroupWhenGroupIdNotFound() {
      api().listRecordsByAccountLevelGroup("AAAAAAAAAAAAAAAA");
   }

   private DirectionalGroupApi api() {
      return api.getDirectionalGroupApiForAccount(account.getId());
   }
}
