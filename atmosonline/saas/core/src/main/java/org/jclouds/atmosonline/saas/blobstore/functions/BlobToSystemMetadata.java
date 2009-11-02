package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToSystemMetadata implements Function<BlobMetadata, SystemMetadata> {
   public SystemMetadata apply(BlobMetadata base) {
      return new SystemMetadata(null, base.getLastModified(), null, null, null, 1, null, base
               .getName(), null, (base.getSize() != null) ? base.getSize() : 0, FileType.REGULAR,
               "root");
   }

}