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
package org.jclouds.ultradns.ws;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map.Entry;

import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "UltraDNSWSApiLiveTest")
public class UltraDNSWSApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   @Test
   protected void testGetCurrentAccount() {
      IdAndName account = api.getCurrentAccount();
      checkAccount(account);
   }

   private void checkAccount(IdAndName account) {
      assertNotNull(account.getId(), "Id cannot be null for " + account);
      assertNotNull(account.getName(), "Name cannot be null for " + account);
   }

   @Test
   public void testListRegions() {
      for (Entry<IdAndName, Collection<String>> region : api.getRegionsByIdAndName().asMap().entrySet()) {
         checkRegion(region);
      }
   }

   private void checkRegion(Entry<IdAndName, Collection<String>> region) {
      assertNotNull(region.getKey().getId(), "Id cannot be null " + region);
      assertNotNull(region.getKey().getName(), "Name cannot be null " + region);
      assertNotNull(region.getValue(), "TerritoryNames cannot be null " + region);
      assertFalse(region.getValue().isEmpty(), "TerritoryNames cannot be empty " + region);
   }
}
