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
package org.jclouds.azure.storage.blob.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@RequiresHttp
public class AzureBlobContextModule extends AbstractModule {

   @Override
   protected void configure() {
      // for converters
      install(new BlobStoreObjectModule());
      install(new AzureBlobModule());
   }

   @Provides
   @Singleton
   RestContext<AzureBlobAsyncClient, AzureBlobClient> provideContext(Closer closer,
            AzureBlobAsyncClient asyncApi, AzureBlobClient defaultApi, @AzureBlob URI endPoint,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account) {
      return new RestContextImpl<AzureBlobAsyncClient, AzureBlobClient>(closer, asyncApi,
               defaultApi, endPoint, account);
   }

}