package org.jclouds.servermanager.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.servermanager.Datacenter;
import org.jclouds.servermanager.Hardware;
import org.jclouds.servermanager.Image;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;

import com.google.common.collect.ImmutableSet;

/**
 * defines the connection between the {@link ServerManager} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class ServerManagerComputeServiceAdapter implements ComputeServiceAdapter<Server, Hardware, Image, Datacenter> {
   private final ServerManager client;

   @Inject
   public ServerManagerComputeServiceAdapter(ServerManager client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Server createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
         Map<String, Credentials> credentialStore) {
      // create the backend object using parameters from the template.
      Server from = client.createServerInDC(template.getLocation().getId(), name,
            Integer.parseInt(template.getImage().getProviderId()),
            Integer.parseInt(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      credentialStore.put(from.id + "", new Credentials(from.loginUser, from.password));
      return from;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return client.listHardware();
   }

   @Override
   public Iterable<Image> listImages() {
      return client.listImages();
   }
   
   @Override
   public Iterable<Server> listNodes() {
      return client.listServers();
   }
   
   @Override
   public Iterable<Datacenter> listLocations() {
      return ImmutableSet.of(new Datacenter(1, "SFO"));
   }

   @Override
   public Server getNode(String id) {
      int serverId = Integer.parseInt(id);
      return client.getServer(serverId);
   }

   @Override
   public void destroyNode(String id) {
      client.destroyServer(Integer.parseInt(id));
   }

   @Override
   public void rebootNode(String id) {
      client.rebootServer(Integer.parseInt(id));      
   }

   @Override
   public void resumeNode(String id) {
      client.startServer(Integer.parseInt(id));      
      
   }

   @Override
   public void suspendNode(String id) {
      client.stopServer(Integer.parseInt(id));      
   }
}