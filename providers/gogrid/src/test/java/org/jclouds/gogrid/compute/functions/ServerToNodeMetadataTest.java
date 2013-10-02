/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.gogrid.compute.suppliers.GoGridHardwareSupplier;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerState;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ServerToNodeMetadataTest")
public class ServerToNodeMetadataTest {
   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @SuppressWarnings("unchecked")
   @Test
   public void testApplySetsTagFromNameAndCredentialsFromName() {

      Map<ServerState, Status> serverStateToNodeStatus = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);
      Option dc = Option.createWithIdNameAndDescription(1l, "US-West-1", "US West 1 Datacenter");
      Option ram = Option.createWithIdNameAndDescription(1l, "512MB", "Server with 512MB RAM");

      Set<? extends org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Server server = createMock(Server.class);

      expect(server.getId()).andReturn(1000l).atLeastOnce();
      expect(server.getName()).andReturn("group-ff").atLeastOnce();
      expect(server.getState()).andReturn(ServerState.ON).atLeastOnce();

      expect(serverStateToNodeStatus.get(ServerState.ON)).andReturn(Status.RUNNING);

      Location location = new LocationBuilder().scope(LocationScope.ZONE).id("1").description("US-West-1").build();
      Set< ? extends Location> locations = ImmutableSet.< Location> of( location);
      
      expect(server.getIp()).andReturn(Ip.builder().ip("127.0.0.1").build());

      ServerImage image = createMock(ServerImage.class);
      expect(server.getImage()).andReturn(image).atLeastOnce();
      expect(server.getRam()).andReturn(ram).atLeastOnce();
      expect(server.getDatacenter()).andReturn(dc).atLeastOnce();
      expect(image.getId()).andReturn(2000l).atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("2000").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();
      expect(jcImage.getOperatingSystem()).andReturn(createMock(OperatingSystem.class)).atLeastOnce();

      replay(serverStateToNodeStatus);
      replay(server);
      replay(image);
      replay(jcImage);

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeStatus, Suppliers
               .<Set<? extends Image>> ofInstance(images), Suppliers
               .<Set<? extends Hardware>> ofInstance(GoGridHardwareSupplier.H_ALL), Suppliers
               .<Set<? extends Location>> ofInstance(locations), namingConvention);

      NodeMetadata metadata = parser.apply(server);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImageId(), "2000");
      assertEquals(metadata.getGroup(), "group");

      verify(serverStateToNodeStatus);
      verify(image);
      verify(server);
      verify(jcImage);

   }

}
