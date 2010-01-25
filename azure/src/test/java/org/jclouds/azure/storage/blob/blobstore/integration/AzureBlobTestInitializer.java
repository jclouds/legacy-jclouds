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
package org.jclouds.azure.storage.blob.blobstore.integration;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.azure.storage.blob.AzureBlobContextFactory;
import org.jclouds.azure.storage.blob.config.AzureBlobStubClientModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class AzureBlobTestInitializer extends BaseTestInitializer {

   @Override
   protected BlobStoreContext createLiveContext(Module configurationModule, String url, String app,
            String account, String key) throws IOException {
      return (BlobStoreContext) new BlobStoreContextFactory().createContext("azureblob", account,
               key, ImmutableSet.of(configurationModule, new Log4JLoggingModule()),
               new Properties());
   }

   @Override
   protected BlobStoreContext createStubContext() {
      return AzureBlobContextFactory.createContext("user", "pass",
               new AzureBlobStubClientModule());
   }

}