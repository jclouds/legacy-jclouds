package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ListableBlobPropertiesToBlobMetadata<T extends ListableBlobProperties> implements
         Function<T, MutableBlobMetadata> {
   public MutableBlobMetadata apply(T from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      to.setETag(from.getETag());
      to.setName(from.getName());
      to.setSize(from.getSize());
      to.setType(ResourceType.BLOB);
      if (from.getContentType() != null && from.getContentType().equals("application/directory")) {
         to.setType(ResourceType.RELATIVE_PATH);
      }
      return to;
   }
}