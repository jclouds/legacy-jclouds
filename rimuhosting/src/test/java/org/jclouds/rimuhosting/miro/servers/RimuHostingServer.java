package org.jclouds.rimuhosting.miro.servers;

import org.jclouds.compute.Server;
import org.jclouds.compute.Platform;
import org.jclouds.compute.Instance;
import org.jclouds.rimuhosting.miro.RimuHostingClient;

import java.util.SortedSet;

public class RimuHostingServer implements Server {
   org.jclouds.rimuhosting.miro.domain.Server rhServer;

   RimuHostingClient rhClient;

   public RimuHostingServer(org.jclouds.rimuhosting.miro.domain.Server rhServer, RimuHostingClient rhClient){
      this.rhServer = rhServer;
      this.rhClient = rhClient;
   }

   public String getId() {
      return rhServer.toString();
   }

   public Platform createPlatform(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public Platform getPlatform(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<Platform> listPlatforms() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<Instance> listInstances() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public Boolean destroyServer() {
      rhClient.destroyInstance(rhServer.getId());
      return Boolean.TRUE;
   }
}
