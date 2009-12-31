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
package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.azure.storage.blob.config.AzureBlobStubClientModule;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.internal.StubAzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in AzureBlobContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobContextBuilderTest")
public class AzureBlobContextBuilderTest {

   public void testNewBuilder() {
      AzureBlobContextBuilder builder = new AzureBlobContextBuilder(new AzureBlobPropertiesBuilder(
               "id", "secret").build()).withModules(new AzureBlobStubClientModule());
      assertEquals(builder.getProperties().getProperty(
               AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX), "x-ms-meta-");
      assertEquals(builder.getProperties().getProperty(
               AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT), "id");
      assertEquals(builder.getProperties().getProperty(
               AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY), "secret");
   }

   public void testBuildContext() {
      RestContext<AzureBlobAsyncClient, AzureBlobClient> context = new AzureBlobContextBuilder(
               new AzureBlobPropertiesBuilder("id", "secret").build()).withModules(
               new AzureBlobStubClientModule()).buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getAsyncApi().getClass(), StubAzureBlobAsyncClient.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/azurestub"));
   }

   public void testBuildInjector() {
      Injector i = new AzureBlobContextBuilder(new AzureBlobPropertiesBuilder("id", "secret")
               .build()).withModules(new AzureBlobStubClientModule()).buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<AzureBlobAsyncClient, AzureBlobClient>>() {
      })) != null;
      assert i.getInstance(AzureBlob.class) != null;
      assert i.getInstance(Blob.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobContextBuilder builder = new AzureBlobContextBuilder(new AzureBlobPropertiesBuilder(
               "id", "secret").build());
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AzureBlobContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobContextBuilder builder = new AzureBlobContextBuilder(new AzureBlobPropertiesBuilder(
               "id", "secret").build());
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AzureBlobRestClientModule.class);
   }

}
