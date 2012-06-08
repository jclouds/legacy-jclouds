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
package org.jclouds.openstack.nova.v2_0.internal;

import java.util.Properties;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v2_0.NovaAsyncClient;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.config.NovaProperties;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.features.FlavorClient;
import org.jclouds.openstack.nova.v2_0.features.ImageClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * Tests behavior of {@code NovaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseNovaClientLiveTest extends BaseComputeServiceContextLiveTest {

   public BaseNovaClientLiveTest() {
      provider = "openstack-nova";
   }

   protected RestContext<NovaClient, NovaAsyncClient> novaContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      novaContext = view.unwrap();
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      setIfTestSystemPropertyPresent(props, NovaProperties.AUTO_ALLOCATE_FLOATING_IPS);
      return props;
   }
   
   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (novaContext != null)
         novaContext.close();
   }
   
   protected Server createServerInZone(String zoneId) {
      ServerClient serverClient = novaContext.getApi().getServerClientForZone(zoneId);
      ServerCreated server = serverClient.createServer("test", imageIdForZone(zoneId), flavorRefForZone(zoneId));
      blockUntilServerInState(server.getId(), serverClient, Status.ACTIVE);
      return serverClient.getServer(server.getId());
   }

   /** 
    * Will block until the requested server is in the correct state, if Extended Server Status extension is loaded
    * this will continue to block while any task is in progress.
    */
   protected void blockUntilServerInState(String serverId, ServerClient client, Status status) {
      Server currentDetails = null;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != status ||
           (currentDetails.getExtendedStatus().isPresent() && currentDetails.getExtendedStatus().get().getTaskState() != null);
           currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status %s%n%s%n", status, currentDetails);
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException e) {
            throw Throwables.propagate(e);
         }
      }
   }
   
   protected String imageIdForZone(String zoneId) {
      ImageClient imageClient = novaContext.getApi().getImageClientForZone(zoneId);
      return Iterables.getLast(imageClient.listImages()).getId();
   }

   protected String flavorRefForZone(String zoneId) {
      FlavorClient flavorClient = novaContext.getApi().getFlavorClientForZone(zoneId);
      return DEFAULT_FLAVOR_ORDERING.min(flavorClient.listFlavorsInDetail()).getId();
   }

   static final Ordering<Flavor> DEFAULT_FLAVOR_ORDERING = new Ordering<Flavor>() {
      public int compare(Flavor left, Flavor right) {
         return ComparisonChain.start().compare(left.getVcpus(), right.getVcpus()).compare(left.getRam(), right.getRam())
               .compare(left.getDisk(), right.getDisk()).result();
      }
   };
}
