package org.jclouds.hpcloud.compute;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.location.Zone;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v1_1.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ImageInZone;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class HPCloudComputeServiceAdapter extends NovaComputeServiceAdapter {

   @Inject
   public HPCloudComputeServiceAdapter(NovaClient novaClient, @Zone Supplier<Set<String>> zoneIds,
            RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate) {
      super(novaClient, zoneIds, removeFloatingIpFromNodeAndDeallocate);
   }

   @Override
   public Iterable<ImageInZone> listImages() {
      return Iterables.filter(super.listImages(), new Predicate<ImageInZone>() {

         @Override
         public boolean apply(ImageInZone arg0) {
            String imageName = arg0.getImage().getName();
            return imageName.indexOf("Kernel") == -1 && imageName.indexOf("Ramdisk") == -1;
         }

         @Override
         public String toString() {
            return "notKernelOrRamdisk";
         }
      });
   }
}
