package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.blobstore.domain.MutableBlobMetadata;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobPropertiesToBlobMetadata extends
         ListableBlobPropertiesToBlobMetadata<BlobProperties> {
   @Override
   public MutableBlobMetadata apply(BlobProperties from) {
      MutableBlobMetadata to = super.apply(from);
      to.setContentMD5(from.getContentMD5());
      to.setUserMetadata(from.getMetadata());
      return to;
   }
}