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

import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.StubAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.config.AzureBlobContextModule.AzureBlobContextImpl;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobContextModuleTest")
public class AzureBlobContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new StubAzureBlobStoreModule(), BlobStoreMapsModule.Builder
               .newBuilder(new TypeLiteral<ContainerMetadata>() {
               }, new TypeLiteral<BlobMetadata>() {
               }, new TypeLiteral<Blob>() {
               }).build(), new AzureBlobContextModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(
                     Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to("user");
            bindConstant()
                     .annotatedWith(Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_KEY)).to(
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
      AzureBlobContext handler = createInjector().getInstance(AzureBlobContext.class);
      assertEquals(handler.getClass(), AzureBlobContextImpl.class);
   }

}