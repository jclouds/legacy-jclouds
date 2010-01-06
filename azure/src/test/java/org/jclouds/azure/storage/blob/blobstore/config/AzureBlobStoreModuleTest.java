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
package org.jclouds.azure.storage.blob.blobstore.config;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.strategy.FindMD5InBlobProperties;
import org.jclouds.azure.storage.blob.config.AzureBlobStubClientModule;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobStoreModuleTest")
public class AzureBlobStoreModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new ExecutorServiceModule(sameThreadExecutor()),
               new JDKLoggingModule(), new AzureBlobStubClientModule(),
               new AzureBlobStoreContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT))
                              .to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                              "key");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT)).to(
                              "http://localhost");
                     super.configure();
                  }
               });
   }

   @Test
   void testContextImpl() {

      Injector injector = createInjector();
      BlobStoreContext<AzureBlobAsyncClient, AzureBlobClient> handler = injector.getInstance(Key
               .get(new TypeLiteral<BlobStoreContext<AzureBlobAsyncClient, AzureBlobClient>>() {
               }));
      assertEquals(handler.getClass(), BlobStoreContextImpl.class);
      ContainsValueInListStrategy valueList = injector
               .getInstance(ContainsValueInListStrategy.class);

      assertEquals(valueList.getClass(), FindMD5InBlobProperties.class);
   }

}