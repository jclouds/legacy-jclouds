package org.jclouds.openstack.nova.v2_0.compute.predicates;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit", singleThreaded = true)
public class ServerStatusPredicateTest {

   @Test
   public void testServerStatusPredicateActiveWithServerActive() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive = newServerWithStatus(Server.Status.ACTIVE);
      
      expect(serverApi.get(serverActive.getId())).andReturn(serverActive);
      replay(serverApi);
      
      ServerStatusPredicate serverStatusPredicateActive = new ServerStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertTrue(serverStatusPredicateActive.apply(serverActive));
      verify(serverApi);
   }

   @Test
   public void testServerStatusPredicateActiveWithServerBuild() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverBuild = newServerWithStatus(Server.Status.BUILD);
      
      expect(serverApi.get(serverBuild.getId())).andReturn(serverBuild);
      replay(serverApi);
      
      ServerStatusPredicate serverStatusPredicateActive = new ServerStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(serverStatusPredicateActive.apply(serverBuild));
      verify(serverApi);
   }
   
   @Test
   public void testServerStatusPredicateActiveWithServerMissing() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive = newServerWithStatus(Server.Status.ACTIVE);
      
      expect(serverApi.get(serverActive.getId())).andThrow(new ResourceNotFoundException());
      replay(serverApi);
      
      ServerStatusPredicate serverStatusPredicateActive = new ServerStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(serverStatusPredicateActive.apply(serverActive));
      verify(serverApi);
   }

   private Server newServerWithStatus(Server.Status status) {
      return Server.builder()
         .id("1")
         .status(status)
         .tenantId("1")
         .userId("1")
         .created(new Date())
         .image(Resource.builder().id("1").build())
         .flavor(Resource.builder().id("1").build())
         .build();
   }
}
