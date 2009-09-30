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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContextImpl;
import org.jclouds.blobstore.BlobMap.Factory;
import org.jclouds.lifecycle.Closer;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.PCSContext;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.functions.FindIdInContainerList;
import org.jclouds.mezeo.pcs2.functions.FindIdInFileList;
import org.jclouds.mezeo.pcs2.functions.Key;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the PCS connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class PCSContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(PCSContext.class).to(PCSContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class PCSContextImpl extends
            BlobStoreContextImpl<PCSBlobStore, ContainerMetadata, FileMetadata, PCSFile> implements
            PCSContext {
      @Inject
      PCSContextImpl(Factory<FileMetadata, PCSFile> blobMapFactory,
               org.jclouds.blobstore.InputStreamMap.Factory<FileMetadata> inputStreamMapFactory,
               Closer closer, Provider<PCSFile> blobProvider, PCSBlobStore defaultApi,
               @PCS URI endPoint, @Named(PCSConstants.PROPERTY_PCS2_USER) String account) {
         super(blobMapFactory, inputStreamMapFactory, closer, blobProvider, defaultApi, endPoint,
                  account);
      }
   }

   @Provides
   @Singleton
   public ConcurrentMap<Key, String> provideConcurrentMap(FindIdInFileList finder) {
      return new MapMaker().expiration(30, TimeUnit.SECONDS).makeComputingMap(finder);
   }

   @Provides
   @Singleton
   public ConcurrentMap<String, String> provideConcurrentMap(FindIdInContainerList finder) {
      return new MapMaker().concurrencyLevel(32).expiration(30, TimeUnit.SECONDS).makeComputingMap(
               finder);
   }

   @Provides
   @Singleton
   public ConcurrentMap<Key, FileMetadata> provideConcurrentMap(final PCSBlobStore connection) {
      return new MapMaker().expiration(30, TimeUnit.SECONDS).makeComputingMap(
               new Function<Key, FileMetadata>() {

                  public FileMetadata apply(Key from) {
                     return connection.blobMetadata(from.getContainer(), from.getKey());
                  }

               });
   }
}