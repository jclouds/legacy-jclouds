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
package org.jclouds.vcloud.compute.strategy;

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

import javax.inject.Provider;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.functions.FindLocationForResourceInVDC;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudListNodesStrategyTest")
public class VCloudListNodesStrategyTest {
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

      replay(client);
      replay(computeClient);

      Location vdcL = new LocationImpl(LocationScope.ZONE, "1", "1", null);
      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.of(vdcL));
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet
               .<Image> of());
      FindLocationForResourceInVDC findLocationForResourceInVDC = new FindLocationForResourceInVDC(
               locations, null);
      VCloudListNodesStrategy strategy = new VCloudListNodesStrategy(client, computeClient,
               vAppStatusToNodeState, getExtra, findLocationForResourceInVDC, images);

      Set<NodeMetadata> nodes = Sets.newHashSet();
      NamedResource vdc = new NamedResourceImpl("1", null, null, null);
      NamedResource resource = new NamedResourceImpl("10", null, null, null);

      strategy.addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);

      verify(client);
      verify(computeClient);
   }
}