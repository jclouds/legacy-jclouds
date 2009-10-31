package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToAzureBlob implements Function<Blob, AzureBlob> {
   private final BlobMetadataToBlobProperties blob2ObjectMd;
   private final AzureBlob.Factory objectProvider;

   @Inject
   BlobToAzureBlob(BlobMetadataToBlobProperties blob2ObjectMd, AzureBlob.Factory objectProvider) {
      this.blob2ObjectMd = blob2ObjectMd;
      this.objectProvider = objectProvider;
   }

   public AzureBlob apply(Blob from) {
      AzureBlob object = objectProvider.create(blob2ObjectMd.apply(from.getMetadata()));
      if (from.getContentLength() != null)
         object.setContentLength(from.getContentLength());
      object.setData(from.getData());
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}