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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.SortedSet;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Implements core Map functionality with an {@link AsyncBlobStore}
 * <p/>
 * All commands will wait a maximum of ${jclouds.blobstore.timeout} milliseconds to complete before
 * throwing an exception.
 * 
 * @author Adrian Cole
 * @param <V>
 *           value of the map
 */
public abstract class BaseBlobMap<V> {
   protected final BlobStore blobstore;
   protected final String containerName;
   protected final Function<String, String> prefixer;
   protected final Function<String, String> pathStripper;
   protected final ListContainerOptions options;
   protected final GetBlobsInListStrategy getAllBlobs;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   protected final ContainsValueInListStrategy containsValueStrategy;
   protected final ClearListStrategy deleteBlobsStrategy;
   protected final CountListStrategy countStrategy;
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

   static class PassThrough<T> implements Function<T, T> {
      public T apply(T from) {
         return from;
      }
   }

   @Inject
   public BaseBlobMap(BlobStore blobstore, GetBlobsInListStrategy getAllBlobs,
            ListBlobMetadataStrategy getAllBlobMetadata,
            ContainsValueInListStrategy containsValueStrategy,
            ClearListStrategy deleteBlobsStrategy, CountListStrategy countStrategy,
            PutBlobsStrategy putBlobsStrategy, String containerName, ListContainerOptions options) {
      this.blobstore = checkNotNull(blobstore, "blobstore");
      this.containerName = checkNotNull(containerName, "container");
      this.options = options;
      if (options.getDir() == null) {
         prefixer = new PassThrough<String>();
         pathStripper = prefixer;
      } else {
         prefixer = new PrefixKey(options.getDir(), "/");
         pathStripper = new StripPath(options.getDir(), "/");
      }
      this.getAllBlobs = checkNotNull(getAllBlobs, "getAllBlobs");
      this.getAllBlobMetadata = checkNotNull(getAllBlobMetadata, "getAllBlobMetadata");
      this.containsValueStrategy = checkNotNull(containsValueStrategy, "containsValueStrategy");
      this.deleteBlobsStrategy = checkNotNull(deleteBlobsStrategy, "deleteBlobsStrategy");
      this.countStrategy = checkNotNull(countStrategy, "countStrategy");
      this.putBlobsStrategy = checkNotNull(putBlobsStrategy, "putBlobsStrategy");
      checkArgument(!containerName.equals(""), "container name must not be a blank string!");
   }

   /**
    * {@inheritDoc}
    * <p/>
    * This returns the number of keys in the {@link ListResponse}
    * 
    * @see ListResponse#getContents()
    */
   public int size() {
      return (int) countStrategy.execute(containerName, options);
   }

   /**
    * attempts asynchronous gets on all objects.
    * 
    * @see AsyncBlobStore#getBlob(String, String)
    */
   protected Set<? extends Blob> getAllBlobs() {
      Set<? extends Blob> returnVal = getAllBlobs.execute(containerName, options);
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

   /**
    * {@inheritDoc}
    * <p/>
    * Note that if value is an instance of InputStream, it will be read and closed following this
    * method. To reuse data from InputStreams, pass {@link java.io.InputStream}s inside {@link Blob}s
    */
   public boolean containsValue(Object value) {
      return containsValueStrategy.execute(containerName, value, options);
   }

   public void clear() {
      deleteBlobsStrategy.execute(containerName, options);
   }

   public Set<String> keySet() {
      Set<String> keys = Sets.newHashSet();
      for (StorageMetadata object : getAllBlobMetadata.execute(containerName, options))
         if (object.getType() == StorageType.BLOB)
            keys.add(pathStripper.apply(object.getName()));
      return keys;
   }

   public boolean containsKey(Object key) {
      String realKey = prefixer.apply(key.toString());
      return blobstore.blobExists(containerName, realKey);
   }

   public boolean isEmpty() {
      return size() == 0;
   }

   public SortedSet<? extends BlobMetadata> list() {
      SortedSet<? extends BlobMetadata> returnVal = getAllBlobMetadata.execute(containerName,
               options);
      if (options.getDir() != null) {
         returnVal = Sets.newTreeSet(Iterables.transform(returnVal,
                  new Function<BlobMetadata, BlobMetadata>() {

                     public BlobMetadata apply(BlobMetadata from) {
                        MutableBlobMetadata md = new MutableBlobMetadataImpl(from);
                        md.setName(pathStripper.apply(from.getName()));
                        return md;
                     }

                  }));
      }
      return returnVal;
   }

}