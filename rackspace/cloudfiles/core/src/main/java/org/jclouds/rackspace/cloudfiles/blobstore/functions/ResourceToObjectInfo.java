package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.http.HttpUtils;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.internal.MutableObjectInfoWithMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToObjectInfo implements Function<ResourceMetadata, MutableObjectInfoWithMetadata> {
   public MutableObjectInfoWithMetadata apply(ResourceMetadata base) {
      MutableObjectInfoWithMetadata to = new MutableObjectInfoWithMetadataImpl();
      if (base.getType() == ResourceType.BLOB){
         to.setContentType(((BlobMetadata)base).getContentType());
         to.setHash(((BlobMetadata)base).getContentMD5());
      } else if (base.getType() == ResourceType.RELATIVE_PATH){
         to.setContentType("application/directory");
      }
      if (base.getETag() != null && to.getHash() == null)
         to.setHash(HttpUtils.fromHexString(base.getETag()));
      to.setName(base.getName());
      to.setLastModified(base.getLastModified());
      if (base.getSize() != null)
         to.setBytes(base.getSize());
      if (base.getUserMetadata() != null)
         to.getMetadata().putAll(base.getUserMetadata());
      return to;
   }

}