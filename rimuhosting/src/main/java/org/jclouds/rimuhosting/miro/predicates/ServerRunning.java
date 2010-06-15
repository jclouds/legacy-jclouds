package org.jclouds.rimuhosting.miro.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerRunning implements Predicate<Server> {

   private final RimuHostingClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public ServerRunning(RimuHostingClient client) {
      this.client = client;
   }

   public boolean apply(Server server) {
      logger.trace("looking for state on server %s", checkNotNull(server,
            "server"));
      server = refresh(server);
      if (server == null)
         return false;
      logger.trace("%s: looking for server state %s: currently: %s", server
            .getId(), RunningState.RUNNING, server.getState());
      return server.getState() == RunningState.RUNNING;
   }

   private Server refresh(Server server) {
      return client.getServer(server.getId());
   }
}
