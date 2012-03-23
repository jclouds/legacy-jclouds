package org.jclouds.trystack.nova;

import java.util.Set;

import javax.inject.Inject;

import com.google.common.cache.LoadingCache;
import org.jclouds.location.Zone;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v1_1.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v1_1.domain.KeyPair;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ImageInZone;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;

/**
 * 
 * @author Adrian Cole
 */
public class TryStackNovaServiceAdapter extends NovaComputeServiceAdapter {

   @Inject
   public TryStackNovaServiceAdapter(NovaClient novaClient, @Zone Supplier<Set<String>> zoneIds,
            RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate, LoadingCache<ZoneAndName, KeyPair> keyPairCache) {
      super(novaClient, zoneIds, removeFloatingIpFromNodeAndDeallocate, keyPairCache);
   }

   @Override
   public Iterable<ImageInZone> listImages() {
      return Iterables.filter(super.listImages(), new Predicate<ImageInZone>() {

         @Override
         public boolean apply(ImageInZone arg0) {
            String imageName = arg0.getImage().getName();
            return imageName.indexOf("kernel") == -1;
         }

         @Override
         public String toString() {
            return "notKernel";
         }
      });
   }
}
