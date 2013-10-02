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
package org.jclouds.dynect.v3.features;

import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.dynect.v3.domain.RecordSet;
import org.jclouds.dynect.v3.domain.RecordSet.Value;
import org.jclouds.dynect.v3.domain.GeoRegionGroup;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "GeoRegionGroupApiLiveTest")
public class GeoRegionGroupApiLiveTest extends BaseDynECTApiLiveTest {

   @Test
   protected void testListAndGetGeoRegionGroups() {
      for (String service : geoApi().list()) {
         GeoRegionGroupApi api = api(service);
         ImmutableList<String> groups = api.list().toList();
         getAnonymousLogger().info("geo service: " + service + " group count: " + groups.size());
         for (String group : groups) {
            GeoRegionGroup groupDetail = api.get(group);
            assertNotNull(groupDetail.getServiceName().get(), "ServiceName cannot be null " + groupDetail);
            checkGeoRegionGroup(groupDetail);
         }
      }
   }

   static void checkGeoRegionGroup(GeoRegionGroup group) {
      assertNotNull(group.getName(), "Name cannot be null " + group);
      assertTrue(group.getCountries().size() > 0, "countries must be assigned " + group);
      assertTrue(group.getRecordSets().size() > 0, "RecordSets must be assigned " + group);
      for (RecordSet recordSet : group.getRecordSets())
         checkRecordSet(recordSet);
   }

   static void checkRecordSet(RecordSet rset) {
      assertNotNull(rset.getType(), "Type cannot be null " + rset);
      assertTrue(rset.getTTL() >= 0, "TTL cannot be negative " + rset);
      for (Value value : rset)
         checkValue(value);
   }

   static void checkValue(Value value) {
      assertNotNull(value.getLabel(), "Label cannot be null " + value);
      assertNotNull(value.getWeight(), "Weight cannot be null " + value);
      assertTrue(value.getRData().size() > 0, "RData entries should be present: " + value);
   }

   protected GeoRegionGroupApi api(String serviceName) {
      return api.getGeoRegionGroupApiForService(serviceName);
   }

   protected GeoServiceApi geoApi() {
      return api.getGeoServiceApi();
   }
}
