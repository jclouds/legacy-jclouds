package org.jclouds.azure.storage.blob.blobstore.functions;

import org.jclouds.azure.storage.blob.domain.AzureBlob;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class BlobName implements Function<Object, String> {

   public String apply(Object from) {
      return ((AzureBlob) from).getProperties().getName();
   }

}