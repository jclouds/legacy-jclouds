package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToContentMetadata implements Function<BlobMetadata, MutableContentMetadata> {
   public MutableContentMetadata apply(BlobMetadata base) {
      MutableContentMetadata to = new MutableContentMetadata();
      to.setContentType(base.getContentType());
      to.setContentMD5(base.getContentMD5());
      to.setName(base.getName());
      if (base.getSize() != null)
         to.setContentLength(base.getSize());
      return to;
   }

}