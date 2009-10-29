package org.jclouds.blobstore.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all Blob implementations
 * 
 * @author Adrian Cole
 */
public class BlobStoreObjectModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      bind(Blob.Factory.class).to(BlobFactory.class).in(Scopes.SINGLETON);
   }

   private static class BlobFactory implements Blob.Factory {
      @Inject
      GenerateMD5Result generateMD5Result;
      @Inject
      GenerateMD5 generateMD5;
      @Inject
      CalculateSize calculateSize;
      @Inject
      Provider<MutableBlobMetadata> metadataProvider;

      public Blob create(MutableBlobMetadata metadata) {
         return new BlobImpl(generateMD5Result, generateMD5, calculateSize,
                  metadata != null ? metadata : metadataProvider.get());
      }
   }

   @Provides
   Blob provideBlob(Blob.Factory factory) {
      return factory.create(null);
   }

}