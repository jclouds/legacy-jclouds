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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ContainerCountStrategy;
import org.jclouds.blobstore.strategy.ContainsValueStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.rest.internal.BoundedSortedSet;
import org.jclouds.util.Utils;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;

/**
 * Implements core Map functionality with an {@link BlobStore}
 * <p/>
 * All commands will wait a maximum of ${jclouds.blobstore.timeout} milliseconds to complete before
 * throwing an exception.
 * 
 * @author Adrian Cole
 * @param <V>
 *           value of the map
 */
public abstract class BaseBlobMap<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>, V> {

   protected final BlobStore<C, M, B> connection;
   protected final String containerName;
   protected final Provider<B> blobFactory;
   protected final GetAllBlobsStrategy<C, M, B> getAllBlobs;
   protected final GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata;
   protected final ContainsValueStrategy<C, M, B> containsValueStrategy;
   protected final ClearContainerStrategy<C, M, B> clearContainerStrategy;
   protected final ContainerCountStrategy<C, M, B> containerCountStrategy;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   /**
    * time to pause before retrying a transient failure
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY)
   protected long requestRetryMilliseconds = 10;

   @Inject
   public BaseBlobMap(BlobStore<C, M, B> connection, Provider<B> blobFactory,
            GetAllBlobsStrategy<C, M, B> getAllBlobs,
            GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata,
            ContainsValueStrategy<C, M, B> containsValueStrategy,
            ClearContainerStrategy<C, M, B> clearContainerStrategy,
            ContainerCountStrategy<C, M, B> containerCountStrategy, @Assisted String containerName) {
      this.connection = checkNotNull(connection, "connection");
      this.containerName = checkNotNull(containerName, "container");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.getAllBlobs = checkNotNull(getAllBlobs, "getAllBlobs");
      this.getAllBlobMetadata = checkNotNull(getAllBlobMetadata, "getAllBlobMetadata");
      this.containsValueStrategy = checkNotNull(containsValueStrategy, "containsValueStrategy");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.containerCountStrategy = checkNotNull(containerCountStrategy, "containerCountStrategy");
      checkArgument(!containerName.equals(""), "container name must not be a blank string!");
   }

   /**
    * {@inheritDoc}
    * <p/>
    * This returns the number of keys in the {@link BoundedSortedSet}
    * 
    * @see BoundedSortedSet#getContents()
    */
   public int size() {
      return (int) containerCountStrategy.execute(connection, containerName);
   }

   /**
    * attempts asynchronous gets on all objects.
    * 
    * @see BlobStore#getBlob(String, String)
    */
   protected Set<B> getAllBlobs() {
      return getAllBlobs.execute(connection, containerName);
   }

   /**
    * {@inheritDoc}
    * <p/>
    * Note that if value is an instance of InputStream, it will be read and closed following this
    * method. To reuse data from InputStreams, pass {@link java.io.InputStream}s inside {@link Blob}s
    */
   public boolean containsValue(Object value) {
      return containsValueStrategy.execute(connection, containerName, value);
   }

   public void clear() {
      clearContainerStrategy.execute(connection, containerName);
   }

   public Set<String> keySet() {
      Set<String> keys = Sets.newHashSet();
      for (BlobMetadata object : getAllBlobMetadata.execute(connection, containerName))
         keys.add(object.getName());
      return keys;
   }

   public boolean containsKey(Object key) {
      try {
         return connection.blobMetadata(containerName, key.toString()) != null;
      } catch (KeyNotFoundException e) {
         return false;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error searching for %1$s:%2$s",
                  containerName, key), e);
      }
   }

   public boolean isEmpty() {
      return size() == 0;
   }

   public SortedSet<M> listContainer() {
      return getAllBlobMetadata.execute(connection, containerName);
   }

}