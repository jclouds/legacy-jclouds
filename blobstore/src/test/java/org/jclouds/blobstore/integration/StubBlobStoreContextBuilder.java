/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.integration;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.integration.config.StubBlobStoreModule;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public class StubBlobStoreContextBuilder extends BlobStoreContextBuilder<AsyncBlobStore, BlobStore> {

   /**
    * This is only to have the same syntax.
    * 
    */
   public StubBlobStoreContextBuilder(Properties props) {
      this();
   }

   public StubBlobStoreContextBuilder() {
      super(new TypeLiteral<AsyncBlobStore>() {
      }, new TypeLiteral<BlobStore>() {
      });
   }

   @Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
   private static interface StubBlobStore extends BlobStore {

   }

   @Override
   public void addContextModule(List<Module> modules) {
      modules.add(new BlobStoreObjectModule());
      modules.add(new BlobStoreMapModule());
      modules.add(new AbstractModule() {

         @Override
         protected void configure() {
            bind(AsyncBlobStore.class).to(StubAsyncBlobStore.class).asEagerSingleton();
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BlobStore provideClient(AsyncBlobStore client) throws IllegalArgumentException,
                  SecurityException, NoSuchMethodException {
            return SyncProxy.create(StubBlobStore.class, client);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         RestContext<AsyncBlobStore, BlobStore> provideContext(Closer closer,
                  final AsyncBlobStore async, final BlobStore sync) {
            return new RestContextImpl<AsyncBlobStore, BlobStore>(closer, async, sync, URI
                     .create("http://localhost/blobstub"), "foo");
         }

      });
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new StubBlobStoreModule());
   }

}