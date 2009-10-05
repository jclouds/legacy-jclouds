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

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
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

import com.google.common.collect.Maps;
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
            Map<TypeLiteral, TypeLiteral> strategyImplMap) {
      this.blobMapFactoryType = blobMapFactoryType;
      this.blobMapImplType = blobMapImplType;
      this.inputStreamMapFactoryType = inputStreamMapFactoryType;
      this.inputStreamMapImplType = inputStreamMapImplType;
      this.strategyImplMap = strategyImplMap;
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
   protected final Map<TypeLiteral, TypeLiteral> strategyImplMap;

   public static class Builder<S, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> {
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
      @SuppressWarnings("unchecked")
      private TypeLiteral containsValueStrategyType;
      @SuppressWarnings("unchecked")
      private TypeLiteral containsValueStrategyImplType;
      @SuppressWarnings("unchecked")
      private TypeLiteral clearContainerStrategyType;
      @SuppressWarnings("unchecked")
      private TypeLiteral clearContainerStrategyImplType;
      @SuppressWarnings("unchecked")
      private TypeLiteral containerCountStrategyType;
      @SuppressWarnings("unchecked")
      private TypeLiteral containerCountStrategyImplType;

      private Builder(TypeLiteral<C> containerMetadataType, TypeLiteral<M> blobMetadataType,
               TypeLiteral<B> blobType) {
         this.containerMetadataType = containerMetadataType;
         this.blobMetadataType = blobMetadataType;
         this.blobType = blobType;
         blobMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(BlobMap.class,
                  BlobMap.Factory.class, blobMetadataType.getType(), blobType.getType()));
         blobMapImplType = TypeLiteral.get(Types.newParameterizedType(BlobMapImpl.class,
                  containerMetadataType.getType(), blobMetadataType.getType(), blobType.getType()));
         inputStreamMapFactoryType = TypeLiteral.get(Types.newParameterizedTypeWithOwner(
                  InputStreamMap.class, InputStreamMap.Factory.class, blobMetadataType.getType()));
         inputStreamMapImplType = TypeLiteral.get(Types.newParameterizedType(
                  InputStreamMapImpl.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         getAllBlobsStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  GetAllBlobsStrategy.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         setGetAllBlobsStrategyImpl(RetryOnNotFoundGetAllBlobsStrategy.class);

         getAllBlobMetadataStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  GetAllBlobMetadataStrategy.class, containerMetadataType.getType(),
                  blobMetadataType.getType(), blobType.getType()));
         setGetAllBlobMetadataStrategyImpl(ContainerListGetAllBlobMetadataStrategy.class);

         containsValueStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  ContainsValueStrategy.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         setContainsValueStrategyImpl(ContentMD5ContainsValueStrategy.class);

         clearContainerStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  ClearContainerStrategy.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         setClearContainerStrategyImpl(DeleteAllKeysClearContainerStrategy.class);
         containerCountStrategyType = TypeLiteral.get(Types.newParameterizedType(
                  ContainerCountStrategy.class, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
         setContainerCountStrategyImpl(KeyCountStrategy.class);
      }

      public Builder<S, C, M, B> withGetAllBlobsStrategy(Class<?> getAllBlobsStrategyImplClass) {
         setGetAllBlobsStrategyImpl(getAllBlobsStrategyImplClass);
         return this;
      }

      public Builder<S, C, M, B> withGetAllBlobMetadataStrategy(
               Class<?> getAllBlobMetadataStrategyImplClass) {
         setGetAllBlobMetadataStrategyImpl(getAllBlobMetadataStrategyImplClass);
         return this;
      }

      public Builder<S, C, M, B> withContainsValueStrategy(Class<?> containsValueStrategyImplClass) {
         setContainsValueStrategyImpl(containsValueStrategyImplClass);
         return this;
      }

      public Builder<S, C, M, B> withClearContainerStrategy(Class<?> clearContainerStrategyImplClass) {
         setClearContainerStrategyImpl(clearContainerStrategyImplClass);
         return this;
      }

      public Builder<S, C, M, B> withContainerCountStrategy(Class<?> containerCountStrategyImplClass) {
         setContainerCountStrategyImpl(containerCountStrategyImplClass);
         return this;
      }

      private void setGetAllBlobsStrategyImpl(Class<?> getAllBlobsStrategyImplClass) {
         getAllBlobsStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  getAllBlobsStrategyImplClass, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
      }

      private void setGetAllBlobMetadataStrategyImpl(Class<?> getAllBlobMetadataStrategyImplClass) {
         getAllBlobMetadataStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  getAllBlobMetadataStrategyImplClass, containerMetadataType.getType(),
                  blobMetadataType.getType(), blobType.getType()));
      }

      private void setContainsValueStrategyImpl(Class<?> containsValueStrategyClass) {
         containsValueStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  containsValueStrategyClass, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
      }

      private void setClearContainerStrategyImpl(Class<?> clearContainerStrategyClass) {
         clearContainerStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  clearContainerStrategyClass, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
      }

      private void setContainerCountStrategyImpl(Class<?> containerCountStrategyClass) {
         containerCountStrategyImplType = TypeLiteral.get(Types.newParameterizedType(
                  containerCountStrategyClass, containerMetadataType.getType(), blobMetadataType
                           .getType(), blobType.getType()));
      }

      public static <S, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> Builder<S, C, M, B> newBuilder(
               TypeLiteral<C> containerMetadataType, TypeLiteral<M> blobMetadataType,
               TypeLiteral<B> blobType) {
         return new Builder<S, C, M, B>(containerMetadataType, blobMetadataType, blobType);
      }

      @SuppressWarnings("unchecked")
      public BlobStoreMapsModule build() {
         Map<TypeLiteral, TypeLiteral> strategyImplMap = Maps.newHashMap();
         strategyImplMap.put(getAllBlobsStrategyType, getAllBlobsStrategyImplType);
         strategyImplMap.put(getAllBlobMetadataStrategyType, getAllBlobMetadataStrategyImplType);
         strategyImplMap.put(containsValueStrategyType, containsValueStrategyImplType);
         strategyImplMap.put(clearContainerStrategyType, clearContainerStrategyImplType);
         strategyImplMap.put(containerCountStrategyType, containerCountStrategyImplType);
         return new BlobStoreMapsModule(blobMapFactoryType, blobMapImplType,
                  inputStreamMapFactoryType, inputStreamMapImplType, strategyImplMap);
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
      for (Entry<TypeLiteral, TypeLiteral> entry : strategyImplMap.entrySet()) {
         bind(entry.getKey()).to(entry.getValue()).in(Scopes.SINGLETON);
      }
   }

}