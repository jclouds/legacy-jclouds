package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobMetadataToObject implements Function<BlobMetadata, AtmosObject> {
   private final AtmosObject.Factory factory;
   private final BlobToContentMetadata blob2ContentMd;
   private final BlobToSystemMetadata blob2SysMd;

   @Inject
   protected BlobMetadataToObject(AtmosObject.Factory factory,
            BlobToContentMetadata blob2ContentMd, BlobToSystemMetadata blob2SysMd) {
      this.factory = factory;
      this.blob2ContentMd = blob2ContentMd;
      this.blob2SysMd = blob2SysMd;
   }

   public AtmosObject apply(BlobMetadata base) {
      UserMetadata userMd = new UserMetadata();
      if (base.getUserMetadata() != null)
         userMd.getMetadata().putAll(base.getUserMetadata());
      return factory.create(blob2ContentMd.apply(base), blob2SysMd.apply(base), userMd);
   }

}