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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.v2_0.compute.functions.ServerInZoneToNodeMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ServerInZone;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerTest;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * Tests for the function for transforming a nova specific Server into a generic
 * NodeMetadata object.
 * 
 * @author Matt Stephenson, Adam Lowe, Adrian Cole
 */
@Test(testName = "ServerInZoneToNodeMetadataTest")
public class ServerInZoneToNodeMetadataTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova")
         .description("openstack-nova").build();
   Location zone = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1")
         .scope(LocationScope.ZONE).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
         .<String, Location> of("az-1.region-a.geo-1", zone));

   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testWhenNoHardwareOrImageMatchServerScopedIdsImageIdIsStillSet() {

      Hardware existingHardware = new HardwareBuilder().id("az-1.region-a.geo-1/FOOOOOOOO").providerId("FOOOOOOOO")
            .location(zone).build();
      Image existingImage = new ImageBuilder().id("az-1.region-a.geo-1/FOOOOOOOO")
            .operatingSystem(OperatingSystem.builder().family(OsFamily.LINUX).description("foobuntu").build())
            .providerId("FOOOOOOOO").description("foobuntu").location(zone).status(Image.Status.AVAILABLE).build();

      checkHardwareAndImageStatus(null, existingHardware, "az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f6f006e54",
            null, existingImage);
   }

   @Test
   public void testWhenNoHardwareAndImageMatchServerScopedIdsHardwareOperatingSystemAndImageIdAreSet() {

      Hardware existingHardware = new HardwareBuilder().id("az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f216543fd")
            .providerId("52415800-8b69-11e0-9b19-734f216543fd").location(zone).build();
      Image existingImage = new ImageBuilder().id("az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f6f006e54")
            .operatingSystem(OperatingSystem.builder().family(OsFamily.LINUX).description("foobuntu").build())
            .providerId("52415800-8b69-11e0-9b19-734f6f006e54").description("foobuntu").status(Image.Status.AVAILABLE)
            .location(zone).build();

      checkHardwareAndImageStatus(existingHardware, existingHardware, existingImage.getId(),
            existingImage.getOperatingSystem(), existingImage);
   }

   // TODO: clean up this syntax
   private void checkHardwareAndImageStatus(Hardware expectedHardware, Hardware existingHardware,
         String expectedImageId, OperatingSystem expectedOs, Image existingImage) {

      Set<Image> images = existingImage == null ? ImmutableSet.<Image> of() : ImmutableSet.of(existingImage);
      Set<Hardware> hardwares = existingHardware == null ? ImmutableSet.<Hardware> of() : ImmutableSet
            .of(existingHardware);
      Server serverToConvert = new ParseServerTest().expected();

      ServerInZone serverInZoneToConvert = new ServerInZone(serverToConvert, "az-1.region-a.geo-1");

      ServerInZoneToNodeMetadata converter = new ServerInZoneToNodeMetadata(
               NovaComputeServiceContextModule.toPortableNodeStatus, locationIndex, Suppliers
                        .<Set<? extends Image>> ofInstance(images), Suppliers
                        .<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata convertedNodeMetadata = converter.apply(serverInZoneToConvert);

      assertEquals(serverInZoneToConvert.slashEncode(), convertedNodeMetadata.getId());
      assertEquals(serverToConvert.getId(), convertedNodeMetadata.getProviderId());

      assertEquals(convertedNodeMetadata.getLocation().getScope(), LocationScope.HOST);
      assertEquals(convertedNodeMetadata.getLocation().getId(), "e4d909c290d0fb1ca068ffaddf22cbd0");

      assertEquals(convertedNodeMetadata.getLocation().getParent(), locationIndex.get().get("az-1.region-a.geo-1"));

      assertEquals(serverToConvert.getName(), convertedNodeMetadata.getName());
      assertEquals(convertedNodeMetadata.getGroup(), "sample");

      assertEquals(convertedNodeMetadata.getImageId(), expectedImageId);
      assertEquals(convertedNodeMetadata.getOperatingSystem(), expectedOs);

      assertEquals(convertedNodeMetadata.getHardware(), expectedHardware);

      assertEquals(NovaComputeServiceContextModule.toPortableNodeStatus.get(serverToConvert.getStatus()),
               convertedNodeMetadata.getStatus());

      assertNotNull(convertedNodeMetadata.getPrivateAddresses());
      assertEquals(convertedNodeMetadata.getPrivateAddresses(), ImmutableSet.of("10.176.42.16"));

      assertNotNull(convertedNodeMetadata.getPublicAddresses());
      // note jclouds doesn't yet support ipv6 b/c not tested yet
      assertEquals(convertedNodeMetadata.getPublicAddresses(), ImmutableSet.of("67.23.10.132", "67.23.10.131"));

      assertNotNull(convertedNodeMetadata.getUserMetadata());
      assertEquals(convertedNodeMetadata.getUserMetadata(),
            ImmutableMap.<String, String> of("Server Label", "Web Head 1", "Image Version", "2.1"));
   }

   @Test
   public void testNewServerWithoutHostIdSetsZoneAsLocation() {

      Set<Image> images = ImmutableSet.<Image> of();
      Set<Hardware> hardwares = ImmutableSet.<Hardware> of();

      Server serverToConvert = expectedServer();

      ServerInZone serverInZoneToConvert = new ServerInZone(serverToConvert, "az-1.region-a.geo-1");

      ServerInZoneToNodeMetadata converter = new ServerInZoneToNodeMetadata(
               NovaComputeServiceContextModule.toPortableNodeStatus, locationIndex, Suppliers
                        .<Set<? extends Image>> ofInstance(images), Suppliers
                        .<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata convertedNodeMetadata = converter.apply(serverInZoneToConvert);

      assertEquals(serverInZoneToConvert.slashEncode(), convertedNodeMetadata.getId());
      assertEquals(serverToConvert.getId(), convertedNodeMetadata.getProviderId());

      assertEquals(convertedNodeMetadata.getLocation(), zone);

   }

   public Server expectedServer() {
      return Server
            .builder()
            .id("71752")
            .uuid("47491020-6a78-4f63-9475-23195ac4515c")
            .tenantId("37936628937291")
            .userId("54297837463082")
            .name("test-e92")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-19T06:21:13Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-19T06:21:13Z"))
            .status(Server.Status.BUILD)
            .image(
                  Resource
                        .builder()
                        .id("1241")
                        .links(
                              Link.create(
                                    Link.Relation.BOOKMARK,
                                    URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/images/1241")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("100")
                        .links(
                              Link.create(
                                    Link.Relation.BOOKMARK,
                                    URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/flavors/100")))
                        .build())
            .links(
                  Link.create(Link.Relation.SELF, URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/servers/71752")),
                  Link.create(Link.Relation.BOOKMARK, URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/servers/71752"))).build();

   }
}
