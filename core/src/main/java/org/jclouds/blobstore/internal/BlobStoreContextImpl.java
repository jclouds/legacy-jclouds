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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.internal.RestContextImpl;

/**
 * @author Adrian Cole
 */
public class BlobStoreContextImpl<A, S> extends RestContextImpl<A, S> implements
         BlobStoreContext<A, S> {
   private final BlobMap.Factory blobMapFactory;
   private final InputStreamMap.Factory inputStreamMapFactory;
   private final AsyncBlobStore ablobStore;
   private final BlobStore blobStore;
   private final ConsistencyModels consistencyModel;

   public BlobStoreContextImpl(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer, AsyncBlobStore ablobStore,
            BlobStore blobStore, A asyncApi, S syncApi, URI endPoint, String account) {
      super(closer, asyncApi, syncApi, endPoint, account);
      Class<?> type;
      if (asyncApi.getClass().isAnnotationPresent(ConsistencyModel.class)) {
         type = asyncApi.getClass();
      } else if (asyncApi.getClass().getInterfaces().length > 0
               && asyncApi.getClass().getInterfaces()[0]
                        .isAnnotationPresent(ConsistencyModel.class)) {
         type = asyncApi.getClass().getInterfaces()[0];
      } else {
         throw new IllegalStateException("@ConsistencyModel needed on " + asyncApi.getClass());
      }
      this.consistencyModel = type.getAnnotation(ConsistencyModel.class).value();
      this.blobMapFactory = checkNotNull(blobMapFactory, "blobMapFactory");
      this.inputStreamMapFactory = checkNotNull(inputStreamMapFactory, "inputStreamMapFactory");
      this.ablobStore = checkNotNull(ablobStore, "ablobStore");
      this.blobStore = checkNotNull(blobStore, "blobStore");
   }

   public BlobMap createBlobMap(String path) {
      checkNotNull(path, "path");
      String container = BlobStoreUtils.parseContainerFromPath(path);
      String prefix = BlobStoreUtils.parsePrefixFromPath(path);
      ListContainerOptions options = new ListContainerOptions();
      if (prefix != null)
         options.inDirectory(prefix);
      return blobMapFactory.create(container, options);
   }

   public InputStreamMap createInputStreamMap(String path) {
      checkNotNull(path, "path");
      String container = BlobStoreUtils.parseContainerFromPath(path);
      String prefix = BlobStoreUtils.parsePrefixFromPath(path);
      ListContainerOptions options = new ListContainerOptions();
      if (prefix != null)
         options.inDirectory(prefix);
      return inputStreamMapFactory.create(container, options);
   }

   public BlobStore getBlobStore() {
      return blobStore;
   }

   public AsyncBlobStore getAsyncBlobStore() {
      return ablobStore;
   }

   @Override
   public ConsistencyModels getConsistencyModel() {
      return consistencyModel;
   }
}
