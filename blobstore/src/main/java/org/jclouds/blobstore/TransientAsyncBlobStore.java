/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.blobstore;

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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
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
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.DateService;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.DelegatingPayload;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link BaseAsyncBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class TransientAsyncBlobStore extends BaseAsyncBlobStore {

   protected final DateService dateService;
   protected final Crypto crypto;
   protected final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;
   protected final ConcurrentMap<String, Location> containerToLocation;
   protected final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   protected final IfDirectoryReturnNameStrategy ifDirectoryReturnName;
   protected final Factory blobFactory;

   @Inject
   protected TransientAsyncBlobStore(BlobStoreContext context, DateService dateService, Crypto crypto,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            ConcurrentMap<String, Location> containerToLocation,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter,
            IfDirectoryReturnNameStrategy ifDirectoryReturnName, Blob.Factory blobFactory, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.blobFactory = blobFactory;
      this.dateService = dateService;
      this.crypto = crypto;
      this.containerToBlobs = containerToBlobs;
      this.containerToLocation = containerToLocation;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.ifDirectoryReturnName = ifDirectoryReturnName;
      getContainerToLocation().put("stub", defaultLocation.get());
      getContainerToBlobs().put("stub", new ConcurrentHashMap<String, Blob>());
   }

   /**
    * default maxResults is 1000
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container, ListContainerOptions options) {
      final Map<String, Blob> realContents = getContainerToBlobs().get(container);

      if (realContents == null)
         return immediateFailedFuture(cnfe(container));

      SortedSet<StorageMetadata> contents = newTreeSet(transform(realContents.keySet(),
               new Function<String, StorageMetadata>() {
                  public StorageMetadata apply(String key) {
                     Blob oldBlob = realContents.get(key);
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

      String marker = null;
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

      final String delimiter = options.isRecursive() ? null : "/";
      if (delimiter != null) {
         SortedSet<String> commonPrefixes = null;
         Iterable<String> iterable = transform(contents, new CommonPrefixes(prefix != null ? prefix : null, delimiter));
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

      return Futures.<PageSet<? extends StorageMetadata>> immediateFuture(new PageSetImpl<StorageMetadata>(contents,
               marker));

   }

   private ContainerNotFoundException cnfe(final String name) {
      return new ContainerNotFoundException(name, String.format("container %s not in %s", name, getContainerToBlobs()
               .keySet()));
   }

   public static MutableBlobMetadata copy(MutableBlobMetadata in) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutput os;
      try {
         os = new ObjectOutputStream(bout);
         os.writeObject(in);
         ObjectInput is = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
         MutableBlobMetadata out = (MutableBlobMetadata) is.readObject();
         convertUserMetadataKeysToLowercase(out);
         HttpUtils.copy(in.getContentMetadata(), out.getContentMetadata());
         return out;
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
      if (getContainerToBlobs().containsKey(container)) {
         getContainerToBlobs().get(container).remove(key);
      }
      return immediateFuture(null);
   }

   public ListenableFuture<Blob> removeBlobAndReturnOld(String container, String key) {
      if (getContainerToBlobs().containsKey(container)) {
         return immediateFuture(getContainerToBlobs().get(container).remove(key));
      }
      return immediateFuture(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      getContainerToBlobs().get(container).clear();
      return immediateFuture(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      if (getContainerToBlobs().containsKey(container)) {
         getContainerToBlobs().remove(container);
      }
      return immediateFuture(null);
   }

   public ListenableFuture<Boolean> deleteContainerImpl(final String container) {
      Boolean returnVal = true;
      if (getContainerToBlobs().containsKey(container)) {
         if (getContainerToBlobs().get(container).size() == 0)
            getContainerToBlobs().remove(container);
         else
            returnVal = false;
      }
      return immediateFuture(returnVal);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> containerExists(final String container) {
      return immediateFuture(getContainerToBlobs().containsKey(container));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return Futures.<PageSet<? extends StorageMetadata>> immediateFuture(new PageSetImpl<StorageMetadata>(transform(
               getContainerToBlobs().keySet(), new Function<String, StorageMetadata>() {
                  public StorageMetadata apply(String name) {
                     MutableStorageMetadata cmd = create();
                     cmd.setName(name);
                     cmd.setType(StorageType.CONTAINER);
                     cmd.setLocation(getContainerToLocation().get(name));
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
   public ListenableFuture<Boolean> createContainerInLocation(final Location location, final String name) {
      if (!getContainerToBlobs().containsKey(name)) {
         getContainerToBlobs().put(name, new ConcurrentHashMap<String, Blob>());
         getContainerToLocation().put(name, location != null ? location : defaultLocation.get());
      }
      return immediateFuture(getContainerToBlobs().containsKey(name));
   }

   /**
    * throws IllegalStateException if the container already exists
    */
   public ListenableFuture<Void> createContainerInLocationIfAbsent(final Location location, final String name) {
      ConcurrentMap<String, Blob> container = getContainerToBlobs().putIfAbsent(name,
               new ConcurrentHashMap<String, Blob>());
      if (container == null) {
         getContainerToLocation().put(name, location != null ? location : defaultLocation.get());
         return immediateFuture((Void) null);
      } else {
         return Futures.immediateFailedFuture(new IllegalStateException("container " + name + " already exists"));
      }
   }

   public String getFirstQueryOrNull(String string, @Nullable HttpRequestOptions options) {
      if (options == null)
         return null;
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
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

         public HttpRequest getRequest() {
            return new HttpRequest("GET", URI.create("http://stub"));
         }

         public int incrementFailureCount() {
            return 0;
         }

         public void setException(Exception exception) {

         }

      }, response);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<String> putBlob(String containerName, Blob in) {
      ConcurrentMap<String, Blob> container = getContainerToBlobs().get(containerName);
      if (container == null) {
         new IllegalStateException("containerName not found: " + containerName);
      }

      Blob blob = createUpdatedCopyOfBlob(in);

      container.put(blob.getMetadata().getName(), blob);

      return immediateFuture(Iterables.getOnlyElement(blob.getAllHeaders().get(HttpHeaders.ETAG)));
   }

   public ListenableFuture<Blob> putBlobAndReturnOld(String containerName, Blob in) {
      ConcurrentMap<String, Blob> container = getContainerToBlobs().get(containerName);
      if (container == null) {
         new IllegalStateException("containerName not found: " + containerName);
      }

      Blob blob = createUpdatedCopyOfBlob(in);

      Blob old = container.put(blob.getMetadata().getName(), blob);

      return immediateFuture(old);
   }

   protected Blob createUpdatedCopyOfBlob(Blob in) {
      ByteArrayPayload payload = (in.getPayload() instanceof ByteArrayPayload) ? ByteArrayPayload.class.cast(in
               .getPayload()) : null;
      if (payload == null)
         payload = (in.getPayload() instanceof DelegatingPayload) ? (DelegatingPayload.class.cast(in.getPayload())
                  .getDelegate() instanceof ByteArrayPayload) ? ByteArrayPayload.class.cast(DelegatingPayload.class
                  .cast(in.getPayload()).getDelegate()) : null : null;
      try {
         if (payload == null || !(payload instanceof ByteArrayPayload)) {
            MutableContentMetadata oldMd = in.getPayload().getContentMetadata();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in.getPayload().writeTo(out);
            payload = (ByteArrayPayload) Payloads.calculateMD5(Payloads.newPayload(out.toByteArray()));
            HttpUtils.copy(oldMd, payload.getContentMetadata());
         } else {
            if (payload.getContentMetadata().getContentMD5() == null)
               Payloads.calculateMD5(in, crypto.md5());
         }
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      Blob blob = blobFactory.create(copy(in.getMetadata()));
      blob.setPayload(payload);
      blob.getMetadata().setLastModified(new Date());
      String eTag = CryptoStreams.hex(payload.getContentMetadata().getContentMD5());
      blob.getMetadata().setETag(eTag);
      // Set HTTP headers to match metadata
      blob.getAllHeaders().replaceValues(HttpHeaders.LAST_MODIFIED,
               Collections.singleton(dateService.rfc822DateFormat(blob.getMetadata().getLastModified())));
      blob.getAllHeaders().replaceValues(HttpHeaders.ETAG, Collections.singleton(eTag));
      copyPayloadHeadersToBlob(payload, blob);
      blob.getAllHeaders().putAll(Multimaps.forMap(blob.getMetadata().getUserMetadata()));
      return blob;
   }

   private void copyPayloadHeadersToBlob(Payload payload, Blob blob) {
      HttpUtils.addContentHeadersFromMetadata(payload.getContentMetadata(), blob.getAllHeaders());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Boolean> blobExists(final String containerName, final String key) {
      if (!getContainerToBlobs().containsKey(containerName))
         return immediateFailedFuture(cnfe(containerName));
      Map<String, Blob> realContents = getContainerToBlobs().get(containerName);
      return immediateFuture(realContents.containsKey(key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenableFuture<Blob> getBlob(final String containerName, final String key, GetOptions options) {
      if (!getContainerToBlobs().containsKey(containerName))
         return immediateFailedFuture(cnfe(containerName));
      Map<String, Blob> realContents = getContainerToBlobs().get(containerName);
      if (!realContents.containsKey(key))
         return immediateFuture(null);

      Blob object = realContents.get(key);

      if (options.getIfMatch() != null) {
         if (!object.getMetadata().getETag().equals(options.getIfMatch()))
            return immediateFailedFuture(returnResponseException(412));
      }
      if (options.getIfNoneMatch() != null) {
         if (object.getMetadata().getETag().equals(options.getIfNoneMatch()))
            return immediateFailedFuture(returnResponseException(304));
      }
      if (options.getIfModifiedSince() != null) {
         Date modifiedSince = options.getIfModifiedSince();
         if (object.getMetadata().getLastModified().before(modifiedSince)) {
            HttpResponse response = new HttpResponse(304, null, null);
            return immediateFailedFuture(new HttpResponseException(String.format("%1$s is before %2$s", object
                     .getMetadata().getLastModified(), modifiedSince), null, response));
         }

      }
      if (options.getIfUnmodifiedSince() != null) {
         Date unmodifiedSince = options.getIfUnmodifiedSince();
         if (object.getMetadata().getLastModified().after(unmodifiedSince)) {
            HttpResponse response = new HttpResponse(412, null, null);
            return immediateFailedFuture(new HttpResponseException(String.format("%1$s is after %2$s", object
                     .getMetadata().getLastModified(), unmodifiedSince), null, response));
         }
      }
      Blob returnVal = copyBlob(object);

      if (options.getRanges() != null && options.getRanges().size() > 0) {
         byte[] data;
         try {
            data = toByteArray(returnVal.getPayload().getInput());
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
         returnVal.setPayload(out.toByteArray());
      }
      checkNotNull(returnVal.getPayload(), "payload " + returnVal);
      return immediateFuture(returnVal);
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

   private Blob copyBlob(Blob blob) {
      Blob returnVal = blobFactory.create(copy(blob.getMetadata()));
      returnVal.setPayload(blob.getPayload());
      copyPayloadHeadersToBlob(blob.getPayload(), returnVal);
      return returnVal;
   }

   public ConcurrentMap<String, ConcurrentMap<String, Blob>> getContainerToBlobs() {
      return containerToBlobs;
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      getContainerToBlobs().remove(container);
      return getContainerToBlobs().containsKey(container);
   }

   public ConcurrentMap<String, Location> getContainerToLocation() {
      return containerToLocation;
   }

}
