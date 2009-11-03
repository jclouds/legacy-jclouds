package org.jclouds.atmosonline.saas.blobstore.functions;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.functions.AtmosObjectName;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements Function<AtmosObject, MutableBlobMetadata> {
   private final AtmosObjectName objectName;
   private static final Set<String> systemMetadata = ImmutableSet.of("atime", "mtime", "ctime",
            "itime", "type", "uid", "gid", "objectid", "objname", "size", "nlink", "policyname",
            "content-md5");

   @Inject
   protected ObjectToBlobMetadata(AtmosObjectName objectName) {
      this.objectName = objectName;
   }

   public MutableBlobMetadata apply(AtmosObject from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setId(from.getSystemMetadata().getObjectID());
      to.setLastModified(from.getSystemMetadata().getLastUserDataModification());
      String md5hex = from.getUserMetadata().getMetadata().get("content-md5");
      if (md5hex != null)
         to.setContentMD5(HttpUtils.fromHexString(md5hex));
      if (from.getContentMetadata().getContentType() != null)
         to.setContentType(from.getContentMetadata().getContentType());
      to.setName(objectName.apply(from));
      to.setSize(from.getSystemMetadata().getSize());
      to.setType(ResourceType.BLOB);
      Map<String, String> lowerKeyMetadata = Maps.newHashMap();
      for (Entry<String, String> entry : from.getUserMetadata().getMetadata().entrySet()) {
         String key = entry.getKey().toLowerCase();
         if (!systemMetadata.contains(key))
            lowerKeyMetadata.put(key, entry.getValue());
      }
      to.setUserMetadata(lowerKeyMetadata);
      return to;
   }
}