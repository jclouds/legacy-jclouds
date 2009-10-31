package org.jclouds.azure.storage.blob.blobstore.strategy;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.util.Utils;

/**
 * Searches Content-MD5 tag for the value associated with the value
 * 
 * @author Adrian Cole
 */
@Singleton
public class FindMD5InBlobProperties implements ContainsValueInListStrategy {

   protected final ObjectMD5 objectMD5;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   private final AzureBlobClient client;

   @Inject
   private FindMD5InBlobProperties(ObjectMD5 objectMD5,
            ListBlobMetadataStrategy getAllBlobMetadata, AzureBlobClient client) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.client = client;
   }

   public boolean execute(String containerName, Object value, ListOptions options) {
      try {
         byte[] toSearch = objectMD5.apply(value);
         for (BlobMetadata metadata : getAllBlobMetadata.execute(containerName, options)) {
            BlobProperties properties = client.getBlobProperties(containerName, metadata.getName());
            if (Arrays.equals(toSearch, properties.getContentMD5()))
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