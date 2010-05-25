package org.jclouds.rackspace.cloudservers.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rackspace.cloudservers.domain.Addresses;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ServerToNodeMetadataTest")
public class ServerToNodeMetadataTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testApplySetsTagFromNameAndSetsMetadata() throws UnknownHostException {
      Map<ServerStatus, NodeState> serverStateToNodeState = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Server server = createMock(Server.class);

      expect(server.getId()).andReturn(10000).atLeastOnce();
      expect(server.getName()).andReturn("adriancole-cloudservers-ea3").atLeastOnce();
      expect(server.getHostId()).andReturn("AHOST").atLeastOnce();
      expect(server.getMetadata()).andReturn(ImmutableMap.<String, String> of()).atLeastOnce();

      expect(server.getStatus()).andReturn(ServerStatus.ACTIVE).atLeastOnce();

      expect(serverStateToNodeState.get(ServerStatus.ACTIVE)).andReturn(NodeState.RUNNING);
      Location provider = new LocationImpl(LocationScope.ZONE, "dallas", "description", null);
      Location location = new LocationImpl(LocationScope.HOST, "AHOST", "AHOST", provider);

      Addresses addresses = createMock(Addresses.class);
      expect(server.getAddresses()).andReturn(addresses).atLeastOnce();

      Set<String> publicAddresses = ImmutableSet.of("12.10.10.1");
      Set<String> privateAddresses = ImmutableSet.of("10.10.10.1");

      expect(addresses.getPublicAddresses()).andReturn(publicAddresses);
      expect(addresses.getPrivateAddresses()).andReturn(privateAddresses);

      expect(server.getImageId()).andReturn(2000).atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("2000").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(provider).atLeastOnce();

      replay(addresses);
      replay(jcImage);
      replay(serverStateToNodeState);
      replay(server);

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState, images,
               provider);

      NodeMetadata metadata = parser.apply(server);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);
      assert metadata.getUserMetadata() != null;
      assertEquals(metadata.getTag(), "cloudservers");
      assertEquals(metadata.getCredentials(), null);

      assertEquals(metadata.getPrivateAddresses(), privateAddresses);
      assertEquals(metadata.getPublicAddresses(), publicAddresses);

      verify(addresses);
      verify(serverStateToNodeState);
      verify(server);
      verify(jcImage);
   }

}
