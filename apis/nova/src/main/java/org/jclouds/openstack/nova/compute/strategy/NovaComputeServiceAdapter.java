package org.jclouds.openstack.nova.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withMetadata;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.withDetails;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.RebootType;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.options.ListOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.JustProvider;

/**
 * defines the connection between the {@link NovaClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class NovaComputeServiceAdapter implements ComputeServiceAdapter<Server, Flavor, Image, Location> {

   protected final NovaClient client;
   protected final JustProvider locationSupplier;

   @Inject
   protected NovaComputeServiceAdapter(NovaClient client, JustProvider locationSupplier) {
      this.client = checkNotNull(client, "client");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");

   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      Server server = client.createServer(name, template.getImage().getId(), template.getHardware().getId(),
               withMetadata(template.getOptions().getUserMetadata()));

      return new NodeAndInitialCredentials<Server>(server, server.getId() + "", LoginCredentials.builder().password(
               server.getAdminPass()).build());
   }

   @Override
   public Iterable<Flavor> listHardwareProfiles() {
      return client.listFlavors(withDetails());

   }

   @Override
   public Iterable<Image> listImages() {
      return client.listImages(withDetails());
   }

   @Override
   public Iterable<Server> listNodes() {
      return client.listServers(ListOptions.Builder.withDetails());
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
   }

   @Override
   public Server getNode(String id) {
      int serverId = Integer.parseInt(id);
      return client.getServer(serverId);
   }

   @Override
   public void destroyNode(String id) {
      int serverId = Integer.parseInt(id);
      // if false server wasn't around in the first place
      client.deleteServer(serverId);
   }

   @Override
   public void rebootNode(String id) {
      int serverId = Integer.parseInt(id);
      // if false server wasn't around in the first place
      client.rebootServer(serverId, RebootType.HARD);
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

}