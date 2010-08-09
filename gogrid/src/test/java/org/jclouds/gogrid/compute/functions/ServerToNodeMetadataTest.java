package org.jclouds.gogrid.compute.functions;

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
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.gogrid.services.GridServerClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "gogrid.ServerToNodeMetadataTest")
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

      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Server server = createMock(Server.class);

      expect(server.getId()).andReturn(1000l).atLeastOnce();
      expect(server.getName()).andReturn("tag-ff").atLeastOnce();
      expect(server.getState()).andReturn(ServerState.ON).atLeastOnce();

      expect(serverStateToNodeState.get(ServerState.ON)).andReturn(NodeState.RUNNING);
      LocationImpl location = new LocationImpl(LocationScope.ZONE, "1", "US-West-1", null);
      Map<String, ? extends Location> locations = ImmutableMap.<String, Location> of("1", location);

      Map<String, Credentials> credentialsMap = createMock(Map.class);
      expect(client.getServerCredentialsList()).andReturn(credentialsMap);
      expect(credentialsMap.get("tag-ff")).andReturn(new Credentials("user", "pass"));

      expect(server.getIp()).andReturn(new Ip("127.0.0.1"));

      ServerImage image = createMock(ServerImage.class);
      expect(server.getImage()).andReturn(image).atLeastOnce();
      expect(server.getDatacenter()).andReturn(dc).atLeastOnce();
      expect(image.getId()).andReturn(2000l).atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("2000").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();

      replay(caller);
      replay(client);
      replay(serverStateToNodeState);
      replay(server);
      replay(image);
      replay(jcImage);
      replay(credentialsMap);

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState, caller, images, locations);

      NodeMetadata metadata = parser.apply(server);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);
      assertEquals(metadata.getTag(), "tag");
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
