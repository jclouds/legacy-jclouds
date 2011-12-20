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

import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of {@code InternetServiceClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "InternetServiceClientLiveTest")
public class InternetServiceClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getInternetServiceClient();
   }

   private InternetServiceClient client;

   public void testGetLayouts() throws Exception {
      //TODO: THe URI should come from the environment
      InternetService internetService = client.getInternetService(URI.create("/cloudapi/ecloud/internetservices/797"));
      assertNotNull(internetService);
   }

   public void testGetMissingLayouts() {
      assertNull(client.getInternetService(URI.create("/cloudapi/ecloud/internetservices/-1")));
   }
}
