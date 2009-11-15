/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.blobstore;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.azure.storage.blob.blobstore.config.AzureBlobStoreContextModule;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.azure.storage.blob.config.AzureBlobStubClientModule;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.internal.AzureBlobImpl;
import org.jclouds.azure.storage.blob.internal.StubAzureBlobAsyncClient;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
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
public class AzureBlobStoreContextBuilderTest {

   public void testNewBuilder() {
      AzureBlobStoreContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "x-ms-meta-");
      assertEquals(builder.getProperties().getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT),
               "id");
      assertEquals(builder.getProperties().getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY),
               "secret");
   }

   private AzureBlobStoreContextBuilder newBuilder() {
      return new AzureBlobStoreContextBuilder(new AzureBlobPropertiesBuilder("id",
               "secret").build()).withModules(new AzureBlobStubClientModule());
   }

   public void testBuildContext() {
      BlobStoreContext<AzureBlobAsyncClient, AzureBlobClient> context = newBuilder().buildContext();
      assertEquals(context.getClass(), BlobStoreContextImpl.class);
      assertEquals(context.getAsyncApi().getClass(), StubAzureBlobAsyncClient.class);
      assertEquals(context.getAsyncBlobStore().getClass(), AzureAsyncBlobStore.class);
      assertEquals(context.getAsyncApi().newBlob().getClass(), AzureBlobImpl.class);
      assertEquals(context.getAsyncBlobStore().newBlob().getClass(), BlobImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/azurestub"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<BlobStoreContext<AzureBlobAsyncClient, AzureBlobClient>>() {
      })) != null;
      assert i.getInstance(AzureBlob.class) != null;
      assert i.getInstance(Blob.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobStoreContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AzureBlobStoreContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobStoreContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AzureBlobRestClientModule.class);
   }

}
