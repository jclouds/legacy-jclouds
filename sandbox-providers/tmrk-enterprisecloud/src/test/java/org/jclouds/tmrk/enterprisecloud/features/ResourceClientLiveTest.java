/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of {@code ResourceClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "ResourceClientLiveTest")
public class ResourceClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getResourceClient();
   }

   private ResourceClient client;

   public void testGetResourceSummaries() throws Exception {
      ComputePoolResourceSummaryList list = client.getResourceSummaries(URI.create("/cloudapi/ecloud/computepools/environments/77/resourcesummarylist"));
      assertNotNull(list);
   }

   public void testMissingResourceSummaries() {
      assertNull(client.getResourceSummaries(URI.create("/cloudapi/ecloud/computepools/environments/-1/resourcesummarylist")));
   }

   public void testGetResourceSummary() throws Exception {
      ComputePoolResourceSummary resourceSummary = client.getResourceSummary(URI.create("/cloudapi/ecloud/computepools/89/resourcesummary"));
      assertNotNull(resourceSummary);
   }

   public void testMissingResourceSummary() {
      assertNull(client.getResourceSummary(URI.create("/cloudapi/ecloud/computepools/-1/resourcesummary")));
   }
}
