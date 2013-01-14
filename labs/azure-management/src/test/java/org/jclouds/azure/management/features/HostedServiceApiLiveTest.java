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
package org.jclouds.azure.management.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.azure.management.domain.DetailedHostedServiceProperties;
import org.jclouds.azure.management.domain.HostedService;
import org.jclouds.azure.management.domain.HostedService.Status;
import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;
import org.jclouds.azure.management.domain.Operation;
import org.jclouds.azure.management.internal.BaseAzureManagementApiLiveTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "HostedServiceApiLiveTest")
public class HostedServiceApiLiveTest extends BaseAzureManagementApiLiveTest {

   public static final String HOSTED_SERVICE = (System.getProperty("user.name") + "-jclouds-hostedService")
            .toLowerCase();

   private Predicate<String> operationSucceeded;
   private Predicate<HostedServiceWithDetailedProperties> hostedServiceCreated;
   private Predicate<HostedService> hostedServiceGone;

   private String location;

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
      // TODO: filter locations on those who have compute
      location = Iterables.get(context.getApi().getLocationApi().list(), 0).getName();
      operationSucceeded = retry(new Predicate<String>() {
         public boolean apply(String input) {
            return context.getApi().getOperationApi().get(input).getStatus() == Operation.Status.SUCCEEDED;
         }
      }, 600, 5, 5, SECONDS);
      hostedServiceCreated = retry(new Predicate<HostedServiceWithDetailedProperties>() {
         public boolean apply(HostedServiceWithDetailedProperties input) {
            return api().getDetails(input.getName()).getProperties().getStatus() == Status.CREATED;
         }
      }, 600, 5, 5, SECONDS);
      hostedServiceGone = retry(new Predicate<HostedService>() {
         public boolean apply(HostedService input) {
            return api().get(input.getName()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   private HostedServiceWithDetailedProperties hostedService;

   public void testCreateHostedService() {

      String requestId = api().createServiceWithLabelInLocation(HOSTED_SERVICE, HOSTED_SERVICE, location);
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      hostedService = api().getDetails(HOSTED_SERVICE);
      Logger.getAnonymousLogger().info("created hostedService: " + hostedService);

      assertEquals(hostedService.getName(), HOSTED_SERVICE);

      checkHostedService(hostedService);

      assertTrue(hostedServiceCreated.apply(hostedService), hostedService.toString());
      hostedService = api().getDetails(hostedService.getName());
      Logger.getAnonymousLogger().info("hostedService available: " + hostedService);

   }

   @Test(dependsOnMethods = "testCreateHostedService")
   public void testDeleteHostedService() {
      String requestId = api().delete(hostedService.getName());
      assertTrue(operationSucceeded.apply(requestId), requestId);
      Logger.getAnonymousLogger().info("operation succeeded: " + requestId);

      assertTrue(hostedServiceGone.apply(hostedService), hostedService.toString());
      Logger.getAnonymousLogger().info("hostedService deleted: " + hostedService);
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDownContext() {
      String requestId = api().delete(HOSTED_SERVICE);
      if (requestId != null)
         operationSucceeded.apply(requestId);

      super.tearDownContext();
   }

   @Test
   protected void testList() {
      Set<HostedServiceWithDetailedProperties> response = api().list();

      for (HostedServiceWithDetailedProperties hostedService : response) {
         checkHostedService(hostedService);
      }

      if (response.size() > 0) {
         HostedService hostedService = response.iterator().next();
         Assert.assertEquals(api().getDetails(hostedService.getName()), hostedService);
      }
   }

   private void checkHostedService(HostedServiceWithDetailedProperties hostedService) {
      checkNotNull(hostedService.getUrl(), "Url cannot be null for a HostedService.");
      checkNotNull(hostedService.getName(), "ServiceName cannot be null for HostedService %s", hostedService.getUrl());
      checkNotNull(hostedService.getProperties(), "Properties cannot be null for HostedService %s",
               hostedService.getUrl());
      checkProperties(hostedService.getProperties());
   }

   private void checkProperties(DetailedHostedServiceProperties hostedService) {
      checkNotNull(hostedService.getDescription(),
               "While Description can be null for DetailedHostedServiceProperties, its Optional wrapper cannot: %s",
               hostedService);
      checkNotNull(hostedService.getLocation(),
               "While Location can be null for DetailedHostedServiceProperties, its Optional wrapper cannot: %s",
               hostedService);
      checkNotNull(hostedService.getAffinityGroup(),
               "While AffinityGroup can be null for DetailedHostedServiceProperties, its Optional wrapper cannot: %s",
               hostedService);
      checkState(hostedService.getLocation().isPresent() || hostedService.getAffinityGroup().isPresent(),
               "Location or AffinityGroup must be present for DetailedHostedServiceProperties: %s", hostedService);
      checkNotNull(hostedService.getLabel(), "Label cannot be null for HostedService %s", hostedService);

      checkNotNull(hostedService.getStatus(), "Status cannot be null for DetailedHostedServiceProperties: %s",
               hostedService);
      assertNotEquals(hostedService.getStatus(), Status.UNRECOGNIZED,
               "Status cannot be UNRECOGNIZED for DetailedHostedServiceProperties: " + hostedService);
      checkNotNull(hostedService.getCreated(), "Created cannot be null for DetailedHostedServiceProperties %s",
               hostedService);
      checkNotNull(hostedService.getLastModified(),
               "LastModified cannot be null for DetailedHostedServiceProperties %s", hostedService);
      checkNotNull(hostedService.getExtendedProperties(),
               "ExtendedProperties cannot be null for DetailedHostedServiceProperties %s", hostedService);
   }

   protected HostedServiceApi api() {
      return context.getApi().getHostedServiceApi();
   }
}
