package org.jclouds.openstack.nova.v2_0.compute.predicates;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.collect.PagedIterables;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit", singleThreaded = true)
public class IterableServersStatusPredicateTest {
   @Test
   public void testServerStatusPredicateActiveWithServerActiveOneServer() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive = newServerWithStatus(Server.Status.ACTIVE, "1");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverActive);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertTrue(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   @Test
   public void testIterableServersStatusPredicateActiveWithServerBuildOneServer() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverBuild = newServerWithStatus(Server.Status.BUILD, "1");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverBuild);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   @Test
   public void testServerStatusPredicateActiveWithServerActiveTwoServers() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive1 = newServerWithStatus(Server.Status.ACTIVE, "1");
      Server serverActive2 = newServerWithStatus(Server.Status.ACTIVE, "2");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverActive1);
      servers.add(serverActive2);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertTrue(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   @Test
   public void testIterableServersStatusPredicateActiveWithServerBuildTwoServers() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverBuild = newServerWithStatus(Server.Status.BUILD, "1");
      Server serverActive = newServerWithStatus(Server.Status.ACTIVE, "2");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverBuild);
      servers.add(serverActive);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }
   
   @Test
   public void testServerStatusPredicateActiveWithServerActiveThreeServers() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive1 = newServerWithStatus(Server.Status.ACTIVE, "1");
      Server serverActive2 = newServerWithStatus(Server.Status.ACTIVE, "2");
      Server serverActive3 = newServerWithStatus(Server.Status.ACTIVE, "3");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverActive1);
      servers.add(serverActive2);
      servers.add(serverActive3);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertTrue(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   @Test
   public void testIterableServersStatusPredicateActiveWithServerBuildThreeServers() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive1 = newServerWithStatus(Server.Status.ACTIVE, "1");
      Server serverActive2 = newServerWithStatus(Server.Status.ACTIVE, "2");
      Server serverBuild = newServerWithStatus(Server.Status.BUILD, "3");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverActive1);
      servers.add(serverActive2);
      servers.add(serverBuild);
      Servers paginatedCollection = new Servers(servers);
      
      expect(serverApi.listInDetail()).andReturn(PagedIterables.of(paginatedCollection));
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   @Test
   public void testServerStatusPredicateActiveWithServerMissing() {
      ServerApi serverApi = createMock(ServerApi.class);
      Server serverActive = newServerWithStatus(Server.Status.ACTIVE, "1");
      Set<Resource> servers = new HashSet<Resource>();
      servers.add(serverActive);
      
      expect(serverApi.listInDetail()).andThrow(new ResourceNotFoundException());
      replay(serverApi);
      
      IterableServersStatusPredicate iterableServersStatusPredicate = 
         new IterableServersStatusPredicate(serverApi, Server.Status.ACTIVE);

      assertFalse(iterableServersStatusPredicate.apply(servers));
      verify(serverApi);
   }

   private Server newServerWithStatus(Server.Status status, String id) {
      return Server.builder()
         .id(id)
         .status(status)
         .tenantId("1")
         .userId("1")
         .created(new Date())
         .image(Resource.builder().id("1").build())
         .flavor(Resource.builder().id("1").build())
         .build();
   }

   static class Servers<T extends Server> extends PaginatedCollection<T> {
      public Servers(Iterable<T> servers) {
         super(servers, null);
      }
   }
}
