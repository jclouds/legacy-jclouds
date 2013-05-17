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

import static com.google.common.base.Functions.identity;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ListableMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions.ImmutableListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListContainerAndRecurseThroughFolders;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Implements core Map functionality with a {@link BlobStore}
 * 
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7. Please use {@link BlobStore}
 */
@Deprecated
public abstract class BaseBlobMap<V> implements ListableMap<String, V> {
   protected final BlobStore blobstore;
   protected final String containerName;
   protected final Function<String, String> prefixer;
   protected final Function<String, String> pathStripper;
   protected final ListContainerOptions options;
   protected final GetBlobsInListStrategy getAllBlobs;
   protected final ContainsValueInListStrategy containsValueStrategy;
   protected final ListContainerAndRecurseThroughFolders listStrategy;
   protected final PutBlobsStrategy putBlobsStrategy;

   static class StripPath implements Function<String, String> {
      private final String prefix;
      private final String delimiter;

      StripPath(String prefix, String delimiter) {
         this.prefix = checkNotNull(prefix, "prefix");
         this.delimiter = checkNotNull(delimiter, "delimiter");
      }

      public String apply(String from) {
         return from.replaceFirst(prefix + delimiter, "");
      }
   }

   static class PrefixKey implements Function<String, String> {
      private final String prefix;
      private final String delimiter;

      PrefixKey(String prefix, String delimiter) {
         this.prefix = checkNotNull(prefix, "prefix");
         this.delimiter = checkNotNull(delimiter, "delimiter");
      }

      public String apply(String from) {
         return prefix + delimiter + from;
      }
   }

   @Inject
   public BaseBlobMap(BlobStore blobstore, GetBlobsInListStrategy getAllBlobs,
         ContainsValueInListStrategy containsValueStrategy, PutBlobsStrategy putBlobsStrategy,
         ListContainerAndRecurseThroughFolders listStrategy, String containerName, ListContainerOptions options) {
      this.blobstore = checkNotNull(blobstore, "blobstore");
      this.containerName = checkNotNull(containerName, "container");
      checkArgument(containerName.indexOf('/') == -1,
            "please specify directory path using the option: inDirectory, not encoded in the container name");
      this.options = checkNotNull(options, "options") instanceof ImmutableListContainerOptions ? options
            : new ImmutableListContainerOptions(options);
      String dir = options.getDir();
      if (dir == null) {
         prefixer = identity();
         pathStripper = prefixer;
      } else {
         prefixer = new PrefixKey(dir, "/");
         pathStripper = new StripPath(dir, "/");
      }
      this.getAllBlobs = checkNotNull(getAllBlobs, "getAllBlobs");
      this.listStrategy = checkNotNull(listStrategy, "listStrategy");
      this.containsValueStrategy = checkNotNull(containsValueStrategy, "containsValueStrategy");
      this.putBlobsStrategy = checkNotNull(putBlobsStrategy, "putBlobsStrategy");
      checkArgument(!containerName.equals(""), "container name must not be a blank string!");
   }

   @Override
   public Set<java.util.Map.Entry<String, V>> entrySet() {
      return ImmutableSet.copyOf(transform(list(), new Function<BlobMetadata, Map.Entry<String, V>>() {
         @Override
         public java.util.Map.Entry<String, V> apply(BlobMetadata from) {
            return new Entry(pathStripper.apply(from.getName()));
         }
      }));
   }

   public class Entry implements java.util.Map.Entry<String, V> {

      private final String key;

      Entry(String key) {
         this.key = key;
      }

      @Override
      public String getKey() {
         return key;
      }

      @Override
      public V getValue() {
         return get(prefixer.apply(key));
      }

      @Override
      public V setValue(V value) {
         return put(prefixer.apply(key), value);
      }

   }

   @Override
   public int size() {
      return (int) blobstore.countBlobs(containerName, options);
   }

   protected Iterable<Blob> getAllBlobs() {
      Iterable<Blob> returnVal = getAllBlobs.execute(containerName, options);
      if (options != null) {
         for (Blob from : returnVal)
            stripPrefix(from);
      }
      return returnVal;
   }

   protected Blob stripPrefix(Blob from) {
      from.getMetadata().setName(pathStripper.apply(from.getMetadata().getName()));
      return from;
   }

   @Override
   public boolean containsValue(Object value) {
      return containsValueStrategy.execute(containerName, value, options);
   }

   @Override
   public void clear() {
      blobstore.clearContainer(containerName, options);
   }

   @Override
   public Set<String> keySet() {
      return ImmutableSet.copyOf(transform(list(), new Function<BlobMetadata, String>() {
         @Override
         public String apply(BlobMetadata from) {
            return from.getName();
         }
      }));
   }

   @Override
   public boolean containsKey(Object key) {
      String realKey = prefixer.apply(checkNotNull(key, "key").toString());
      return blobstore.blobExists(containerName, realKey);
   }

   @Override
   public boolean isEmpty() {
      return size() == 0;
   }

   public Iterable<? extends BlobMetadata> list() {
      return transform(listStrategy.execute(containerName, options), new Function<BlobMetadata, BlobMetadata>() {
         public BlobMetadata apply(BlobMetadata from) {
            MutableBlobMetadata md = new MutableBlobMetadataImpl(from);
            if (options.getDir() != null)
               md.setName(pathStripper.apply(from.getName()));
            return md;
         }

      });
   }

   @Override
   public String toString() {
      return "[containerName=" + containerName + ", options=" + options + "]";
   }

}
