package org.jclouds.rimuhosting.miro.servers;

import org.jclouds.servers.ServerService;
import org.jclouds.servers.Server;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Instance;
import org.jclouds.rimuhosting.miro.domain.NewInstanceResponse;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingServerService implements ServerService {
   RimuHostingClient rhClient;

   @Inject
   public RimuHostingServerService(RimuHostingClient rhClient){
      this.rhClient = rhClient;
   }

   public Server createServerAndWait(String name, String profile, String image) {
      NewInstanceResponse instanceResp = rhClient.createInstance(name, image, profile);
      return new RimuHostingServer(instanceResp.getInstance(), rhClient);
   }

   public Future<Server> createServer(String name, String profile, String image) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<Server> listServers() {
      SortedSet<Server> servers = new TreeSet<Server>();
      SortedSet<Instance> rhServers = rhClient.getInstanceList();
      for(Instance rhServer : rhServers) {
         servers.add(new RimuHostingServer(rhServer,rhClient));
      }
      return servers;
   }

   public Server getServer(String id) {
      return new RimuHostingServer(rhClient.getInstance(Long.valueOf(id)), rhClient);
   }
}