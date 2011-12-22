package org.jclouds.slicehost.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Image;
import org.jclouds.slicehost.domain.Slice;

/**
 * defines the connection between the {@link SlicehostClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SlicehostComputeServiceAdapter implements ComputeServiceAdapter<Slice, Flavor, Image, Location> {

   protected final SlicehostClient client;
   protected final JustProvider locationSupplier;

   @Inject
   protected SlicehostComputeServiceAdapter(SlicehostClient client, JustProvider locationSupplier) {
      this.client = checkNotNull(client, "client");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");

   }

   @Override
   public NodeAndInitialCredentials<Slice> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      Slice server = client
               .createSlice(name, Integer.parseInt(template.getImage().getProviderId()), Integer.parseInt(template
                        .getHardware().getProviderId()));

      return new NodeAndInitialCredentials<Slice>(server, server.getId() + "", LoginCredentials.builder().password(
               server.getRootPassword()).build());
   }

   @Override
   public Iterable<Flavor> listHardwareProfiles() {
      return client.listFlavors();

   }

   @Override
   public Iterable<Image> listImages() {
      return client.listImages();
   }

   @Override
   public Iterable<Slice> listNodes() {
      return client.listSlices();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
   }

   @Override
   public Slice getNode(String id) {
      int serverId = Integer.parseInt(id);
      return client.getSlice(serverId);
   }

   @Override
   public void destroyNode(String id) {
      int serverId = Integer.parseInt(id);
      // if false server wasn't around in the first place
      client.destroySlice(serverId);
   }

   @Override
   public void rebootNode(String id) {
      int sliceId = Integer.parseInt(id);
      client.hardRebootSlice(sliceId);
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