package org.jclouds.gogrid.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.services.GridServerClient;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "gogrid.ServerToNodeMetadataTest")
public class ServerToNodeMetadataTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testApplySetsTagFromNameAndCredentialsFromName() throws UnknownHostException {
      GridServerClient client = createMock(GridServerClient.class);
      Map<String, NodeState> serverStateToNodeState = createMock(Map.class);
      Map<String, org.jclouds.compute.domain.Image> images = createMock(Map.class);
      Server server = createMock(Server.class);

      expect(server.getId()).andReturn(1000l).atLeastOnce();
      expect(server.getName()).andReturn("tag").atLeastOnce();
      expect(server.getState()).andReturn(new Option("RUNNING")).atLeastOnce();

      expect(serverStateToNodeState.get("RUNNING")).andReturn(NodeState.RUNNING);
      Location location = new LocationImpl(LocationScope.ZONE, "sanfran", "description", null);

      Map<String, Credentials> credentialsMap = createMock(Map.class);
      expect(client.getServerCredentialsList()).andReturn(credentialsMap);
      expect(credentialsMap.get("tag")).andReturn(new Credentials("user", "pass"));

      expect(server.getIp()).andReturn(
               new Ip(InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 })));

      ServerImage image = createMock(ServerImage.class);
      expect(server.getImage()).andReturn(image).atLeastOnce();
      expect(image.getId()).andReturn(2000l).atLeastOnce();

      
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);
      expect(images.get("2000")).andReturn(jcImage);
      
      
      replay(client);
      replay(serverStateToNodeState);
      replay(server);
      replay(image);
      replay(credentialsMap);
      replay(images);

      ServerToNodeMetadata parser = new ServerToNodeMetadata(serverStateToNodeState, client,
               images, location);

      NodeMetadata metadata = parser.apply(server);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);
      assertEquals(metadata.getTag(), "tag");
      assertEquals(metadata.getCredentials(), new Credentials("user", "pass"));

      verify(client);
      verify(serverStateToNodeState);
      verify(image);
      verify(credentialsMap);
      verify(server);
      verify(images);
   }

}
