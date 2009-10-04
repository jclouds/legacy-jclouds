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

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.internal.BlobMapImpl;
import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ContainerListGetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.internal.RetryOnNotFoundGetAllBlobsStrategy;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public class BlobStoreMapsModule extends AbstractModule {

   @SuppressWarnings("unchecked")
   private BlobStoreMapsModule(TypeLiteral blobMapFactoryType, TypeLiteral blobMapImplType,
            TypeLiteral inputStreamMapFactoryType, TypeLiteral inputStreamMapImplType,
            TypeLiteral getAllBlobsStrategyType, TypeLiteral getAllBlobsStrategyImplType,
            TypeLiteral getAllBlobMetadataStrategyType,
            TypeLiteral getAllBlobMetadataStrategyImplType) {
      this.blobMapFactoryType = blobMapFactoryType;
      this.blobMapImplType = blobMapImplType;
      this.inputStreamMapFactoryType = inputStreamMapFactoryType;
      this.inputStreamMapImplType = inputStreamMapImplType;
      this.getAllBlobsStrategyType = getAllBlobsStrategyType;
      this.getAllBlobsStrategyImplType = getAllBlobsStrategyImplType;
      this.getAllBlobMetadataStrategyType = getAllBlobMetadataStrategyType;
      this.getAllBlobMetadataStrategyImplType = getAllBlobMetadataStrategyImplType;
   }

   // code is unchecked here as we are getting types at runtime. Due to type erasure, we cannot pass
   // generic types into provider methods. This is why we are sending in TypeLiterals.
   @SuppressWarnings("unchecked")
   protected final TypeLiteral blobMapFactoryType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral blobMapImplType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral inputStreamMapFactoryType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral inputStreamMapImplType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral getAllBlobsStrategyType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral getAllBlobsStrategyImplType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral getAllBlobMetadataStrategyType;
   @SuppressWarnings("unchecked")
   protected final TypeLiteral getAllBlobMetadataStrategyImplType;

   public static class Builder<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> {
      @SuppressWarnings("unused")
      private final TypeLiteral<S> connectionType;
      private final TypeLiteral<C> containerMetadataType;
      private final TypeLiteral<M> blobMetadataType;
      private final TypeLiteral<B> blobType;
      @SuppressWarnings("unchecked")
      private TypeLiteral blobMapFactoryType;
      @SuppressWarnings("unchecked")
      private TypeLiteral blobMapImplType;
      @SuppressWarnings("unchecked")
      private TypeLiteral inputStreamMapFactoryType;
      @SuppressWarnings("unchecked")
      private TypeLiteral inputStreamMapImplType;
      @SuppressWarnings("unchecked")
      private TypeLiteral getAllBlobsStrategyType;
      @SuppressWarnings("unchecked")
      private TypeLiteral getAllBlobsStrategyImplType;
      @SuppressWarnings("unchecked")
      private TypeLiteral getAllBlobMetadataStrategyType;
      @SuppressWarnings("unchecked")
      private TypeLiteral getAllBlobMetadataStrategyImplType;

      private Builder(TypeLiteral<S> connectionType, TypeLiteral<C> containerMetadataType,
               TypeLiteral<M> blobMetadataType, TypeLiteral<B> blobType) {
         this.connectionType = connectionType;
         this.containerMetadataType = containerMetadataType;
         this.blobMetadataType = blobMetadataType;
         this.blobType = blobType;
         blobMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(BlobMap.class,
                  BlobMap.Factory.class, blobMetadataType.getType(), blobType.getType()));
         blobMapImplType = TypeLiteral.get(Types.newParameterizedType(BlobMapImpl.class,
                  connectionType.getType(), containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         inputStreamMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(
                  InputStreamMap.class, InputStreamMap.Factory.class, blobMetadataType.getType()));
         inputStreamMapImplType = TypeLiteral.get(Types.newParameterizedType(
                  InputStreamMapImpl.class, connectionType.getType(), containerMetadataType
                           .getType(), blobMetadataType.getType(), blobType.getType()));
         getAllBlobsStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  GetAllBlobsStrategy.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         setGetAllBlobsStrategyImpl(RetryOnNotFoundGetAllBlobsStrategy.class);

         getAllBlobMetadataStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  GetAllBlobMetadataStrategy.class, containerMetadataType.getType(),
                  blobMetadataType.getType(), blobType.getType()));
         getAllBlobMetadataStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  ContainerListGetAllBlobMetadataStrategy.class, containerMetadataType.getType(),
                  blobMetadataType.getType(), blobType.getType()));
      }

      Builder<S, C, M, B> withGetAllBlobsStrategy(Class<?> getAllBlobsStrategyImplClass) {
         setGetAllBlobsStrategyImpl(getAllBlobsStrategyImplClass);
         return this;
      }

      private void setGetAllBlobsStrategyImpl(Class<?> getAllBlobsStrategyImplClass) {
         getAllBlobsStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  getAllBlobsStrategyImplClass, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
      }

      public static <S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> Builder<S, C, M, B> newBuilder(
               TypeLiteral<S> connectionType, TypeLiteral<C> containerMetadataType,
               TypeLiteral<M> blobMetadataType, TypeLiteral<B> blobType) {
         return new Builder<S, C, M, B>(connectionType, containerMetadataType, blobMetadataType,
                  blobType);
      }

      public BlobStoreMapsModule build() {

         return new BlobStoreMapsModule(blobMapFactoryType, blobMapImplType,
                  inputStreamMapFactoryType, inputStreamMapImplType, getAllBlobsStrategyType,
                  getAllBlobsStrategyImplType, getAllBlobMetadataStrategyType,
                  getAllBlobMetadataStrategyImplType);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      bind(blobMapFactoryType).toProvider(
               FactoryProvider.newFactory(blobMapFactoryType, blobMapImplType))
               .in(Scopes.SINGLETON);
      bind(inputStreamMapFactoryType).toProvider(
               FactoryProvider.newFactory(inputStreamMapFactoryType, inputStreamMapImplType)).in(
               Scopes.SINGLETON);
      bind(getAllBlobsStrategyType).to(getAllBlobsStrategyImplType).in(Scopes.SINGLETON);
      bind(getAllBlobMetadataStrategyType).to(getAllBlobMetadataStrategyImplType).in(
               Scopes.SINGLETON);
   }

}