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

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.internal.BlobMapImpl;
import org.jclouds.blobstore.internal.InputStreamMapImpl;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public class BlobStoreMapsModule<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends AbstractModule {

   // code is unchecked here as we are getting types at runtime. Due to type erasure, we cannot pass
   // generic types into provider methods. This is why we are sending in TypeLiterals.
   @SuppressWarnings("unchecked")
   private final TypeLiteral blobMapFactoryType;
   @SuppressWarnings("unchecked")
   private final TypeLiteral blobMapImplType;
   @SuppressWarnings("unchecked")
   private final TypeLiteral inputStreamMapFactoryType;
   @SuppressWarnings("unchecked")
   private final TypeLiteral inputStreamMapImplType;

   @Inject
   public BlobStoreMapsModule(TypeLiteral<S> connectionType, TypeLiteral<C> containerMetadataType,
            TypeLiteral<M> blobMetadataType, TypeLiteral<B> blobType) {
      blobMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(BlobMap.class,
               BlobMap.Factory.class, blobMetadataType.getType(), blobType.getType()));
      blobMapImplType = TypeLiteral.get(Types.newParameterizedType(BlobMapImpl.class,
               connectionType.getType(), containerMetadataType.getType(), blobMetadataType
                        .getType(), blobType.getType()));
      inputStreamMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(
               InputStreamMap.class, InputStreamMap.Factory.class, blobMetadataType.getType()));
      inputStreamMapImplType = TypeLiteral.get(Types.newParameterizedType(InputStreamMapImpl.class,
               connectionType.getType(), containerMetadataType.getType(), blobMetadataType
                        .getType(), blobType.getType()));
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      bind(blobMapFactoryType).toProvider(
               FactoryProvider.newFactory(blobMapFactoryType, blobMapImplType));
      bind(inputStreamMapFactoryType).toProvider(
               FactoryProvider.newFactory(inputStreamMapFactoryType, inputStreamMapImplType));
   }

}