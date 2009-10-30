package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObject implements Function<Blob, CFObject> {
   private final ResourceToObjectInfo blob2ObjectMd;
   private final CFObject.Factory objectProvider;

   @Inject
   BlobToObject(ResourceToObjectInfo blob2ObjectMd, CFObject.Factory objectProvider) {
      this.blob2ObjectMd = blob2ObjectMd;
      this.objectProvider = objectProvider;
   }

   public CFObject apply(Blob from) {
      CFObject object = objectProvider.create(blob2ObjectMd.apply(from.getMetadata()));
      if (from.getContentLength() != null)
         object.setContentLength(from.getContentLength());
      object.setData(from.getData());
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}