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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.rest.BoundedList;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
   protected final String container;
   protected final Provider<B> blobFactory;

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
            @Assisted String containerName) {
      this.connection = checkNotNull(connection, "connection");
      this.container = checkNotNull(containerName, "container");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      checkArgument(!container.equals(""), "container name must not be a blank string!");
   }

   /**
    * {@inheritDoc}
    * <p/>
    * This returns the number of keys in the {@link BoundedList}
    * 
    * @see BoundedList#getContents()
    */
   public int size() {
      try {
         return refreshContainer().size();
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error getting size of container" + container, e);
      }
   }

   protected boolean containsETag(byte[] eTag) throws InterruptedException, ExecutionException,
            TimeoutException {
      for (BlobMetadata metadata : refreshContainer()) {
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
   protected Set<B> getAllObjects() {
      Set<B> objects = Sets.newHashSet();
      Map<String, Future<B>> futureObjects = Maps.newHashMap();
      for (String key : keySet()) {
         futureObjects.put(key, connection.getBlob(container, key));
      }
      for (Entry<String, Future<B>> futureObjectEntry : futureObjects.entrySet()) {
         try {
            ifNotFoundRetryOtherwiseAddToSet(futureObjectEntry.getKey(), futureObjectEntry
                     .getValue(), objects);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException(String.format("Error getting value from blob %1$s",
                     container), e);
         }

      }
      return objects;
   }

   @VisibleForTesting
   public void ifNotFoundRetryOtherwiseAddToSet(String key, Future<B> value, Set<B> objects)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 3; i++) {
         try {
            B object = value.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
            object.getMetadata().setKey(key);
            objects.add(object);
            return;
         } catch (KeyNotFoundException e) {
            Thread.sleep(requestRetryMilliseconds);
         }
      }
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
                  "Error searching for ETAG of value: [%2$s] in container:%1$s", container, value),
                  e);
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
      try {
         List<Future<Boolean>> deletes = Lists.newArrayList();
         for (String key : keySet()) {
            deletes.add(connection.removeBlob(container, key));
         }
         for (Future<Boolean> isdeleted : deletes)
            if (!isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)) {
               throw new BlobRuntimeException("failed to delete entry");
            }
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error clearing container" + container, e);
      }
   }

   /**
    * 
    * @throws ContainerNotFoundException
    *            when the container doesn't exist
    */
   protected List<M> refreshContainer() throws InterruptedException, ExecutionException,
            TimeoutException {
      return connection.listBlobs(container).get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
   }

   public Set<String> keySet() {
      try {
         Set<String> keys = Sets.newHashSet();
         for (BlobMetadata object : refreshContainer())
            keys.add(object.getKey());
         return keys;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error getting keys in container: " + container, e);
      }
   }

   public boolean containsKey(Object key) {
      try {
         return connection.blobMetadata(container, key.toString()) != null;
      } catch (KeyNotFoundException e) {
         return false;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error searching for %1$s:%2$s", container,
                  key), e);
      }
   }

   public boolean isEmpty() {
      return keySet().size() == 0;
   }

   public List<M> listContainer() {
      try {
         return refreshContainer();
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error getting container" + container, e);
      }
   }
}