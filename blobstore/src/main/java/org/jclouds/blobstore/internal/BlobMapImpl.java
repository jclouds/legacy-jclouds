/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListContainerAndRecurseThroughFolders;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Map representation of a live connection to a Blob Service.
 * 
 * @see BlobStore
 * @see BaseBlobMap
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7. Please use {@link BlobStore}
 */
@Deprecated
public class BlobMapImpl extends BaseBlobMap<Blob> implements BlobMap {
   public static class CorrectBlobName implements Function<java.util.Map.Entry<? extends String, ? extends Blob>, Blob> {
      private final Function<String, String> prefixer;

      public CorrectBlobName(Function<String, String> prefixer) {
         this.prefixer = checkNotNull(prefixer, "prefixer");
      }

      @Override
      public Blob apply(java.util.Map.Entry<? extends String, ? extends Blob> arg0) {
         return apply(arg0.getKey(), arg0.getValue());
      }

      public Blob apply(String key, Blob blob) {
         blob.getMetadata().setName(prefixer.apply(key));
         return blob;
      }
   }

   private final CorrectBlobName correctBlobName;
   private final Provider<BlobBuilder> blobBuilders;

   @Inject
   public BlobMapImpl(BlobStore blobstore, GetBlobsInListStrategy getAllBlobs,
         ContainsValueInListStrategy containsValueStrategy, PutBlobsStrategy putBlobsStrategy,
         ListContainerAndRecurseThroughFolders listStrategy, String containerName, ListContainerOptions options,
         Provider<BlobBuilder> blobBuilders) {
      super(blobstore, getAllBlobs, containsValueStrategy, putBlobsStrategy, listStrategy, containerName, options);
      this.correctBlobName = new CorrectBlobName(prefixer);
      this.blobBuilders = checkNotNull(blobBuilders, "blobBuilders");
   }

   @Override
   public Blob get(Object key) {
      String realKey = prefixer.apply(checkNotNull(key, "key").toString());
      Blob blob = blobstore.getBlob(containerName, realKey);
      return blob != null ? stripPrefix(blob) : null;
   }

   @Override
   public Blob put(String key, Blob value) {
      Blob returnVal = getLastValue(checkNotNull(key, "key"));
      blobstore.putBlob(containerName, correctBlobName.apply(key, value));
      return returnVal;
   }

   @Override
   public void putAll(Map<? extends String, ? extends Blob> map) {
      putBlobsStrategy.execute(containerName, transform(checkNotNull(map, "map").entrySet(), correctBlobName));
   }

   @Override
   public Blob remove(Object key) {
      Blob old = getLastValue(checkNotNull(key, "key"));
      String realKey = prefixer.apply(key.toString());
      blobstore.removeBlob(containerName, realKey);
      return old;
   }

   private Blob getLastValue(Object key) {
      Blob old;
      try {
         old = get(checkNotNull(key, "key"));
      } catch (KeyNotFoundException e) {
         old = null;
      }
      return old;
   }

   @Override
   public Collection<Blob> values() {
      return ImmutableSet.copyOf(getAllBlobs.execute(containerName, options));
   }

   @Override
   public BlobBuilder blobBuilder() {
      return blobBuilders.get();
   }
}
