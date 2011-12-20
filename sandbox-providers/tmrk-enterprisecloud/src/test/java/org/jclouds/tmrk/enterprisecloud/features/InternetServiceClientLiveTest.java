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

import org.jclouds.tmrk.enterprisecloud.domain.Task;
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

   public void testGetInternetService() throws Exception {
      //TODO: The URI should come from the environment
      //TODO: Should create a new service edit it then delete it.
      //TODO: Need a retryable predicate to wait until the task is done.
      URI uri = URI.create("/cloudapi/ecloud/internetservices/797");
      InternetService internetService = client.getInternetService(uri);
      assertNotNull(internetService);
      /*
      final String originalName = internetService.getName();
      final String newName = originalName+"edited";
      boolean enable = !internetService.isEnabled();

      // Change the name and enabled flag
      testEditInternetService(internetService.getHref(),newName,enable);
      internetService = client.getInternetService(uri);
      assertEquals(internetService.getName(),newName);
      assertEquals(internetService.isEnabled(),enable);

      // Change it back again
      enable = !internetService.isEnabled();
      testEditInternetService(internetService.getHref(),originalName,enable);
      assertEquals(internetService.getName(),originalName);
      assertEquals(internetService.isEnabled(),enable);
      */
   }

   public void testGetMissingInternetService() {
      assertNull(client.getInternetService(URI.create("/cloudapi/ecloud/internetservices/-1")));
   }

   private void testEditInternetService(URI uri, String name, boolean enable) {
      InternetService service = InternetService.builder().href(uri).name(name).enabled(enable).build();
      Task task = client.editInternetService(service);
      //TODO: Wait for task to complete.
   }
}
