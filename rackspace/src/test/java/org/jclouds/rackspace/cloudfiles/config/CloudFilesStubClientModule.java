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
package org.jclouds.rackspace.cloudfiles.config;

import javax.inject.Singleton;

import org.jclouds.blobstore.config.TransientBlobStoreModule;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.internal.StubCloudFilesAsyncClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@ConfiguresRestClient
public class CloudFilesStubClientModule extends AbstractModule {

   protected void configure() {
      install(new ParserModule());
      install(new TransientBlobStoreModule());
      bind(CloudFilesAsyncClient.class).to(StubCloudFilesAsyncClient.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   public CloudFilesClient provideClient(CloudFilesAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(CloudFilesClient.class, client);
   }

}