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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.SimpleTenantUsage;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * Tests behavior of SimpleTenantUsageApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "SimpleTenantUsageApiLiveTest")
public class SimpleTenantUsageApiLiveTest extends BaseNovaApiLiveTest {

   public void testList() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         Optional<? extends SimpleTenantUsageApi> optApi = api.getSimpleTenantUsageExtensionForZone(zoneId);
         if (optApi.isPresent() && identity.endsWith(":admin")) {
            SimpleTenantUsageApi api = optApi.get();
            Set<? extends SimpleTenantUsage> usages = api.list().toSet();
            assertNotNull(usages);
            for (SimpleTenantUsage usage : usages) {
               SimpleTenantUsage details = api.get(usage.getTenantId());
               assertNotNull(details);
            }
         }
      }
   }
}
