/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.compute.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule.VCloudListNodesStrategy;
import org.jclouds.vcloud.compute.functions.GetExtra;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudComputeServiceContextModuleTest")
public class VCloudComputeServiceContextModuleTest {
   private VApp newVApp() throws UnknownHostException {
      ListMultimap<String, InetAddress> networkToAddresses = ImmutableListMultimap
               .<String, InetAddress> of("Network 1", InetAddress.getLocalHost());

      VirtualSystem system = new VirtualSystem(0, "Virtual Hardware Family", "SimpleVM", "vmx-07");

      SortedSet<ResourceAllocation> resourceAllocations = ImmutableSortedSet
               .<ResourceAllocation> naturalOrder().add(
                        new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs",
                                 ResourceType.PROCESSOR, null, null, null, null, null, null, 1,
                                 "hertz * 10^6"),
                        new ResourceAllocation(2, "512MB of memory", "Memory Size",
                                 ResourceType.MEMORY, null, null, null, null, null, null, 512,
                                 "byte * 2^20")).add(

                        new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller",
                                 ResourceType.SCSI_CONTROLLER, "lsilogic", null, 0, null, null,
                                 null, 1, null)).add(

                        new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE,
                                 null, "20971520", null, 0, 3, null, 20971520, "byte * 2^20"))
               .build();

      return new VAppImpl("10", "10", URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vapp/10"), VAppStatus.OFF,
               new Long(20971520), null, networkToAddresses, null, system, resourceAllocations);
   }

   // consistency delay specifically in terremark beta where a vapp is present for listing, but not
   // yet available for get vapp command.
   @Test
   public void testRetryOnVAppNotFoundForGetVAppEvenWhenPresentInAvailableResources()
            throws ExecutionException, InterruptedException, TimeoutException, IOException {
      VCloudClient client = createMock(VCloudClient.class);
      GetExtra getExtra = new GetExtra();

      VCloudComputeClient computeClient = createMock(VCloudComputeClient.class);
      Map<VAppStatus, NodeState> vAppStatusToNodeState = Maps.newHashMap();
      VApp vApp = newVApp();
      expect(client.getVApp("10")).andThrow(new NullPointerException());
      expect(client.getVApp("10")).andThrow(new NullPointerException());
      expect(client.getVApp("10")).andReturn(vApp);
      expect(computeClient.getPublicAddresses("10")).andReturn(Sets.<InetAddress> newHashSet());
      expect(computeClient.getPrivateAddresses("10")).andReturn(
               Sets.newHashSet(InetAddress.getLocalHost()));
      
      replay(getExtra);
      replay(client);
      replay(computeClient);
      
      Map<String, ? extends Location> locations = ImmutableMap.of();
      Map<String, ? extends Image> images= ImmutableMap.of();
      VCloudListNodesStrategy strategy = new VCloudListNodesStrategy(client, computeClient,
               vAppStatusToNodeState, getExtra, locations, images);

      Set<ComputeMetadata> nodes = Sets.newHashSet();
      NamedResource vdc = new NamedResourceImpl("1", null, null, null);
      NamedResource resource = new NamedResourceImpl("10", null, null, null);

      strategy.addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
      
      verify(getExtra);
      verify(client);
      verify(computeClient);
   }
}