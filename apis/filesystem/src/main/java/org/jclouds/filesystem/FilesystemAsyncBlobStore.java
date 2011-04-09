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
package org.jclouds.filesystem;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.DateService;
import org.jclouds.domain.Location;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.strategy.FilesystemStorageStrategy;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * Preconditions: Blob name cannot start with / char (or \ under windows)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemAsyncBlobStore extends BaseAsyncBlobStore {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;
   protected final Crypto crypto;
   protected final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   protected final IfDirectoryReturnNameStrategy ifDirectoryReturnName;
   protected final Factory blobFactory;
   protected final FilesystemStorageStrategy storageStrategy;

   @Inject
   protected FilesystemAsyncBlobStore(BlobStoreContext context, DateService dateService, Crypto crypto,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter,
            IfDirectoryReturnNameStrategy ifDirectoryReturnName, Factory blobFactory, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, FilesystemStorageStrategy storageStrategy) {
      super(context, blobUtils, service, defaultLocation, locations);
      // super(context, blobUtils, service, null, null);
      this.blobFactory = blobFactory;
      this.dateService = dateService;
      this.crypto = crypto;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.ifDirectoryReturnName = ifDirectoryReturnName;
      this.storageStrategy = checkNotNull(storageStrategy, "Storage strategy");
   }

   /**
    * default maxResults is 1000
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container, ListContainerOptions options) {

      // Check if the container exists
      if (!containerExistsSyncImpl(container)) {
         return immediateFailedFuture(cnfe(container));
      }

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
                     Blob oldBlob = loadFileBlob(container, key);

                     checkState(oldBlob != null, "blob " + key + " is not present although it was in the list of "
                              + container);
                     checkState(oldBlob.getMetadata() != null, "blob " + container + "/" + key + " has no metadata");
                     MutableBlobMetadata md = copy(oldBlob.getMetadata());
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
                  return metadata.getName().equals(finalMarker);
               }
            });
            contents = contents.tailSet(lastMarkerMetadata);
            contents.remove(lastMarkerMetadata);
         }

         final String prefix = options.getDir();
         if (prefix != null) {
            contents = newTreeSet(filter(contents, new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata o) {
                  return (o != null && o.getName().startsWith(prefix) && !o.getName().equals(prefix));
               }
            }));
         }

         Integer maxResults = options.getMaxResults() != null ? options.getMaxResults() : 1000;
         if (contents.size() > 0) {
            SortedSet<StorageMetadata> contentsSlice = firstSliceOfSize(contents, maxResults);
            if (!contentsSlice.contains(contents.last())) {
               // Partial listing
               marker = contentsSlice.last().getName();
            } else {
               marker = null;
            }
            contents = contentsSlice;
         }

         final String delimiter = options.isRecursive() ? null : File.separator;
         if (delimiter != null) {
            SortedSet<String> commonPrefixes = null;
            Iterable<String> iterable = transform(contents, new CommonPrefixes(prefix != null ? prefix : null,
                     delimiter));
            commonPrefixes = iterable != null ? newTreeSet(iterable) : new TreeSet<String>();
            commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

            contents = newTreeSet(filter(contents, new DelimiterFilter(prefix != null ? prefix : null, delimiter)));

            Iterables.<StorageMetadata> addAll(contents, transform(commonPrefixes,
                     new Function<String, StorageMetadata>() {
                        public StorageMetadata apply(String o) {
                           MutableStorageMetadata md = new MutableStorageMetadataImpl();
                           md.setType(StorageType.RELATIVE_PATH);
                           md.setName(o);
                           return md;
                        }
                     }));
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
      return new ContainerNotFoundException(name, String.format("container %s not in filesystem", name));
   }

   public static MutableBlobMetadata copy(MutableBlobMetadata in) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutput os;
      try {
         os = new ObjectOutputStream(bout);
         os.writeObject(in);
         ObjectInput is = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
         MutableBlobMetadata metadata = (MutableBlobMetadata) is.readObject();
         convertUserMetadataKeysToLowercase(metadata);
         metadata.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(in.getContentMetadata().toBuilder()
                  .build()));
         return metadata;
      } catch (Exception e) {
         propagate(e);
         assert false : "exception should have propagated: " + e;
         return null;
      }
   }

   private static void convertUserMetadataKeysToLowercase(MutableBlobMetadata metadata) {
      Map<String, String> lowerCaseUserMetadata = newHashMap();
      for (Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
         lowerCaseUserMetadata.put(entry.getKey().toLowerCase(), entry.getValue());
      }
      metadata.setUserMetadata(lowerCaseUserMetadata);
   }

   public static MutableBlobMetadata copy(MutableBlobMetadata in, String newKey) {
      MutableBlobMetadata newMd = copy(in);
      newMd.setName(newKey);
      return newMd;
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
   public ListenableFuture<Boolean> containerExists(final String containerName) {
      boolean exists = containerExistsSyncImpl(containerName);
      return immediateFuture(exists);
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
   @Path("{container}")
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(final Location location,
            @PathParam("container") @ParamValidators( { FilesystemContainerNameValidator.class }) String name) {
      boolean result = storageStrategy.createContainer(name);
      return immediateFuture(result);
   }

   public String getFirstQueryOrNull(String string, @Nullable HttpRequestOptions options) {
      if (options == null)
         return null;
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   /**
    * Load the blob with the given key belonging to the container with the given name. There must
    * exist a resource on the file system whose complete name is given concatenating the container
    * name and the key
    * 
    * @param container
    *           it's the name of the container the blob belongs to
    * @param key
    *           it's the key of the blob
    * 
    * @return the blob belonging to the given container with the given key
    */
   private Blob loadFileBlob(final String container, final String key) {
      logger.debug("Opening blob in container: %s - %s", container, key);
      File blobPayload = storageStrategy.getFileForBlobKey(container, key);

      Payload payload = new FilePayload(blobPayload);
      // Loading object metadata
      MutableBlobMetadata metadata = new MutableBlobMetadataImpl();
      metadata.setName(key);
      metadata.setLastModified(new Date(blobPayload.lastModified()));
      metadata.getContentMetadata().setContentLength(blobPayload.length());
      // TODO What about the MD5? are we supposed to calculate it each time we load
      // the file?
      try {
         payload = Payloads.calculateMD5(payload);
      } catch (IOException e) {
         logger.error("An error occurred calculating MD5 for blob %s from container ", key, container);
         Throwables.propagateIfPossible(e);
      }
      metadata.getContentMetadata().setContentType("");
      String eTag = CryptoStreams.hex(payload.getContentMetadata().getContentMD5());
      metadata.setETag(eTag);
      // Creating new blob object
      Blob blob = blobFactory.create(metadata);
      blob.setPayload(blobPayload);
      return blob;
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

   public static <T extends Comparable<?>> SortedSet<T> firstSliceOfSize(Iterable<T> elements, int size) {
      List<List<T>> slices = partition(newArrayList(elements), size);
      return newTreeSet(slices.get(0));
   }

   public static HttpResponseException returnResponseException(int code) {
      HttpResponse response = null;
      response = new HttpResponse(code, null, null);
      return new HttpResponseException(new HttpCommand() {

         public int getRedirectCount() {
            return 0;
         }

         public int incrementRedirectCount() {
            return 0;
         }

         public boolean isReplayable() {
            return false;
         }

         public Exception getException() {
            return null;
         }

         public int getFailureCount() {
            return 0;
         }

         public int incrementFailureCount() {
            return 0;
         }

         public void setException(Exception exception) {

         }

         @Override
         public HttpRequest getCurrentRequest() {
            return new HttpRequest("GET", URI.create("http://stub"));
         }

         @Override
         public void setCurrentRequest(HttpRequest request) {

         }

      }, response);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<String> putBlob(String containerName, Blob object) {
      String blobKey = object.getMetadata().getName();

      logger.debug("Put object with key [%s] to container [%s]", blobKey, containerName);
      String eTag = getEtag(object);
      try {
         // TODO
         // must override existing file?

         storageStrategy.writePayloadOnFile(containerName, blobKey, object.getPayload());
      } catch (IOException e) {
         logger.error(e, "An error occurred storing the new object with name [%s] to container [%s].", blobKey,
                  containerName);
         Throwables.propagate(e);
      }
      return immediateFuture(eTag);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> blobExists(final String containerName, final String key) {
      return immediateFuture(storageStrategy.blobExists(containerName, key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Blob> getBlob(final String containerName, final String key, GetOptions options) {
      logger.debug("Retrieving blob with key %s from container %s", key, containerName);
      // If the container doesn't exist, an exception is thrown
      if (!containerExistsSyncImpl(containerName)) {
         logger.debug("Container %s does not exist", containerName);
         return immediateFailedFuture(cnfe(containerName));
      }
      // If the blob doesn't exist, a null object is returned
      if (!storageStrategy.blobExists(containerName, key)) {
         logger.debug("Item %s does not exist in container %s", key, containerName);
         return immediateFuture(null);
      }

      Blob blob = loadFileBlob(containerName, key);

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
               HttpResponse response = new HttpResponse(304, null, null);
               return immediateFailedFuture(new HttpResponseException(String.format("%1$s is before %2$s", blob
                        .getMetadata().getLastModified(), modifiedSince), null, response));
            }

         }
         if (options.getIfUnmodifiedSince() != null) {
            Date unmodifiedSince = options.getIfUnmodifiedSince();
            if (blob.getMetadata().getLastModified().after(unmodifiedSince)) {
               HttpResponse response = new HttpResponse(412, null, null);
               return immediateFailedFuture(new HttpResponseException(String.format("%1$s is after %2$s", blob
                        .getMetadata().getLastModified(), unmodifiedSince), null, response));
            }
         }

         if (options.getRanges() != null && options.getRanges().size() > 0) {
            byte[] data;
            try {
               data = toByteArray(blob.getPayload().getInput());
            } catch (IOException e) {
               return immediateFailedFuture(new RuntimeException(e));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (String s : options.getRanges()) {
               if (s.startsWith("-")) {
                  int length = Integer.parseInt(s.substring(1));
                  out.write(data, data.length - length, length);
               } else if (s.endsWith("-")) {
                  int offset = Integer.parseInt(s.substring(0, s.length() - 1));
                  out.write(data, offset, data.length - offset);
               } else if (s.contains("-")) {
                  String[] firstLast = s.split("\\-");
                  int offset = Integer.parseInt(firstLast[0]);
                  int last = Integer.parseInt(firstLast[1]);
                  int length = (last < data.length) ? last + 1 : data.length - offset;
                  out.write(data, offset, length);
               } else {
                  return immediateFailedFuture(new IllegalArgumentException("first and last were null!"));
               }

            }
            blob.setPayload(out.toByteArray());
            blob.getMetadata().getContentMetadata().setContentLength(new Long(data.length));
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
         return immediateFuture(blob != null ? (BlobMetadata) copy(blob.getMetadata()) : null);
      } catch (Exception e) {
         if (size(filter(getCausalChain(e), KeyNotFoundException.class)) >= 1)
            return immediateFuture(null);
         return immediateFailedFuture(e);
      }
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      storageStrategy.deleteContainer(container);
      return containerExistsSyncImpl(container);
   }

   /**
    * Override parent method because it uses strange futures and listenables that creates problem in
    * the test if more than one test that deletes the container is executed
    * 
    * @param container
    * @return
    */
   @Override
   public ListenableFuture<Void> deleteContainer(String container) {
      deleteAndVerifyContainerGone(container);
      return immediateFuture(null);
   }

   /**
    * Each container is a directory, so in order to check if a container exists the corresponding
    * directory must exists. Synchronous implementation
    * 
    * @param containerName
    * @return
    */
   private boolean containerExistsSyncImpl(String containerName) {
      return storageStrategy.containerExists(containerName);
   }

   /**
    * 
    * Calculates the object MD5 and returns it as eTag
    * 
    * @param object
    * @return
    */
   private String getEtag(Blob object) {
      try {
         Payloads.calculateMD5(object, crypto.md5());
      } catch (IOException ex) {
         logger.error(ex, "An error occurred calculating MD5 for object with name %s.", object.getMetadata().getName());
         Throwables.propagate(ex);
      }

      String eTag = CryptoStreams.hex(object.getPayload().getContentMetadata().getContentMD5());
      return eTag;
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
