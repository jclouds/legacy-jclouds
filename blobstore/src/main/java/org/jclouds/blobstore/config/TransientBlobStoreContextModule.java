package org.jclouds.blobstore.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TransientBlobStoreContext}; requires
 * {@link TransientAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreContextModule extends
      RestClientModule<TransientBlobStore, AsyncBlobStore> {

   public TransientBlobStoreContextModule() {
      super(TransientBlobStore.class, AsyncBlobStore.class);
   }

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(BlobStoreContext.class)
            .to(
                  new TypeLiteral<BlobStoreContextImpl<TransientBlobStore, AsyncBlobStore>>() {
                  }).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   BlobStore provide(TransientBlobStore in) {
      return in;
   }

   @Provides
   @Singleton
   Location provideDefaultLocation() {
      return new LocationImpl(LocationScope.PROVIDER, "transient", "transient",
            null);
   }

   @Override
   protected void bindAsyncClient() {
      bind(AsyncBlobStore.class).to(TransientAsyncBlobStore.class)
            .asEagerSingleton();
   }

   @Provides
   @Singleton
   RestContext<TransientBlobStore, AsyncBlobStore> provideContext(
         Closer closer, HttpClient http, HttpAsyncClient asyncHttp,
         TransientBlobStore sync, AsyncBlobStore async) {
      return new RestContextImpl<TransientBlobStore, AsyncBlobStore>(closer,
            http, asyncHttp, sync, async, URI
                  .create("http://localhost/transient"), System
                  .getProperty("user.name"));
   }

}
