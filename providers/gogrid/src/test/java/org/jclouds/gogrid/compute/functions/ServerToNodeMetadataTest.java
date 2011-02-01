/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.gogrid.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.compute.suppliers.GoGridHardwareSupplier;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.gogrid.services.GridServerClient;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerToNodeMetadataTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testApplySetsTagFromNameAndCredentialsFromName() throws UnknownHostException {
      GoGridClient caller = createMock(GoGridClient.class);
      GridServerClient client = createMock(GridServerClient.class);
      expect(caller.getServerServices()).andReturn(client).atLeastOnce();
      Map<ServerState, NodeState> serverStateToNodeState = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);
      Option dc = new Option(1l, "US-West-1", "US West 1 Datacenter");
      Option ram = new Option(1l, "512MB", "Server with 512MB RAM");

      Set<? extends org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Server server = createMock(Server.class);

      expect(server.getId()).andReturn(1000l).atLeastOnce();
      expect(server.getName()).andReturn("group-ff").atLeastOnce();
      expect(server.getState()).andReturn(ServerState.ON).atLeastOnce();

      expect(serverStateToNodeState.get(ServerState.ON)).andReturn(NodeState.RUNNING);

      Location location = new LocationBuilder().scope(LocationScope.ZONE).id("1").description("US-West-1").build();
      Map<String, ? extends Location> locations = ImmutableMap.<String, Location> of("1", location);

      Map<String, Credentials> credentialsMap = createMock(Map.class);
      expect(client.getServerCredentialsList()).andReturn(credentialsMap);
      expect(credentialsMap.get("group-ff")).andReturn(new Credentials("user", "pass"));

      expect(server.getIp()).andReturn(new Ip("127.0.0.1"));

      ServerImage image = createMock(ServerImage.class);
      expect(server.getImage()).andReturn(image).atLeastOnce();
      expect(server.getRam()).andReturn(ram).atLeastOnce();
      expect(server.getDatacenter()).andReturn(dc).atLeastOnce();
      expect(image.getId()).andReturn(2000l).atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("2000").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();
      expect(jcImage.getOperatingSystem()).andReturn(createMock(OperatingSystem.class)).atLeastOnce();

      replay(caller);
      replay(client);
      replay(serverStateToNodeState);
      replay(server);
      replay(image);
      replay(jcImage);
      replay(credentialsMap);

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState, caller, Suppliers
               .<Set<? extends Image>> ofInstance(images), Suppliers
               .<Set<? extends Hardware>> ofInstance(GoGridHardwareSupplier.H_ALL), Suppliers
               .<Map<String, ? extends Location>> ofInstance(locations));

      NodeMetadata metadata = parser.apply(server);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImageId(), "2000");
      assertEquals(metadata.getGroup(), "group");
      assertEquals(metadata.getCredentials(), new Credentials("user", "pass"));

      verify(caller);
      verify(client);
      verify(serverStateToNodeState);
      verify(image);
      verify(credentialsMap);
      verify(server);
      verify(jcImage);

   }

}
