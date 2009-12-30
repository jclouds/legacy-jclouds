package org.jclouds.aws.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.internal.Iterables;

/**
 * 
 * Tests to see if a volume is completed.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VolumeAvailable implements Predicate<Volume> {

   private final ElasticBlockStoreClient client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VolumeAvailable(ElasticBlockStoreClient client) {
      this.client = client;
   }

   public boolean apply(Volume volume) {
      logger.trace("looking for status on volume %s", volume.getId());
      volume = Iterables.getOnlyElement(client.describeVolumesInRegion(volume.getRegion(), volume
               .getId()));
      logger.trace("%s: looking for status %s: currently: %s", volume, Volume.Status.AVAILABLE,
               volume.getStatus());
      return volume.getStatus() == Volume.Status.AVAILABLE;
   }

}
