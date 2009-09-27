/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.azure.storage.blob;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.RestAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.internal.GuiceAzureBlobContext;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.functions.ParseBlobFromHeadersAndHttpContent.BlobFactory;
import org.jclouds.blobstore.functions.ParseContentTypeFromHeaders.BlobMetadataFactory;
import org.jclouds.cloud.CloudContext;
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
      AzureBlobContextBuilder builder = AzureBlobContextBuilder.newBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX), "x-ms-meta-");
      assertEquals(builder.getProperties().getProperty(
               AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT), "id");
      assertEquals(builder.getProperties().getProperty(
               AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY), "secret");
   }

   public void testBuildContext() {
      CloudContext<AzureBlobStore> context = AzureBlobContextBuilder.newBuilder("id", "secret")
               .buildContext();
      assertEquals(context.getClass(), GuiceAzureBlobContext.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://id.blob.core.windows.net"));
   }

   public void testBuildInjector() {
      Injector i = AzureBlobContextBuilder.newBuilder("id", "secret").buildInjector();
      assert i.getInstance(AzureBlobContext.class) != null;
      assert i.getInstance(GuiceAzureBlobContext.AzureBlobObjectMapFactory.class) != null;
      assert i.getInstance(GuiceAzureBlobContext.AzureBlobInputStreamMapFactory.class) != null;
      assert i.getInstance(Key.get(new TypeLiteral<BlobMetadataFactory<BlobMetadata>>() {
      })) != null;
      assert i.getInstance(Key.get(new TypeLiteral<BlobFactory<BlobMetadata, Blob>>() {
      })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobContextBuilder builder = AzureBlobContextBuilder.newBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AzureBlobContextModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      AzureBlobContextBuilder builder = AzureBlobContextBuilder.newBuilder("id", "secret");
      builder.addApiModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestAzureBlobStoreModule.class);
   }

}
