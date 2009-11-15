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
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.aws.s3.blobstore.config.S3BlobStoreContextModule;
import org.jclouds.aws.s3.config.S3RestClientModule;
import org.jclouds.aws.s3.config.S3StubClientModule;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.internal.S3ObjectImpl;
import org.jclouds.aws.s3.internal.StubS3AsyncClient;
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
 * Tests behavior of modules configured in S3ContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextBuilderTest")
public class S3BlobStoreContextBuilderTest {

   public void testNewBuilder() {
      S3BlobStoreContextBuilder builder = new S3BlobStoreContextBuilder(new S3PropertiesBuilder(
               "id", "secret").build());
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "x-amz-meta-");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_ACCESSKEYID), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_SECRETACCESSKEY), "secret");
   }

   public void testBuildContext() {
      BlobStoreContext<S3AsyncClient, S3Client> context = new S3BlobStoreContextBuilder(
               new S3PropertiesBuilder("id", "secret").build()).withModules(
               new S3StubClientModule()).buildContext();
      assertEquals(context.getClass(), BlobStoreContextImpl.class);
      assertEquals(context.getAsyncApi().getClass(), StubS3AsyncClient.class);
      assertEquals(context.getAsyncBlobStore().getClass(), S3AsyncBlobStore.class);
      assertEquals(context.getAsyncApi().newS3Object().getClass(), S3ObjectImpl.class);
      assertEquals(context.getAsyncBlobStore().newBlob().getClass(), BlobImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/s3stub"));
   }

   public void testBuildInjector() {
      Injector i = new S3BlobStoreContextBuilder(new S3PropertiesBuilder("id", "secret").build())
               .withModules(new S3StubClientModule()).buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<BlobStoreContext<S3AsyncClient, S3Client>>() {
      })) != null;
      assert i.getInstance(S3Object.class) != null;
      assert i.getInstance(Blob.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      S3BlobStoreContextBuilder builder = new S3BlobStoreContextBuilder(new S3PropertiesBuilder(
               "id", "secret").build());
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), S3BlobStoreContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      S3BlobStoreContextBuilder builder = new S3BlobStoreContextBuilder(new S3PropertiesBuilder(
               "id", "secret").build());
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), S3RestClientModule.class);
   }

}
