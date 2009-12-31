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
package org.jclouds.atmosonline.saas.blobstore.integration;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.AtmosStoragePropertiesBuilder;
import org.jclouds.atmosonline.saas.blobstore.AtmosBlobStoreContextBuilder;
import org.jclouds.atmosonline.saas.blobstore.AtmosBlobStoreContextFactory;
import org.jclouds.atmosonline.saas.config.AtmosStorageStubClientModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class AtmosStorageTestInitializer extends
         BaseTestInitializer<AtmosStorageAsyncClient, AtmosStorageClient> {

   @Override
   protected BlobStoreContext<AtmosStorageAsyncClient, AtmosStorageClient> createLiveContext(
            Module configurationModule, String url, String app, String account, String key) {
      return new AtmosBlobStoreContextBuilder(new AtmosStoragePropertiesBuilder(account, key)
               .relaxSSLHostname().build()).withModules(configurationModule,
               new Log4JLoggingModule()).buildContext();
   }

   @Override
   protected BlobStoreContext<AtmosStorageAsyncClient, AtmosStorageClient> createStubContext() {
      return AtmosBlobStoreContextFactory.createContext("user", "pass",
               new AtmosStorageStubClientModule());
   }

}