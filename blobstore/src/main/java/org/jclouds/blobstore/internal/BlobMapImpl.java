/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Map representation of a live connection to a Blob Service.
 * 
 * @see AsyncBlobStore
 * @see BaseBlobMap
 * 
 * @author Adrian Cole
 */
public class BlobMapImpl extends BaseBlobMap<Blob> implements BlobMap {

   @Inject
   public BlobMapImpl(AsyncBlobStore connection, GetBlobsInListStrategy getAllBlobs,
            ListBlobMetadataStrategy getAllBlobMetadata,
            ContainsValueInListStrategy containsValueStrategy,
            ClearListStrategy clearContainerStrategy, CountListStrategy containerCountStrategy,
            String containerName, ListContainerOptions listOptions) {
      super(connection, getAllBlobs, getAllBlobMetadata, containsValueStrategy,
               clearContainerStrategy, containerCountStrategy, containerName, listOptions);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #values()
    */
   public Set<java.util.Map.Entry<String, Blob>> entrySet() {
      Set<Map.Entry<String, Blob>> entrySet = new HashSet<Map.Entry<String, Blob>>();
      for (Blob value : values()) {
         Map.Entry<String, Blob> entry = new Entry(pathStripper
                  .apply(value.getMetadata().getName()), value);
         entrySet.add(entry);
      }
      return entrySet;
   }

   public class Entry implements java.util.Map.Entry<String, Blob> {

      private Blob value;
      private final String key;

      Entry(String key, Blob value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return key;
      }

      public Blob getValue() {
         return value;
      }

      /**
       * {@inheritDoc}
       * 
       * @see LiveBMap#put(String, Blob)
       */
      public Blob setValue(Blob value) {
         return put(key, value);
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#getBlob(String, String)
    */
   public Blob get(Object key) {
      String realKey = prefixer.apply(key.toString());
      try {
         return stripPrefix(connection.getBlob(containerName, realKey).get(
                  requestTimeoutMilliseconds, TimeUnit.MILLISECONDS));
      } catch (Exception e) {
         if (Iterables.size(Iterables.filter(Throwables.getCausalChain(e),
                  KeyNotFoundException.class)) >= 1)
            return null;
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format("Error geting blob %s:%s", containerName,
                  realKey), e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#put(String, Blob)
    */
   public Blob put(String key, Blob value) {
      Blob returnVal = getLastValue(key);
      try {
         connection.putBlob(containerName, value).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format("Error putting blob %s:%s%n%3$s",
                  containerName, key, value), e);
      }
      return returnVal;
   }

   /**
    * {@inheritDoc} attempts to put all objects asynchronously.
    * 
    * @see S3Client#put(String, Blob)
    */
   public void putAll(Map<? extends String, ? extends Blob> map) {
      try {
         Set<Future<String>> puts = Sets.newHashSet();
         for (Blob object : map.values()) {
            // TODO: basename then add prefix
            puts.add(connection.putBlob(containerName, object));
         }
         for (Future<String> put : puts)
            // this will throw an exception if there was a problem
            put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException("Error putting into containerName" + containerName, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#removeBlob(String, String)
    */
   public Blob remove(Object key) {
      Blob old = getLastValue(key);
      String realKey = prefixer.apply(key.toString());
      try {
         connection.removeBlob(containerName, realKey).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format("Error removing blob %s:%s",
                  containerName, realKey), e);
      }
      return old;
   }

   private Blob getLastValue(Object key) {
      Blob old;
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
    * @see #getAllBlobs()
    */
   public Collection<Blob> values() {
      // convert ? extends Blob to Blob
      return Collections2.transform(getAllBlobs.execute(containerName, options),
               new Function<Blob, Blob>() {
                  public Blob apply(Blob from) {
                     return from;
                  }
               });
   }

   public Blob newBlob(String name) {
      return connection.newBlob(name);
   }

}
