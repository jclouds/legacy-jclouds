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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.BaseBlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.assistedinject.Assisted;

/**
 * Map representation of a live connection to S3. All put operations will result in ETag
 * calculation. If this is not desired, use {@link LiveBMap} instead.
 * 
 * @author Adrian Cole
 * @see BlobStore
 * @see InputStreamMap
 * @see BaseBlobMap
 */
public class InputStreamMapImpl<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseBlobMap<C, M, B, InputStream> implements InputStreamMap<M> {

   @Inject
   public InputStreamMapImpl(S connection, Provider<B> blobFactory,
            @Assisted String container) {
      super(connection, blobFactory, container);
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#getBlob(String, String)
    */
   public InputStream get(Object o) {
      try {
         return (InputStream) (connection.getBlob(container, o.toString()).get(
                  requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).getData();
      } catch (KeyNotFoundException e) {
         return null;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String
                  .format("Error geting object %1$s:%2$s", container, o), e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Connection#removeBlob(String, String)
    */
   public InputStream remove(Object o) {
      InputStream old = getLastValue(o);
      try {
         connection.removeBlob(container, o.toString()).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error removing object %1$s:%2$s", container,
                  o), e);
      }
      return old;
   }

   private InputStream getLastValue(Object o) {
      InputStream old;
      try {
         old = get(o);
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
   public Collection<InputStream> values() {
      Collection<InputStream> values = new LinkedList<InputStream>();
      Set<B> objects = getAllObjects();
      for (B object : objects) {
         values.add((InputStream) object.getData());
      }
      return values;
   }

   /**
    * {@inheritDoc}
    * 
    * @see #getAllObjects()
    */
   public Set<Map.Entry<String, InputStream>> entrySet() {
      Set<Map.Entry<String, InputStream>> entrySet = new HashSet<Map.Entry<String, InputStream>>();
      for (B object : getAllObjects()) {
         entrySet.add(new Entry(object.getKey(), (InputStream) object.getData()));
      }
      return entrySet;
   }

   public class Entry implements java.util.Map.Entry<String, InputStream> {

      private InputStream value;
      private String key;

      Entry(String key, InputStream value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return key;
      }

      public InputStream getValue() {
         return value;
      }

      /**
       * {@inheritDoc}
       * 
       * @see InputStreamMapImpl#put(String, InputStream)
       */
      public InputStream setValue(InputStream value) {
         return put(key, value);
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAll(Map<? extends String, ? extends InputStream> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllBytes(Map<? extends String, ? extends byte[]> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllFiles(Map<? extends String, ? extends File> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllStrings(Map<? extends String, ? extends String> map) {
      putAllInternal(map);
   }

   /**
    * submits requests to add all objects and collects the results later. All values will have eTag
    * calculated first. As a side-effect of this, the content will be copied into a byte [].
    * 
    * @see S3Connection#put(String, B)
    */
   @VisibleForTesting
   void putAllInternal(Map<? extends String, ? extends Object> map) {
      try {
         List<Future<byte[]>> puts = new ArrayList<Future<byte[]>>();
         for (Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
            B object = blobFactory.get();
            object.getMetadata().setKey(entry.getKey());
            object.setData(entry.getValue());
            object.generateMD5();
            puts.add(connection.putBlob(container, object));
            // / ParamExtractor Funcion<?,String>
            // / response transformer set key on the way out.
            // / ExceptionHandler convert 404 to NOT_FOUND
         }
         for (Future<byte[]> put : puts)
            // this will throw an exception if there was a problem
            put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error putting into containerName" + container, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putString(String key, String value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putFile(String key, File value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putBytes(String key, byte[] value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream put(String key, InputStream value) {
      return putInternal(key, value);
   }

   /**
    * calculates eTag before adding the object to s3. As a side-effect of this, the content will be
    * copied into a byte []. *
    * 
    * @see S3Connection#put(String, B)
    */
   @VisibleForTesting
   InputStream putInternal(String s, Object o) {
      B object = blobFactory.get();
      object.getMetadata().setKey(s);
      try {
         InputStream returnVal = containsKey(s) ? get(s) : null;
         object.setData(o);
         object.generateMD5();
         connection.putBlob(container, object).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
         return returnVal;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error adding object %1$s:%2$s", container,
                  object), e);
      }
   }

}
