package org.jclouds.libvirt.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.jna.Libvirt;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * defines the connection between the {@link Libvirt} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class LibvirtComputeServiceAdapter implements ComputeServiceAdapter<Domain, Domain, Image, Datacenter> {
   private final Connect client;

   @Inject
   public LibvirtComputeServiceAdapter(Connect client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Domain runNodeWithTagAndNameAndStoreCredentials(String tag, String name, Template template,
         Map<String, Credentials> credentialStore) {
      // create the backend object using parameters from the template.
      // Domain from = client.createDomainInDC(template.getLocation().getId(), name,
      // Integer.parseInt(template.getImage().getProviderId()),
      // Integer.parseInt(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      // credentialStore.put("node#" + from.id + "", new Credentials(from.loginUser, from.password));
      return null;
   }

   @Override
   public Iterable<Domain> listHardwareProfiles() {
      return listNodes();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.of();
      // TODO
      // return client.listImages();
   }

   @Override
   public Iterable<Domain> listNodes() {
      try {
         List<Domain> domains = Lists.newArrayList();
         for (int domain : client.listDomains()) {
            domains.add(client.domainLookupByID(domain));
         }
         return domains;
      } catch (LibvirtException e) {
         return propogate(e);
      }
   }

   protected <T> T propogate(LibvirtException e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return ImmutableSet.of(new Datacenter(1, "SFO"));
   }

   @Override
   public Domain getNode(String id) {
      try {
         return client.domainLookupByUUIDString(id);
      } catch (LibvirtException e) {
         return propogate(e);
      }
   }

   @Override
   public void destroyNode(String id) {
      try {
         client.domainLookupByUUIDString(id).destroy();
      } catch (LibvirtException e) {
         propogate(e);
      }
   }

   @Override
   public void rebootNode(String id) {
      try {
         client.domainLookupByUUIDString(id).reboot(0);
      } catch (LibvirtException e) {
         propogate(e);
      }
   }
}
