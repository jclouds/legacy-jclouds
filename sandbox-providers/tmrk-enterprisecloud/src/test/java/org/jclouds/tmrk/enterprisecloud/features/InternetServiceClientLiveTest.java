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

import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.jclouds.tmrk.enterprisecloud.predicates.TaskPredicates.completeOrSuccess;
import static org.testng.Assert.*;

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
      taskClient = context.getApi().getTaskClient();
   }

   private InternetServiceClient client;
   private TaskClient taskClient;

   public void testInternetServiceCalls() {
      InternetService service = InternetService.builder()
            .name("live test")
            .href(URI.create(""))
            .protocol(Protocol.TCP)
            .port(2020)
            .enabled(true)
            .persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build())
            .build();

      // TODO: Fetch a public ip from the environment
      // This has a method not allowed error - needs debugging.
      URI uri = URI.create("/cloudapi/ecloud/publicips/3929");
      InternetService internetService = client.createInternetService(uri, service);
      System.out.println("service:"+internetService);

      InternetService editServiceData = InternetService.builder().href(uri).name("testName").enabled(false).build();

      Task editTask = client.editInternetService(editServiceData);
      System.out.println("Task:"+editTask);
      RetryablePredicate retryablePredicate = new RetryablePredicate(completeOrSuccess(taskClient), 1000*60);
      if (!retryablePredicate.apply(editTask)) {
         fail("Did not manage to edit service:"+editTask);
      }

      InternetService editedService = client.getInternetService(internetService.getHref());
      assertEquals(editedService.getName(),"testName");
      assertFalse(editedService.isEnabled());

      Task removeTask = client.removeInternetService(internetService.getHref());
      if (!retryablePredicate.apply(removeTask)) {
         fail("Did not manage to remove service:"+removeTask);
      }
   }

   public void testGetMissingInternetService() {
      assertNull(client.getInternetService(URI.create("/cloudapi/ecloud/internetservices/-1")));
   }
}
