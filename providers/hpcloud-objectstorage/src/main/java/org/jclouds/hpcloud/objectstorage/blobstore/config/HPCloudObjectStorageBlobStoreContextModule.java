/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.hpcloud.objectstorage.blobstore.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApi;
import org.jclouds.hpcloud.objectstorage.blobstore.HPCloudObjectStorageAsyncBlobStore;
import org.jclouds.hpcloud.objectstorage.blobstore.HPCloudObjectStorageBlobStore;
import org.jclouds.hpcloud.objectstorage.blobstore.functions.HPCloudObjectStorageObjectToBlobMetadata;
import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.extensions.CDNContainerApi;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;

/**
 *
 * @author Adrian Cole
 */
public class HPCloudObjectStorageBlobStoreContextModule extends SwiftBlobStoreContextModule {

   @Beta
   @Singleton
   public static final class GetCDNMetadata extends CacheLoader<String, URI> {
      @Resource
      protected Logger logger = Logger.NULL;

      private final HPCloudObjectStorageApi client;

      @Inject
      public GetCDNMetadata(HPCloudObjectStorageApi client) {
         this.client = client;
      }

      @Override
      public URI load(String container) {
         Optional<CDNContainerApi> cdnExtension = client.getCDNExtension();
         checkArgument(cdnExtension.isPresent(), "CDN is required, but the extension is not available!");
         try {
            CDNContainer md = cdnExtension.get().get(container);
            return md != null ? md.getCDNUri() : null;
         } catch (HttpResponseException e) {
            // TODO: this is due to beta status
            logger.trace("couldn't get cdn metadata for %s: %s", container, e.getMessage());
            return null;
         } catch (NoSuchElementException e) {
             logger.trace("CDN may not be enabled. Couldn't get cdn metadata for %s: %s", container, e.getMessage());
             return null;
         }
      }

      @Override
      public String toString() {
         return "get()";
      }
   }

   @Provides
   @Singleton
   protected LoadingCache<String, URI> cdnContainer(GetCDNMetadata loader) {
      return CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build(loader);
   }

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(HPCloudObjectStorageAsyncBlobStore.class);
      bind(BlobStore.class).to(HPCloudObjectStorageBlobStore.class);
      bind(ObjectToBlobMetadata.class).to(HPCloudObjectStorageObjectToBlobMetadata.class);
   }
}
