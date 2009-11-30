package org.jclouds.rimuhosting.miro.servers;

import org.jclouds.compute.ComputeService;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService implements ComputeService {
   RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeService(RimuHostingClient rhClient){
      this.rhClient = rhClient;
   }

   public org.jclouds.compute.Server createServerAndWait(String name, String profile, String image) {
      NewServerResponse serverResp = rhClient.createInstance(name, image, profile);
      return new RimuHostingServer(serverResp.getInstance(), rhClient);
   }

   public Future<org.jclouds.compute.Server> createServer(String name, String profile, String image) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<org.jclouds.compute.Server> listServers() {
      SortedSet<org.jclouds.compute.Server> servers = new TreeSet<org.jclouds.compute.Server>();
      SortedSet<Server> rhServers = rhClient.getInstanceList();
      for(Server rhServer : rhServers) {
         servers.add(new RimuHostingServer(rhServer,rhClient));
      }
      return servers;
   }

   public org.jclouds.compute.Server getServer(String id) {
      return new RimuHostingServer(rhClient.getInstance(Long.valueOf(id)), rhClient);
   }
}