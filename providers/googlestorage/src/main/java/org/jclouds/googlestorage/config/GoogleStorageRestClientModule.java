package org.jclouds.googlestorage.config;

import javax.inject.Singleton;

import org.jclouds.googlestorage.GoogleStorageAsyncClient;
import org.jclouds.googlestorage.binders.BindGoogleStorageObjectMetadataToRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.config.S3RestClientModule;

import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class GoogleStorageRestClientModule extends S3RestClientModule<S3Client, GoogleStorageAsyncClient> {

   public GoogleStorageRestClientModule() {
      super(S3Client.class, GoogleStorageAsyncClient.class);
   }

   @Override
   protected void configure() {
      bind(BindS3ObjectMetadataToRequest.class).to(BindGoogleStorageObjectMetadataToRequest.class);
      super.configure();
   }

   @Provides
   @Singleton
   S3AsyncClient provideS3AsyncClient(GoogleStorageAsyncClient in) {
      return in;
   }

}
