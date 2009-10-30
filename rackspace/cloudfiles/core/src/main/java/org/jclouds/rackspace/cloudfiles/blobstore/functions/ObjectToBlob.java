package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlob implements Function<CFObject, Blob> {
   private final Blob.Factory blobFactory;
   private final ObjectToBlobMetadata object2BlobMd;

   @Inject
   ObjectToBlob(Factory blobFactory, ObjectToBlobMetadata object2BlobMd) {
      this.blobFactory = blobFactory;
      this.object2BlobMd = object2BlobMd;
   }

   public Blob apply(CFObject from) {
      Blob blob = blobFactory.create(object2BlobMd.apply(from.getInfo()));
      if (from.getContentLength() != null)
         blob.setContentLength(from.getContentLength());
      blob.setData(from.getData());
      blob.setAllHeaders(from.getAllHeaders());
      return blob;
   }
}
