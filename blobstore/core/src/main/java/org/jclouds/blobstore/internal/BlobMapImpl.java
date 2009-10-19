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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ContainerCountStrategy;
import org.jclouds.blobstore.strategy.ContainsValueStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.util.Utils;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to a Blob Service.
 * 
 * @see BlobStore
 * @see BaseBlobMap
 * 
 * @author Adrian Cole
 */
public class BlobMapImpl<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseBlobMap<C, M, B, B> implements BlobMap<M, B> {

   @Inject
   public BlobMapImpl(BlobStore<C, M, B> connection, Provider<B> blobFactory,
            GetAllBlobsStrategy<C, M, B> getAllBlobs,
            GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata,
            ContainsValueStrategy<C, M, B> containsValueStrategy,
            ClearContainerStrategy<C, M, B> clearContainerStrategy,
            ContainerCountStrategy<C, M, B> containerCountStrategy, @Assisted String containerName) {
      super(connection, blobFactory, getAllBlobs, getAllBlobMetadata, containsValueStrategy,
               clearContainerStrategy, containerCountStrategy, containerName);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #values()
    */
   public Set<java.util.Map.Entry<String, B>> entrySet() {
      Set<Map.Entry<String, B>> entrySet = new HashSet<Map.Entry<String, B>>();
      for (B value : values()) {
         Map.Entry<String, B> entry = new Entry(value.getName(), value);
         entrySet.add(entry);
      }
      return entrySet;
   }

   public class Entry implements java.util.Map.Entry<String, B> {

      private B value;
      private String key;

      Entry(String key, B value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return key;
      }

      public B getValue() {
         return value;
      }

      /**
       * {@inheritDoc}
       * 
       * @see LiveBMap#put(String, B)
       */
      public B setValue(B value) {
         return put(key, value);
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#getBlob(String, String)
    */
   public B get(Object key) {
      try {
         return connection.getBlob(containerName, key.toString()).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (KeyNotFoundException e) {
         return null;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error geting object %1$s:%2$s",
                  containerName, key), e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#put(String, B)
    */
   public B put(String key, B value) {
      B returnVal = getLastValue(key);
      try {
         connection.putBlob(containerName, value).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error putting object %1$s:%2$s%n%3$s",
                  containerName, key, value), e);
      }
      return returnVal;
   }

   /**
    * {@inheritDoc} attempts to put all objects asynchronously.
    * 
    * @see S3Connection#put(String, B)
    */
   public void putAll(Map<? extends String, ? extends B> map) {
      try {
         Set<Future<String>> puts = Sets.newHashSet();
         for (B object : map.values()) {
            puts.add(connection.putBlob(containerName, object));
         }
         for (Future<String> put : puts)
            // this will throw an exception if there was a problem
            put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error putting into containerName" + containerName, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#removeBlob(String, String)
    */
   public B remove(Object key) {
      B old = getLastValue(key);
      try {
         connection.removeBlob(containerName, key.toString()).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error removing object %1$s:%2$s",
                  containerName, key), e);
      }
      return old;
   }

   private B getLastValue(Object key) {
      B old;
      try {
         old = get(key);
      } catch (KeyNotFoundException e) {
         old = null;
      }
      return old;
   }

   /**
    * {@inheritDoc}
    * 
    * @see #getAllObjects()
    */
   public Collection<B> values() {
      return super.getAllBlobs();
   }
}
