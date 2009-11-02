package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObject implements Function<Blob, AtmosObject> {
   private final BlobMetadataToObject blobMd2Object;

   @Inject
   BlobToObject(BlobMetadataToObject blobMd2Object) {
      this.blobMd2Object = blobMd2Object;
   }

   public AtmosObject apply(Blob from) {
      AtmosObject object = blobMd2Object.apply(from.getMetadata());
      object.setData(from.getData());
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}