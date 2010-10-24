package org.jclouds.libvirt.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Hardware;
import org.jclouds.libvirt.Image;
import org.libvirt.Domain;
import org.libvirt.jna.Libvirt;

import com.google.common.collect.ImmutableSet;

/**
 * defines the connection between the {@link Libvirt} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class LibvirtComputeServiceAdapter implements ComputeServiceAdapter<Domain, Hardware, Image, Datacenter> {
   private final Libvirt client;

   @Inject
   public LibvirtComputeServiceAdapter(Libvirt client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Domain createNodeAndStoreCredentials(String tag, String name, Template template,
         Map<String, Credentials> credentialStore) {
      // create the backend object using parameters from the template.
      // Domain from = client.createDomainInDC(template.getLocation().getId(), name,
      // Integer.parseInt(template.getImage().getProviderId()),
      // Integer.parseInt(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      // credentialStore.put(from.id + "", new Credentials(from.loginUser, from.password));
      return null;
   }

   @Override
   public Iterable<Hardware> listHardware() {
      return ImmutableSet.of();
      // TODO
      // return client.listHardware();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.of();
      // TODO
      // return client.listImages();
   }

   @Override
   public Iterable<Domain> listNodes() {
      return ImmutableSet.of();
      // TODO
      // return client.listDomains();
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return ImmutableSet.of(new Datacenter(1, "SFO"));
   }

   @Override
   public Domain getNode(String id) {
      // int serverId = Integer.parseInt(id);
      // return client.getDomain(serverId);
      return null;
   }

   @Override
   public void destroyNode(String id) {
      // client.destroyDomain(Integer.parseInt(id));
   }

   @Override
   public void rebootNode(String id) {
      // client.rebootDomain(Integer.parseInt(id));
   }
}
