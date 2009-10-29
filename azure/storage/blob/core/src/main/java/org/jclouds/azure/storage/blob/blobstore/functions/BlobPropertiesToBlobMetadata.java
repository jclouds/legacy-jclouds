package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobPropertiesToBlobMetadata implements Function<BlobProperties, MutableBlobMetadata> {
   public MutableBlobMetadata apply(BlobProperties from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setContentMD5(from.getContentMD5());
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      to.setETag(from.getETag());
      to.setName(from.getName());
      to.setSize(from.getSize());
      to.setType(ResourceType.BLOB);
      to.setUserMetadata(from.getMetadata());
      if (from.getContentType() != null && from.getContentType().equals("application/directory")) {
         to.setType(ResourceType.RELATIVE_PATH);
      }
      return to;
   }
}