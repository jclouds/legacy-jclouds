package org.jclouds.aws.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.internal.Iterables;

/**
 * 
 * Tests to see if a volume is attached.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VolumeAttached implements Predicate<Attachment> {

   private final ElasticBlockStoreClient client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VolumeAttached(ElasticBlockStoreClient client) {
      this.client = client;
   }

   public boolean apply(Attachment attachment) {
      logger.trace("looking for volume %s", attachment.getVolumeId());
      Volume volume = Iterables.getOnlyElement(client.describeVolumesInRegion(attachment
               .getRegion(), attachment.getVolumeId()));
      if (volume.getAttachments().size() == 0) {
         return false;
      }
      Attachment lastAttachment = Sets.newTreeSet(volume.getAttachments()).last();
      logger.trace("%s: looking for status %s: currently: %s", lastAttachment,
               Attachment.Status.ATTACHED, lastAttachment.getStatus());
      return lastAttachment.getStatus() == Attachment.Status.ATTACHED;
   }

}
