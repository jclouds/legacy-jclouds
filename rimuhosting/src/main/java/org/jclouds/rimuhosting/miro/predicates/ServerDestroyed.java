package org.jclouds.rimuhosting.miro.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerDestroyed implements Predicate<Server> {

   private final RimuHostingClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public ServerDestroyed(RimuHostingClient client) {
      this.client = client;
   }

   public boolean apply(Server server) {
      server = refresh(server);
      if (server == null)
         return true;
    return false;
   }

   private Server refresh(Server server) {
      return client.getServer(server.getId());
   }
}
