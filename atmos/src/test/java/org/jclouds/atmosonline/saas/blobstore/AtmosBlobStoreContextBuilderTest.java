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
package org.jclouds.atmosonline.saas.blobstore;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.AtmosStoragePropertiesBuilder;
import org.jclouds.atmosonline.saas.blobstore.config.AtmosBlobStoreContextModule;
import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.atmosonline.saas.config.AtmosStorageStubClientModule;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.internal.AtmosObjectImpl;
import org.jclouds.atmosonline.saas.internal.StubAtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
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
 * Tests behavior of modules configured in AtmosStorageContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.AtmosStorageContextBuilderTest")
public class AtmosBlobStoreContextBuilderTest {

   public void testNewBuilder() {
      AtmosBlobStoreContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX), null);
      assertEquals(builder.getProperties().getProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_UID),
               "id");
      assertEquals(builder.getProperties().getProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY),
               "secret");
   }

   private AtmosBlobStoreContextBuilder newBuilder() {
      return new AtmosBlobStoreContextBuilder(new AtmosStoragePropertiesBuilder("id", "secret")
               .build()).withModules(new AtmosStorageStubClientModule());
   }

   public void testBuildContext() {
      BlobStoreContext<AtmosStorageAsyncClient, AtmosStorageClient> context = newBuilder()
               .buildContext();
      assertEquals(context.getClass(), BlobStoreContextImpl.class);
      assertEquals(context.getAsyncApi().getClass(), StubAtmosStorageAsyncClient.class);
      assertEquals(context.getAsyncBlobStore().getClass(), AtmosAsyncBlobStore.class);
      assertEquals(context.getAsyncApi().newObject().getClass(), AtmosObjectImpl.class);
      assertEquals(context.getAsyncBlobStore().newBlob().getClass(), BlobImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/azurestub"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i
               .getInstance(Key
                        .get(new TypeLiteral<BlobStoreContext<AtmosStorageAsyncClient, AtmosStorageClient>>() {
                        })) != null;
      assert i.getInstance(AtmosObject.class) != null;
      assert i.getInstance(Blob.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      AtmosBlobStoreContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AtmosBlobStoreContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      AtmosBlobStoreContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), AtmosStorageRestClientModule.class);
   }

}
