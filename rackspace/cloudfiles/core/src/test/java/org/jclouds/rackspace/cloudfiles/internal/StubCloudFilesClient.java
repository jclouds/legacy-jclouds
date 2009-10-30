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
package org.jclouds.rackspace.cloudfiles.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.blobstore.integration.internal.StubBlobStore.FutureBase;
import org.jclouds.blobstore.options.ListOptions;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ListContainerOptionsToListOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ResourceToObjectInfo;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ResourceToObjectList;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link CloudFilesClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubCloudFilesClient implements CloudFilesClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubBlobStore blobStore;
   private final LoggerFactory logFactory;
   private final CFObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ResourceToObjectInfo blob2ObjectInfo;
   private final ListContainerOptionsToListOptions container2ContainerListOptions;
   private final ResourceToObjectList resource2ObjectList;

   @Inject
   private StubCloudFilesClient(StubBlobStore blobStore, LoggerFactory logFactory,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            CFObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, ResourceToObjectInfo blob2ObjectInfo,
            ListContainerOptionsToListOptions container2ContainerListOptions,
            ResourceToObjectList resource2ContainerList) {
      this.blobStore = blobStore;
      this.logFactory = logFactory;
      this.objectProvider = objectProvider;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectInfo = checkNotNull(blob2ObjectInfo, "blob2ObjectInfo");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.resource2ObjectList = checkNotNull(resource2ContainerList, "resource2ContainerList");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   public boolean containerExists(String container) {
      return blobStore.getContainerToBlobs().containsKey(container);
   }

   public Future<Boolean> createContainer(String container) {
      return blobStore.createContainer(container);
   }

   public Future<Boolean> deleteContainerIfEmpty(String container) {
      return blobStore.deleteContainerImpl(container);
   }

   public boolean disableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public URI enableCDN(String container, Long ttl) {
      throw new UnsupportedOperationException();
   }

   public URI enableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public AccountMetadata getAccountStatistics() {
      throw new UnsupportedOperationException();
   }

   public ContainerCDNMetadata getCDNMetadata(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<CFObject> getObject(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return wrapFuture(blobStore.getBlob(container, key, getOptions), blob2Object);
   }

   public MutableObjectInfoWithMetadata getObjectInfo(String container, String key) {
      return blob2ObjectInfo.apply(blobStore.blobMetadata(container, key));
   }

   public SortedSet<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<? extends SortedSet<ContainerMetadata>> listContainers(
            ListContainerOptions... options) {
      return new FutureBase<SortedSet<ContainerMetadata>>() {

         public SortedSet<ContainerMetadata> get() throws InterruptedException, ExecutionException {
            return Sets.newTreeSet(Iterables.transform(blobStore.getContainerToBlobs().keySet(),
                     new Function<String, ContainerMetadata>() {
                        public ContainerMetadata apply(String name) {
                           return new ContainerMetadata(name, -1, -1);
                        }

                     }));
         }
      };
   }

   public Future<BoundedSortedSet<ObjectInfo>> listObjects(String container,
            ListContainerOptions... optionsList) {
      ListOptions options = container2ContainerListOptions.apply(optionsList);
      return wrapFuture(blobStore.list(container, options), resource2ObjectList);
   }

   public Future<String> putObject(String container, CFObject object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public Future<Void> removeObject(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

   public boolean setObjectInfo(String container, String key, Map<String, String> userMetadata) {
      throw new UnsupportedOperationException();
   }

   public URI updateCDN(String container, Long ttl) {
      throw new UnsupportedOperationException();
   }

   public CFObject newCFObject() {
      return objectProvider.create(null);
   }

}
