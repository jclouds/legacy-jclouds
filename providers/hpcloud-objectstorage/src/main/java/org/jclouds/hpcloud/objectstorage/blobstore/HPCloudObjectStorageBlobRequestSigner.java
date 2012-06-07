package org.jclouds.hpcloud.objectstorage.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageAsyncClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.internal.RestAnnotationProcessor;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class HPCloudObjectStorageBlobRequestSigner implements BlobRequestSigner {
   private final RestAnnotationProcessor<HPCloudObjectStorageAsyncClient> processor;
   private final BlobToObject blobToObject;
   private final BlobToHttpGetOptions blob2HttpGetOptions;

   private final Method getMethod;
   private final Method deleteMethod;
   private final Method createMethod;

   @Inject
   public HPCloudObjectStorageBlobRequestSigner(RestAnnotationProcessor<HPCloudObjectStorageAsyncClient> processor, BlobToObject blobToObject,
            BlobToHttpGetOptions blob2HttpGetOptions) throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");
      this.getMethod = HPCloudObjectStorageAsyncClient.class.getMethod("getObject", String.class, String.class,
               GetOptions[].class);
      this.deleteMethod = HPCloudObjectStorageAsyncClient.class.getMethod("removeObject", String.class, String.class);
      this.createMethod = HPCloudObjectStorageAsyncClient.class.getMethod("putObject", String.class, SwiftObject.class);

   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return cleanRequest(processor.createRequest(getMethod, container, name));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return cleanRequest(processor.createRequest(createMethod, container, blobToObject.apply(blob)));
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      return cleanRequest(processor.createRequest(deleteMethod, container, name));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      return cleanRequest(processor.createRequest(getMethod, container, name, blob2HttpGetOptions.apply(options)));
   }
}
