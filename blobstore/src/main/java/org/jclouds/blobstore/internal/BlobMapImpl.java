/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListContainerAndRecurseThroughFolders;

import com.google.common.collect.Sets;

/**
 * Map representation of a live connection to a Blob Service.
 * 
 * @see BlobStore
 * @see BaseBlobMap
 * 
 * @author Adrian Cole
 */
public class BlobMapImpl extends BaseBlobMap<Blob> implements BlobMap {

   @Inject
   public BlobMapImpl(BlobStore blobstore, GetBlobsInListStrategy getAllBlobs,
            ContainsValueInListStrategy containsValueStrategy, PutBlobsStrategy putBlobsStrategy,
            ListContainerAndRecurseThroughFolders listStrategy, String containerName, ListContainerOptions options) {
      super(blobstore, getAllBlobs, containsValueStrategy, putBlobsStrategy, listStrategy, containerName, options);
   }

   @Override
   public Blob get(Object key) {
      String realKey = prefixer.apply(key.toString());
      Blob blob = blobstore.getBlob(containerName, realKey);
      return blob != null ? stripPrefix(blob) : null;
   }

   @Override
   public Blob put(String key, Blob value) {
      Blob returnVal = getLastValue(key);
      blobstore.putBlob(containerName, value);
      return returnVal;
   }

   @Override
   public void putAll(Map<? extends String, ? extends Blob> map) {
      putBlobsStrategy.execute(containerName, map.values());
   }

   @Override
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

   @Override
   public Collection<Blob> values() {
      return Sets.newLinkedHashSet(getAllBlobs.execute(containerName, options));
   }

   @Override
   public Blob newBlob(String name) {
      return blobstore.newBlob(name);
   }

}
