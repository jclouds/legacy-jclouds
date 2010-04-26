package org.jclouds.blobstore.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TransientBlobStoreContext}; requires {@link TransientAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreObjectModule<AsyncBlobStore, BlobStore>(
               new TypeLiteral<AsyncBlobStore>() {
               }, new TypeLiteral<BlobStore>() {
               }));
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(TransientAsyncBlobStore.class).asEagerSingleton();
      bind(BlobStoreContext.class).to(
               new TypeLiteral<BlobStoreContextImpl<AsyncBlobStore, BlobStore>>() {
               }).in(Scopes.SINGLETON);
   }

   @Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
   private static interface TransientBlobStore extends BlobStore {

   }

   @Provides
   @Singleton
   Location provideDefaultLocation() {
      return new LocationImpl(LocationScope.ZONE, "default", "description", null);
   }

   @Provides
   @Singleton
   public BlobStore provideClient(AsyncBlobStore client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(TransientBlobStore.class, client);
   }

   @Provides
   @Singleton
   RestContext<AsyncBlobStore, BlobStore> provideContext(Closer closer, final AsyncBlobStore async,
            final BlobStore sync) {
      return new RestContextImpl<AsyncBlobStore, BlobStore>(closer, async, sync, URI
               .create("http://localhost/transient"), System.getProperty("user.name"));
   }

}
