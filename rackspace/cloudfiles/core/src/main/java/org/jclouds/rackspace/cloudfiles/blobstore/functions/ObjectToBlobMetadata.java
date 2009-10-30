package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.http.HttpUtils;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements
         Function<MutableObjectInfoWithMetadata, MutableBlobMetadata> {
   public MutableBlobMetadata apply(MutableObjectInfoWithMetadata from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setContentMD5(from.getHash());
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      if (from.getHash() != null)
         to.setETag(HttpUtils.toHexString(from.getHash()));
      to.setName(from.getName());
      if (from.getBytes() != null)
         to.setSize(from.getBytes());
      to.setType(ResourceType.BLOB);
      to.setUserMetadata(from.getMetadata());
      if (from.getContentType() != null && from.getContentType().equals("application/directory")) {
         to.setType(ResourceType.RELATIVE_PATH);
      }
      return to;
   }
}