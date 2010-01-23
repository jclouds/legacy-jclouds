package org.jclouds.rackspace.cloudservers.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerDeleted implements Predicate<Server> {

   private final CloudServersClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public ServerDeleted(CloudServersClient client) {
      this.client = client;
   }

   public boolean apply(Server server) {
      logger.trace("looking for state on server %s", server);
      try {
         server = refresh(server);
         logger.trace("%s: looking for server state %s: currently: %s", server.getId(),
                  ServerStatus.DELETED, server.getStatus());
         return server.getStatus() == ServerStatus.DELETED;
      } catch (ResourceNotFoundException e) {
         return true;
      }
   }

   private Server refresh(Server server) {
      return client.getServer(server.getId());
   }
}
