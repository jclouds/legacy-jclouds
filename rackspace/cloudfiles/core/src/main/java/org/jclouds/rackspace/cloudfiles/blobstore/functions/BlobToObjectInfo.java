package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.internal.MutableObjectInfoWithMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObjectInfo implements Function<BlobMetadata, MutableObjectInfoWithMetadata> {
   public MutableObjectInfoWithMetadata apply(BlobMetadata base) {
      MutableObjectInfoWithMetadata to = new MutableObjectInfoWithMetadataImpl();
      to.setContentType(base.getContentType());
      to.setHash(base.getContentMD5());
      to.setName(base.getName());
      to.setLastModified(base.getLastModified());
      if (base.getSize() != null)
         to.setBytes(base.getSize());
      if (base.getUserMetadata() != null)
         to.getMetadata().putAll(base.getUserMetadata());
      return to;
   }

}