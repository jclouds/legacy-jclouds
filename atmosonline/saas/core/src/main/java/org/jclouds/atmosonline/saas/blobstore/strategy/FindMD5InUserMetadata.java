package org.jclouds.atmosonline.saas.blobstore.strategy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.Utils;

/**
 * Searches Content-MD5 tag for the value associated with the value
 * 
 * @author Adrian Cole
 */
@Singleton
public class FindMD5InUserMetadata implements ContainsValueInListStrategy {

   protected final ObjectMD5 objectMD5;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   private final AtmosStorageClient client;

   @Inject
   private FindMD5InUserMetadata(ObjectMD5 objectMD5,
            ListBlobMetadataStrategy getAllBlobMetadata, AtmosStorageClient client) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.client = client;
   }

   public boolean execute(String containerName, Object value, ListContainerOptions options) {
      try {
         byte[] toSearch = objectMD5.apply(value);
         String hex = HttpUtils.toHexString(toSearch);
         for (BlobMetadata metadata : getAllBlobMetadata.execute(containerName, options)) {
            UserMetadata properties = client.getUserMetadata(containerName+"/"+metadata.getName());
            if (hex.equals(properties.getMetadata().get("content-md5")))
               return true;
         }
         return false;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format(
                  "Error searching for ETAG of value: [%2$s] in container:%1$s", containerName,
                  value), e);
      }
   }

}