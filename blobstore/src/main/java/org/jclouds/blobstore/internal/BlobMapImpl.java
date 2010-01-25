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

import javax.inject.Inject;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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
   public BlobMapImpl(BlobStore blobstore, GetBlobsInListStrategy getAllBlobs,
            ListBlobMetadataStrategy getAllBlobMetadata,
            ContainsValueInListStrategy containsValueStrategy,
            ClearListStrategy clearContainerStrategy, CountListStrategy containerCountStrategy,
            PutBlobsStrategy putBlobsStrategy, String containerName,
            ListContainerOptions listOptions) {
      super(blobstore, getAllBlobs, getAllBlobMetadata, containsValueStrategy,
               clearContainerStrategy, containerCountStrategy, putBlobsStrategy, containerName,
               listOptions);
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
      Blob blob = blobstore.getBlob(containerName, realKey);
      return blob != null ? stripPrefix(blob) : null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#put(String, Blob)
    */
   public Blob put(String key, Blob value) {
      Blob returnVal = getLastValue(key);
      blobstore.putBlob(containerName, value);
      return returnVal;
   }

   /**
    * {@inheritDoc} attempts to put all objects asynchronously.
    * 
    * @see S3Client#put(String, Blob)
    */
   public void putAll(Map<? extends String, ? extends Blob> map) {
      putBlobsStrategy.execute(containerName, map.values());
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#removeBlob(String, String)
    */
   public Blob remove(Object key) {
      Blob old = getLastValue(key);
      String realKey = prefixer.apply(key.toString());
      blobstore.removeBlob(containerName, realKey);
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
      return blobstore.newBlob(name);
   }

}
