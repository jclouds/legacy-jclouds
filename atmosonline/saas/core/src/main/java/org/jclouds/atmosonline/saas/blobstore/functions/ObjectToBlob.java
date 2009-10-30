package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlob implements Function<AtmosObject, Blob> {
   private final Blob.Factory blobFactory;
   private final ObjectToBlobMetadata object2BlobMd;

   @Inject
   ObjectToBlob(Factory blobFactory, ObjectToBlobMetadata object2BlobMd) {
      this.blobFactory = blobFactory;
      this.object2BlobMd = object2BlobMd;
   }

   public Blob apply(AtmosObject from) {
      Blob blob = blobFactory.create(object2BlobMd.apply(from));
      if (from.getContentMetadata().getContentLength() != null)
         blob.setContentLength(from.getContentMetadata().getContentLength());
      blob.setData(from.getData());
      blob.setAllHeaders(from.getAllHeaders());
      return blob;
   }
}
