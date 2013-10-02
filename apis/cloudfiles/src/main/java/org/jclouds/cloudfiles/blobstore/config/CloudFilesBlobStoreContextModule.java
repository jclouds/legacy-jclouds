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
package org.jclouds.cloudfiles.blobstore.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.cloudfiles.blobstore.CloudFilesAsyncBlobStore;
import org.jclouds.cloudfiles.blobstore.CloudFilesBlobStore;
import org.jclouds.cloudfiles.blobstore.functions.CloudFilesObjectToBlobMetadata;
import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class CloudFilesBlobStoreContextModule extends SwiftBlobStoreContextModule {

   @Provides
   @Singleton
   protected LoadingCache<String, URI> cdnContainer(final CloudFilesClient client) {
      return CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<String, URI>() {
         @Override
         public URI load(String container) {
            ContainerCDNMetadata md = client.getCDNMetadata(container);
            return md != null ? md.getCDNUri() : null;
         }

         @Override
         public String toString() {
            return "getCDNMetadata()";
         }
      });
   }

   @Override
   protected void configure() {
      super.configure();
      bind(SwiftBlobStore.class).to(CloudFilesBlobStore.class);
      bind(SwiftAsyncBlobStore.class).to(CloudFilesAsyncBlobStore.class);
      bind(ObjectToBlobMetadata.class).to(CloudFilesObjectToBlobMetadata.class);
   }
}
