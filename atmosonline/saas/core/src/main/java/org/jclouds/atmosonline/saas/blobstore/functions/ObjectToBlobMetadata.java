package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.functions.AtmosObjectName;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements Function<AtmosObject, MutableBlobMetadata> {
   private final AtmosObjectName objectName;

   @Inject
   protected ObjectToBlobMetadata(AtmosObjectName objectName) {
      this.objectName = objectName;
   }

   public MutableBlobMetadata apply(AtmosObject from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setId(from.getSystemMetadata().getObjectID());
      to.setLastModified(from.getSystemMetadata().getLastUserDataModification());
      to.setContentMD5(from.getContentMetadata().getContentMD5());
      if (from.getContentMetadata().getContentType() != null)
         to.setContentType(from.getContentMetadata().getContentType());
      to.setName(objectName.apply(from));
      to.setSize(from.getSystemMetadata().getSize());
      to.setType(ResourceType.BLOB);
      to.setUserMetadata(from.getUserMetadata().getMetadata());
      return to;
   }
}