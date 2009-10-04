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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.rest.BoundedSortedSet;
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
            GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata, @Assisted String containerName) {
      this.connection = checkNotNull(connection, "connection");
      this.containerName = checkNotNull(containerName, "container");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.getAllBlobs = checkNotNull(getAllBlobs, "getAllBlobs");
      this.getAllBlobMetadata = checkNotNull(getAllBlobMetadata, "getAllBlobMetadata");
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
      return getAllBlobMetadata.execute(connection, containerName).size();
   }

   protected boolean containsETag(byte[] eTag) throws InterruptedException, ExecutionException,
            TimeoutException {
      for (BlobMetadata metadata : getAllBlobMetadata.execute(connection, containerName)) {
         if (Arrays.equals(eTag, metadata.getETag()))
            return true;
      }
      return false;
   }

   protected byte[] getMD5(Object value) throws IOException, FileNotFoundException,
            InterruptedException, ExecutionException, TimeoutException {
      Blob<?> object;
      if (value instanceof Blob<?>) {
         object = (Blob<?>) value;
      } else {
         object = blobFactory.get();
         object.setData(value);
      }
      if (object.getMetadata().getContentMD5() == null)
         object.generateMD5();
      return object.getMetadata().getContentMD5();
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
      return eTagExistsMatchingMD5Of(value);
   }

   private boolean eTagExistsMatchingMD5Of(Object value) {
      try {
         byte[] eTag = getMD5(value);
         return containsETag(eTag);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format(
                  "Error searching for ETAG of value: [%2$s] in container:%1$s", containerName,
                  value), e);
      }
   }

   public static class BlobRuntimeException extends RuntimeException {
      private static final long serialVersionUID = 1L;

      BlobRuntimeException(String s) {
         super(s);
      }

      public BlobRuntimeException(String s, Throwable throwable) {
         super(s, throwable);
      }
   }

   public void clear() {
      Set<Future<Boolean>> deletes = Sets.newHashSet();
      for (M md : getAllBlobMetadata.execute(connection, containerName)) {
         deletes.add(connection.removeBlob(containerName, md.getKey()));
      }
      for (Future<Boolean> isdeleted : deletes) {
         try {
            if (!isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)) {
               throw new BlobRuntimeException("Failed to delete blob in container: "
                        + containerName);
            }
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException("Error deleting blob in container: " + containerName, e);
         }
      }
   }

   public Set<String> keySet() {
      Set<String> keys = Sets.newHashSet();
      for (BlobMetadata object : getAllBlobMetadata.execute(connection, containerName))
         keys.add(object.getKey());
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