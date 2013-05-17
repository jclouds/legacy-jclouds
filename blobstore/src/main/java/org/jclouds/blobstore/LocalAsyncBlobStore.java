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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Implementation of {@link BaseAsyncBlobStore} which uses a pluggable
 * LocalStorageStrategy.
 *
 * @author Adrian Cole
 * @author Alfredo "Rainbowbreeze" Morresi
 * @author Andrew Gaul
 * @author James Murty
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please create and use {@link LocalBlobStore}
 */
@Deprecated
public class LocalAsyncBlobStore extends BaseAsyncBlobStore {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final ContentMetadataCodec contentMetadataCodec;
   protected final IfDirectoryReturnNameStrategy ifDirectoryReturnName;
   protected final Factory blobFactory;
   protected final LocalStorageStrategy storageStrategy;

   @Inject
   protected LocalAsyncBlobStore(BlobStoreContext context,
         BlobUtils blobUtils,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         Supplier<Location> defaultLocation,
         @Memoized Supplier<Set<? extends Location>> locations,
         ContentMetadataCodec contentMetadataCodec,
         IfDirectoryReturnNameStrategy ifDirectoryReturnName,
         Factory blobFactory, LocalStorageStrategy storageStrategy) {
      super(context, blobUtils, userExecutor, defaultLocation, locations);
      this.blobFactory = blobFactory;
      this.contentMetadataCodec = contentMetadataCodec;
      this.ifDirectoryReturnName = ifDirectoryReturnName;
      this.storageStrategy = storageStrategy;
   }

   /**
    * default maxResults is 1000
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container, ListContainerOptions options) {

      // Check if the container exists
      if (!storageStrategy.containerExists(container))
         return immediateFailedFuture(cnfe(container));

      // Loading blobs from container
      Iterable<String> blobBelongingToContainer = null;
      try {
         blobBelongingToContainer = storageStrategy.getBlobKeysInsideContainer(container);
      } catch (IOException e) {
         logger.error(e, "An error occurred loading blobs contained into container %s", container);
         Throwables.propagate(e);
      }

      SortedSet<StorageMetadata> contents = newTreeSet(transform(blobBelongingToContainer,
            new Function<String, StorageMetadata>() {
               public StorageMetadata apply(String key) {
                  Blob oldBlob = loadBlob(container, key);
                  checkState(oldBlob != null, "blob " + key + " is not present although it was in the list of "
                        + container);
                  checkState(oldBlob.getMetadata() != null, "blob " + container + "/" + key + " has no metadata");
                  MutableBlobMetadata md = BlobStoreUtils.copy(oldBlob.getMetadata());
                  String directoryName = ifDirectoryReturnName.execute(md);
                  if (directoryName != null) {
                     md.setName(directoryName);
                     md.setType(StorageType.RELATIVE_PATH);
                  }
                  return md;
               }
            }));

      String marker = null;
      if (options != null) {
         if (options.getMarker() != null) {
            final String finalMarker = options.getMarker();
            StorageMetadata lastMarkerMetadata = find(contents, new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata metadata) {
                  return metadata.getName().compareTo(finalMarker) > 0;
               }
            });
            contents = contents.tailSet(lastMarkerMetadata);
         }

         final String prefix = options.getDir();
         if (prefix != null) {
            contents = newTreeSet(filter(contents, new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata o) {
                  return o != null && o.getName().startsWith(prefix) && !o.getName().equals(prefix);
               }
            }));
         }

         int maxResults = options.getMaxResults() != null ? options.getMaxResults() : 1000;
         if (!contents.isEmpty()) {
            StorageMetadata lastElement = contents.last();
            contents = newTreeSet(Iterables.limit(contents, maxResults));
            if (!contents.contains(lastElement)) {
               // Partial listing
               marker = contents.last().getName();
            }
         }

         if (!options.isRecursive()) {
            String delimiter = storageStrategy.getSeparator();
            SortedSet<String> commonPrefixes = newTreeSet(
                   transform(contents, new CommonPrefixes(prefix, delimiter)));
            commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

            contents = newTreeSet(filter(contents, new DelimiterFilter(prefix, delimiter)));

            for (String o : commonPrefixes) {
               MutableStorageMetadata md = new MutableStorageMetadataImpl();
               md.setType(StorageType.RELATIVE_PATH);
               md.setName(o);
               contents.add(md);
            }
         }

         // trim metadata, if the response isn't supposed to be detailed.
         if (!options.isDetailed()) {
            for (StorageMetadata md : contents) {
               md.getUserMetadata().clear();
            }
         }
      }

      return Futures.<PageSet<? extends StorageMetadata>> immediateFuture(new PageSetImpl<StorageMetadata>(contents,
            marker));

   }

   private ContainerNotFoundException cnfe(final String name) {
      return new ContainerNotFoundException(name, String.format(
            "container %s not in %s", name,
            storageStrategy.getAllContainerNames()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Void> removeBlob(final String container, final String key) {
      storageStrategy.removeBlob(container, key);
      return immediateFuture(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      storageStrategy.clearContainer(container);
      return immediateFuture(null);
   }

   /**
    * Override parent method because it uses strange futures and listenables
    * that creates problem in the test if more than one test that deletes the
    * container is executed
    *
    * @param container
    * @return
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      deleteAndVerifyContainerGone(container);
      return immediateFuture(null);
   }

   public ListenableFuture<Boolean> deleteContainerIfEmpty(final String container) {
      Boolean returnVal = true;
      if (storageStrategy.containerExists(container)) {
         try {
            if (Iterables.isEmpty(storageStrategy.getBlobKeysInsideContainer(container)))
               storageStrategy.deleteContainer(container);
            else
               returnVal = false;
         } catch (IOException e) {
            logger.error(e, "An error occurred loading blobs contained into container %s", container);
            Throwables.propagate(e);
         }
      }
      return immediateFuture(returnVal);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> containerExists(final String containerName) {
      return immediateFuture(storageStrategy.containerExists(containerName));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      Iterable<String> containers = storageStrategy.getAllContainerNames();

      return Futures.<PageSet<? extends StorageMetadata>> immediateFuture(new PageSetImpl<StorageMetadata>(transform(
            containers, new Function<String, StorageMetadata>() {
               public StorageMetadata apply(String name) {
                  MutableStorageMetadata cmd = create();
                  cmd.setName(name);
                  cmd.setType(StorageType.CONTAINER);
                  cmd.setLocation(storageStrategy.getLocation(name));
                  return cmd;
               }
            }), null));
   }

   protected MutableStorageMetadata create() {
      return new MutableStorageMetadataImpl();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(final Location location,
         final String name) {
      boolean result = storageStrategy.createContainerInLocation(name, location);
      return immediateFuture(result);
   }

   private Blob loadBlob(final String container, final String key) {
      logger.debug("Opening blob in container: %s - %s", container, key);
      return storageStrategy.getBlob(container, key);
   }

   protected static class DelimiterFilter implements Predicate<StorageMetadata> {
      private final String prefix;
      private final String delimiter;

      public DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(StorageMetadata metadata) {
         if (prefix == null)
            return metadata.getName().indexOf(delimiter) == -1;
         // ensure we don't accidentally append twice
         String toMatch = prefix.endsWith("/") ? prefix : prefix + delimiter;
         if (metadata.getName().startsWith(toMatch)) {
            String unprefixedName = metadata.getName().replaceFirst(toMatch, "");
            if (unprefixedName.equals("")) {
               // we are the prefix in this case, return false
               return false;
            }
            return unprefixedName.indexOf(delimiter) == -1;
         }
         return false;
      }
   }

   protected static class CommonPrefixes implements Function<StorageMetadata, String> {
      private final String prefix;
      private final String delimiter;
      public static final String NO_PREFIX = "NO_PREFIX";

      public CommonPrefixes(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public String apply(StorageMetadata metadata) {
         String working = metadata.getName();
         if (prefix != null) {
            // ensure we don't accidentally append twice
            String toMatch = prefix.endsWith("/") ? prefix : prefix + delimiter;
            if (working.startsWith(toMatch)) {
               working = working.replaceFirst(toMatch, "");
            }
         }
         if (working.contains(delimiter)) {
            return working.substring(0, working.indexOf(delimiter));
         }
         return NO_PREFIX;
      }
   }

   public static HttpResponseException returnResponseException(int code) {
      HttpResponse response = HttpResponse.builder().statusCode(code).build();
      return new HttpResponseException(new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://stub")
            .build()), response);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<String> putBlob(String containerName, Blob blob) {
      checkNotNull(containerName, "containerName must be set");
      checkNotNull(blob, "blob must be set");
      String blobKey = blob.getMetadata().getName();

      logger.debug("Put blob with key [%s] to container [%s]", blobKey, containerName);
      if (!storageStrategy.containerExists(containerName)) {
         return Futures.immediateFailedFuture(new IllegalStateException("containerName not found: " + containerName));
      }

      try {
         return immediateFuture(storageStrategy.putBlob(containerName, blob));
      } catch (IOException e) {
         logger.error(e, "An error occurred storing the new blob with name [%s] to container [%s].", blobKey,
               containerName);
         throw Throwables.propagate(e);
      }
   }

   private void copyPayloadHeadersToBlob(Payload payload, Blob blob) {
      blob.getAllHeaders().putAll(contentMetadataCodec.toHeaders(payload.getContentMetadata()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> blobExists(final String containerName, final String key) {
      if (!storageStrategy.containerExists(containerName))
         return immediateFailedFuture(cnfe(containerName));
      return immediateFuture(storageStrategy.blobExists(containerName, key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Blob> getBlob(final String containerName, final String key, GetOptions options) {
      logger.debug("Retrieving blob with key %s from container %s", key, containerName);
      // If the container doesn't exist, an exception is thrown
      if (!storageStrategy.containerExists(containerName)) {
         logger.debug("Container %s does not exist", containerName);
         return immediateFailedFuture(cnfe(containerName));
      }
      // If the blob doesn't exist, a null object is returned
      if (!storageStrategy.blobExists(containerName, key)) {
         logger.debug("Item %s does not exist in container %s", key, containerName);
         return immediateFuture(null);
      }

      Blob blob = loadBlob(containerName, key);

      if (options != null) {
         if (options.getIfMatch() != null) {
            if (!blob.getMetadata().getETag().equals(options.getIfMatch()))
               return immediateFailedFuture(returnResponseException(412));
         }
         if (options.getIfNoneMatch() != null) {
            if (blob.getMetadata().getETag().equals(options.getIfNoneMatch()))
               return immediateFailedFuture(returnResponseException(304));
         }
         if (options.getIfModifiedSince() != null) {
            Date modifiedSince = options.getIfModifiedSince();
            if (blob.getMetadata().getLastModified().before(modifiedSince)) {
               HttpResponse response = HttpResponse.builder().statusCode(304).build();
               return immediateFailedFuture(new HttpResponseException(String.format("%1$s is before %2$s", blob
                     .getMetadata().getLastModified(), modifiedSince), null, response));
            }

         }
         if (options.getIfUnmodifiedSince() != null) {
            Date unmodifiedSince = options.getIfUnmodifiedSince();
            if (blob.getMetadata().getLastModified().after(unmodifiedSince)) {
               HttpResponse response = HttpResponse.builder().statusCode(412).build();
               return immediateFailedFuture(new HttpResponseException(String.format("%1$s is after %2$s", blob
                     .getMetadata().getLastModified(), unmodifiedSince), null, response));
            }
         }
         blob = copyBlob(blob);

         if (options.getRanges() != null && options.getRanges().size() > 0) {
            byte[] data;
            try {
               data = toByteArray(blob.getPayload());
            } catch (IOException e) {
               return immediateFailedFuture(new RuntimeException(e));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (String s : options.getRanges()) {
               // HTTP uses a closed interval while Java array indexing uses a
               // half-open interval.
               int offset = 0;
               int last = data.length - 1;
               if (s.startsWith("-")) {
                  offset = last - Integer.parseInt(s.substring(1)) + 1;
               } else if (s.endsWith("-")) {
                  offset = Integer.parseInt(s.substring(0, s.length() - 1));
               } else if (s.contains("-")) {
                  String[] firstLast = s.split("\\-");
                  offset = Integer.parseInt(firstLast[0]);
                  last = Integer.parseInt(firstLast[1]);
               } else {
                  return immediateFailedFuture(new IllegalArgumentException("illegal range: " + s));
               }

               if (offset > last) {
                  return immediateFailedFuture(new IllegalArgumentException("illegal range: " + s));
               }
               if (last + 1 > data.length) {
                  last = data.length - 1;
               }
               out.write(data, offset, last - offset + 1);
            }
            ContentMetadata cmd = blob.getPayload().getContentMetadata();
            byte[] byteArray = out.toByteArray();
            blob.setPayload(byteArray);
            HttpUtils.copy(cmd, blob.getPayload().getContentMetadata());
            blob.getPayload().getContentMetadata().setContentLength(Long.valueOf(byteArray.length));
         }
      }
      checkNotNull(blob.getPayload(), "payload " + blob);
      return immediateFuture(blob);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(final String container, final String key) {
      try {
         Blob blob = getBlob(container, key).get();
         return immediateFuture(blob != null ? (BlobMetadata) BlobStoreUtils.copy(blob.getMetadata()) : null);
      } catch (Exception e) {
         if (size(filter(getCausalChain(e), KeyNotFoundException.class)) >= 1)
            return immediateFuture(null);
         return immediateFailedFuture(e);
      }
   }

   private Blob copyBlob(Blob blob) {
      Blob returnVal = blobFactory.create(BlobStoreUtils.copy(blob.getMetadata()));
      returnVal.setPayload(blob.getPayload());
      copyPayloadHeadersToBlob(blob.getPayload(), returnVal);
      return returnVal;
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(final String container) {
      storageStrategy.deleteContainer(container);
      return storageStrategy.containerExists(container);
   }

   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
      // TODO implement options
      return putBlob(container, blob);
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
         CreateContainerOptions options) {
      if (options.isPublicRead())
         throw new UnsupportedOperationException("publicRead");
      return createContainerInLocation(location, container);
   }
}
