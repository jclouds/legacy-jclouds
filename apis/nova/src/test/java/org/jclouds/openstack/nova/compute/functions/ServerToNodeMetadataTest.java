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
package org.jclouds.openstack.nova.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.openstack.nova.functions.ParseServerFromJsonResponseTest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerToNodeMetadataTest {
   Location provider = new LocationBuilder().scope(LocationScope.ZONE).id("dallas").description("description").build();
   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testApplyWhereImageAndHardwareNotFound() throws UnknownHostException, NoSuchMethodException,
         ClassNotFoundException, URISyntaxException {
      Map<ServerStatus, NodeState> serverStateToNodeState = NovaComputeServiceContextModule.serverToNodeState;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of();
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            Suppliers.<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider),
            Suppliers.<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(server);

      NodeMetadata constructedMetadata = newNodeMetadataBuilder().build();

      assertEquals(metadata, constructedMetadata);

   }

   private NodeMetadataBuilder newNodeMetadataBuilder() throws URISyntaxException {
      return new NodeMetadataBuilder()
            .state(NodeState.PENDING)
            .publicAddresses(ImmutableSet.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"))
            .privateAddresses(ImmutableSet.of("10.176.42.16", "::babe:10.176.42.16"))
            .id("1234")
            .providerId("1234")
            .group("sample")
            .name("sample-server")
            .location(
                  new LocationBuilder().scope(LocationScope.HOST).id("e4d909c290d0fb1ca068ffaddf22cbd0")
                        .description("e4d909c290d0fb1ca068ffaddf22cbd0").parent(provider).build())
            .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1"))
            .uri(new URI("http://servers.api.openstack.org/1234/servers/1234"));
   }

   @Test
   public void testApplyWhereImageFoundAndHardwareNotFound() throws UnknownHostException, NoSuchMethodException,
         ClassNotFoundException, URISyntaxException {
      Map<ServerStatus, NodeState> serverStateToNodeState = NovaComputeServiceContextModule.serverToNodeState;
      org.jclouds.compute.domain.Image jcImage = NovaImageToImageTest.convertImage();
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            Suppliers.<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider),
            Suppliers.<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(server);

      NodeMetadata constructedMetadata = newNodeMetadataBuilder()
            .imageId("2")
            .operatingSystem(
                  new OperatingSystem.Builder().family(OsFamily.CENTOS).description("CentOS 5.2").version("5.2")
                        .is64Bit(true).build()).build();

      assertEquals(metadata, constructedMetadata);

   }

   @Test
   public void testApplyWhereImageAndHardwareFound() throws UnknownHostException, NoSuchMethodException,
         ClassNotFoundException, URISyntaxException {
      Map<ServerStatus, NodeState> serverStateToNodeState = NovaComputeServiceContextModule.serverToNodeState;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(NovaImageToImageTest.convertImage());
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of(FlavorToHardwareTest.convertFlavor());
      Server server = ParseServerFromJsonResponseTest.parseServer();

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState,
            Suppliers.<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider),
            Suppliers.<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(server);

      NodeMetadata constructedMetadata = newNodeMetadataBuilder()
            .imageId("2")
            .group("sample")
            .operatingSystem(
                  new OperatingSystem.Builder().family(OsFamily.CENTOS).description("CentOS 5.2").version("5.2")
                        .is64Bit(true).build())
            .hardware(
                  new HardwareBuilder()
                        .ids("1")
                        .name("256 MB Server")
                        .processors(ImmutableList.of(new Processor(1.0, 1.0)))
                        .ram(256)
                        .volumes(
                              ImmutableList.of(new VolumeBuilder().type(Volume.Type.LOCAL).size(10.0f).durable(true)
                                    .bootDevice(true).build()))
                        .uri(new URI("http://servers.api.openstack.org/1234/flavors/1")).build()).build();

      assertEquals(metadata, constructedMetadata);
   }
}
