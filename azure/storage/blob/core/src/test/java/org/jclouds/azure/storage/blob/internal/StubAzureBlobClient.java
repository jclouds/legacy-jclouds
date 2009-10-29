package org.jclouds.azure.storage.blob.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.http.options.GetOptions;

/**
 * Implementation of {@link AzureBlobClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubAzureBlobClient implements AzureBlobClient {

   public Future<Boolean> createContainer(String container, CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> createRootContainer(CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteBlob(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteContainer(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> deleteRootContainer() {
      throw new UnsupportedOperationException();
   }

   public Future<AzureBlob> getBlob(String container, String key, GetOptions... options) {
      throw new UnsupportedOperationException();
   }

   public BlobProperties getBlobProperties(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public ListableContainerProperties getContainerProperties(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<ListBlobsResponse> listBlobs(String container, ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<ListBlobsResponse> listBlobs(ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public BoundedList<ListableContainerProperties> listContainers(ListOptions... listOptions) {
      throw new UnsupportedOperationException();
   }

   public AzureBlob newBlob() {
      throw new UnsupportedOperationException();
   }

   public Future<String> putBlob(String container, AzureBlob object) {
      throw new UnsupportedOperationException();
   }

   public void setBlobMetadata(String container, String key, Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public void setResourceMetadata(String container, Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

}
