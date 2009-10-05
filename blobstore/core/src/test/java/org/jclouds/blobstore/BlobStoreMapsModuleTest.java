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
package org.jclouds.blobstore;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.integration.config.StubBlobStoreConnectionModule;
import org.jclouds.blobstore.internal.BlobMapImpl;
import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ContainerCountStrategy;
import org.jclouds.blobstore.strategy.ContainsValueStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ContainerListGetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.internal.ContentMD5ContainsValueStrategy;
import org.jclouds.blobstore.strategy.internal.DeleteAllKeysClearContainerStrategy;
import org.jclouds.blobstore.strategy.internal.KeyCountStrategy;
import org.jclouds.blobstore.strategy.internal.RetryOnNotFoundGetAllBlobsStrategy;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", testName = "blobstore.BlobStoreMapsModuleTest")
public class BlobStoreMapsModuleTest {

   public void testBuilderBuild() {
      BlobStoreMapsModule module = BlobStoreMapsModule.Builder.newBuilder(
               new TypeLiteral<ContainerMetadata>() {
               }, new TypeLiteral<BlobMetadata>() {
               }, new TypeLiteral<Blob<BlobMetadata>>() {
               }).build();
      assertEquals(module.blobMapFactoryType,
               new TypeLiteral<BlobMap.Factory<BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(module.blobMapImplType,
               new TypeLiteral<BlobMapImpl<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(module.inputStreamMapFactoryType,
               new TypeLiteral<InputStreamMap.Factory<BlobMetadata>>() {
               });
      assertEquals(
               module.inputStreamMapImplType,
               new TypeLiteral<InputStreamMapImpl<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(
               module.strategyImplMap
                        .get(new TypeLiteral<GetAllBlobsStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }),
               new TypeLiteral<RetryOnNotFoundGetAllBlobsStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(
               module.strategyImplMap
                        .get(new TypeLiteral<GetAllBlobMetadataStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }),
               new TypeLiteral<ContainerListGetAllBlobMetadataStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(
               module.strategyImplMap
                        .get(new TypeLiteral<ContainsValueStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }),
               new TypeLiteral<ContentMD5ContainsValueStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(
               module.strategyImplMap
                        .get(new TypeLiteral<ClearContainerStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }),
               new TypeLiteral<DeleteAllKeysClearContainerStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
      assertEquals(
               module.strategyImplMap
                        .get(new TypeLiteral<ContainerCountStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }),
               new TypeLiteral<KeyCountStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
               });
   }

   public void testInject() {
      Injector i = Guice.createInjector(new StubBlobStoreConnectionModule(),
               BlobStoreMapsModule.Builder.newBuilder(new TypeLiteral<ContainerMetadata>() {
               }, new TypeLiteral<BlobMetadata>() {
               }, new TypeLiteral<Blob<BlobMetadata>>() {
               }).build());
      assertNotNull(i.getInstance(Key
               .get(new TypeLiteral<BlobMap.Factory<BlobMetadata, Blob<BlobMetadata>>>() {
               })));
      assertNotNull(i.getInstance(Key.get(new TypeLiteral<InputStreamMap.Factory<BlobMetadata>>() {
      })));
      assertEquals(
               i
                        .getInstance(
                                 Key
                                          .get(new TypeLiteral<GetAllBlobsStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                                          })).getClass(), RetryOnNotFoundGetAllBlobsStrategy.class);
      assertEquals(
               i
                        .getInstance(
                                 Key
                                          .get(new TypeLiteral<GetAllBlobMetadataStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                                          })).getClass(),
               ContainerListGetAllBlobMetadataStrategy.class);
   }
}
