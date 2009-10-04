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

import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.cloud.CloudContextBuilder;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public abstract class BlobStoreContextBuilder<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends CloudContextBuilder<S> {
   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withExecutorService(ExecutorService service) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withExecutorService(service);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withHttpMaxRedirects(int httpMaxRedirects) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withHttpMaxRetries(int httpMaxRetries) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withHttpMaxRetries(httpMaxRetries);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withJsonDebug() {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withJsonDebug();
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withModule(Module module) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withModule(module);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withModules(Module... modules) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withModules(modules);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (BlobStoreContextBuilder<S, C, M, B>) super
               .withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (BlobStoreContextBuilder<S, C, M, B>) super
               .withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withPoolMaxConnections(int poolMaxConnections) {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withPoolMaxConnections(poolMaxConnections);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (BlobStoreContextBuilder<S, C, M, B>) super
               .withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withPoolRequestInvokerThreads(
            int poolRequestInvokerThreads) {
      return (BlobStoreContextBuilder<S, C, M, B>) super
               .withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> withSaxDebug() {
      return (BlobStoreContextBuilder<S, C, M, B>) super.withSaxDebug();
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContextBuilder<S, C, M, B> relaxSSLHostname() {
      return (BlobStoreContextBuilder<S, C, M, B>) super.relaxSSLHostname();
   }

   protected final TypeLiteral<C> containerMetadataType;
   protected final TypeLiteral<M> blobMetadataType;
   protected final TypeLiteral<B> blobType;

   public BlobStoreContextBuilder(TypeLiteral<S> connectionType,
            TypeLiteral<C> containerMetadataType, TypeLiteral<M> blobMetadataType,
            TypeLiteral<B> blobType) {
      this(connectionType, containerMetadataType, blobMetadataType, blobType, new Properties());
   }

   public BlobStoreContextBuilder(TypeLiteral<S> connectionType,
            TypeLiteral<C> containerMetadataType, TypeLiteral<M> blobMetadataType,
            TypeLiteral<B> blobType, Properties properties) {
      super(connectionType, properties);
      this.containerMetadataType = containerMetadataType;
      this.blobMetadataType = blobMetadataType;
      this.blobType = blobType;
      modules.add(BlobStoreMapsModule.Builder.newBuilder(connectionType,
               containerMetadataType, blobMetadataType, blobType).build());
   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContext<S, C, M, B> buildContext() {
      return (BlobStoreContext<S, C, M, B>) this.buildInjector().getInstance(
               Key.get(Types.newParameterizedType(BlobStoreContext.class, connectionType.getType(),
                        containerMetadataType.getType(), blobMetadataType.getType(), blobType
                                 .getType())));
   }
}