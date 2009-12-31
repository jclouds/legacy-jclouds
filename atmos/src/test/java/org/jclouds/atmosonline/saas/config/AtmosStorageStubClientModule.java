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
package org.jclouds.atmosonline.saas.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorage;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.internal.StubAtmosStorageAsyncClient;
import org.jclouds.blobstore.integration.config.StubBlobStoreModule;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * adds a stub alternative to invoking AtmosStorage
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AtmosStorageStubClientModule extends AbstractModule {

   protected void configure() {
      install(new StubBlobStoreModule());
      bind(AtmosStorageAsyncClient.class).to(StubAtmosStorageAsyncClient.class).asEagerSingleton();
      bind(URI.class).annotatedWith(AtmosStorage.class).toInstance(
               URI.create("https://localhost/azurestub"));
   }

   @Provides
   @Singleton
   public AtmosStorageClient provideClient(AtmosStorageAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(AtmosStorageClient.class, client);
   }
}