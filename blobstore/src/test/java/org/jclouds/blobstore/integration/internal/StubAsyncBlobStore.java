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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.makeListenable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.ListContainerResponseImpl;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.IsDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.date.DateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.HttpRequestOptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.internal.Nullable;

/**
 * Implementation of {@link S3BlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubAsyncBlobStore implements AsyncBlobStore {

   protected final DateService dateService;
   protected final EncryptionService encryptionService;
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;
   protected final Blob.Factory blobProvider;
   protected final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   protected final GetDirectoryStrategy getDirectoryStrategy;
   protected final MkdirStrategy mkdirStrategy;
   private final IsDirectoryStrategy isDirectoryStrategy;

   protected final ExecutorService service;

   @Inject
   protected StubAsyncBlobStore(
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            DateService dateService, EncryptionService encryptionService,
            Blob.Factory blobProvider, GetDirectoryStrategy getDirectoryStrategy,
            MkdirStrategy mkdirStrategy, IsDirectoryStrategy isDirectoryStrategy,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ExecutorService service) {
      this.dateService = checkNotNull(dateService, "dateService");
      this.encryptionService = checkNotNull(encryptionService, "encryptionService");
      this.containerToBlobs = checkNotNull(containerToBlobs, "containerToBlobs");
      this.blobProvider = checkNotNull(blobProvider, "blobProvider");
      this.getDirectoryStrategy = checkNotNull(getDirectoryStrategy, "getDirectoryStrategy");
      this.mkdirStrategy = checkNotNull(mkdirStrategy, "mkdirStrategy");
      this.isDirectoryStrategy = checkNotNull(isDirectoryStrategy, "isDirectoryStrategy");
      this.httpGetOptionsConverter = checkNotNull(httpGetOptionsConverter,
               "httpGetOptionsConverter");
      this.service = checkNotNull(service, "service");
      getContainerToBlobs().put("stub", new ConcurrentHashMap<String, Blob>());
   }

   /**
    * @throws java.io.IOException
    */
   public static byte[] toByteArray(Object data) throws IOException {
      checkNotNull(data, "data must be set before calling generateETag()");
      byte[] bytes = null;
      if (data == null || data instanceof byte[]) {
         bytes = (byte[]) data;
      } else if (data instanceof String) {
         bytes = ((String) data).getBytes();
      } else if (data instanceof File || data instanceof InputStream) {
         InputStream io = (data instanceof InputStream) ? (InputStream) data : new FileInputStream(
                  (File) data);
         bytes = ByteStreams.toByteArray(io);
         Closeables.closeQuietly(io);
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return bytes;

   }

   public ListenableFuture<Blob> getBlob(final String bucketName, final String key) {
      if (!getContainerToBlobs().containsKey(bucketName))
         return immediateFailedFuture(new ContainerNotFoundException(bucketName));
      Map<String, Blob> realContents = getContainerToBlobs().get(bucketName);
      if (!realContents.containsKey(key))
         return immediateFailedFuture(new KeyNotFoundException(bucketName, key));
      Blob object = realContents.get(key);
      Blob returnVal = blobProvider.create(copy(object.getMetadata()));
      returnVal.setPayload(object.getContent());
      return immediateFuture(returnVal);
   }

   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            final String name, ListContainerOptions... optionsList) {
      final ListContainerOptions options = (optionsList.length == 0) ? new ListContainerOptions()
               : optionsList[0];

      final Map<String, Blob> realContents = getContainerToBlobs().get(name);

      if (realContents == null)
         return immediateFailedFuture(new ContainerNotFoundException(name));

      SortedSet<StorageMetadata> contents = Sets.newTreeSet(Iterables.transform(realContents
               .keySet(), new Function<String, StorageMetadata>() {
         public StorageMetadata apply(String key) {
            MutableBlobMetadata md = copy(realContents.get(key).getMetadata());
            if (isDirectoryStrategy.execute(md))
               md.setType(StorageType.RELATIVE_PATH);
            return md;
         }
      }));

      if (options.getMarker() != null) {
         final String finalMarker = options.getMarker();
         StorageMetadata lastMarkerMetadata = Iterables.find(contents,
                  new Predicate<StorageMetadata>() {
                     public boolean apply(StorageMetadata metadata) {
                        return metadata.getName().equals(finalMarker);
                     }
                  });
         contents = contents.tailSet(lastMarkerMetadata);
         contents.remove(lastMarkerMetadata);
      }

      final String prefix = options.getDir();
      if (prefix != null) {
         contents = Sets.newTreeSet(Iterables.filter(contents, new Predicate<StorageMetadata>() {
            public boolean apply(StorageMetadata o) {
               return (o != null && o.getName().startsWith(prefix));
            }
         }));
      }

      int maxResults = contents.size();
      boolean truncated = false;
      String marker = null;
      if (options.getMaxResults() != null && contents.size() > 0) {
         SortedSet<StorageMetadata> contentsSlice = firstSliceOfSize(contents, options
                  .getMaxResults().intValue());
         maxResults = options.getMaxResults();
         if (!contentsSlice.contains(contents.last())) {
            // Partial listing
            truncated = true;
            marker = contentsSlice.last().getName();
         } else {
            marker = null;
         }
         contents = contentsSlice;
      }

      final String delimiter = options.isRecursive() ? null : "/";
      if (delimiter != null) {
         SortedSet<String> commonPrefixes = null;
         Iterable<String> iterable = Iterables.transform(contents, new CommonPrefixes(
                  prefix != null ? prefix : null, delimiter));
         commonPrefixes = iterable != null ? Sets.newTreeSet(iterable) : new TreeSet<String>();
         commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

         contents = Sets.newTreeSet(Iterables.filter(contents, new DelimiterFilter(
                  prefix != null ? prefix : null, delimiter)));

         Iterables.<StorageMetadata> addAll(contents, Iterables.transform(commonPrefixes,
                  new Function<String, StorageMetadata>() {
                     public StorageMetadata apply(String o) {
                        MutableStorageMetadata md = new MutableStorageMetadataImpl();
                        md.setType(StorageType.RELATIVE_PATH);
                        md.setName(o);
                        return md;
                     }
                  }));
      }
      return immediateFuture(new ListContainerResponseImpl<StorageMetadata>(contents, prefix,
               marker, maxResults, truncated));

   }

   public MutableBlobMetadata copy(MutableBlobMetadata in) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutput os;
      try {
         os = new ObjectOutputStream(bout);
         os.writeObject(in);
         ObjectInput is = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
         MutableBlobMetadata metadata = (MutableBlobMetadata) is.readObject();
         convertUserMetadataKeysToLowercase(metadata);
         return metadata;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void convertUserMetadataKeysToLowercase(MutableBlobMetadata metadata) {
      Map<String, String> lowerCaseUserMetadata = Maps.newHashMap();
      for (Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
         lowerCaseUserMetadata.put(entry.getKey().toLowerCase(), entry.getValue());
      }
      metadata.setUserMetadata(lowerCaseUserMetadata);
   }

   public MutableBlobMetadata copy(MutableBlobMetadata in, String newKey) {
      MutableBlobMetadata newMd = copy(in);
      newMd.setName(newKey);
      return newMd;
   }

   // public BlobMetadata metadata(final String container, final String key) {
   // if (!getContainerToBlobs().containsKey(container))
   // return immediateFailedFuture(new ContainerNotFoundException(container));
   // Map<String, Blob> realContents = getContainerToBlobs().get(container);
   // if (!realContents.containsKey(key))
   // return immediateFailedFuture(new KeyNotFoundException(container, key));
   // return copy(realContents.get(key).getMetadata());
   // }

   public ListenableFuture<Void> removeBlob(final String container, final String key) {
      if (getContainerToBlobs().containsKey(container)) {
         getContainerToBlobs().get(container).remove(key);
      }
      return immediateFuture(null);
   }

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

   public ListenableFuture<Boolean> containerExists(final String container) {
      return immediateFuture(getContainerToBlobs().containsKey(container));
   }

   public ListenableFuture<? extends ListResponse<? extends StorageMetadata>> list() {
      return immediateFuture(new ListResponseImpl<StorageMetadata>(Iterables.transform(
               getContainerToBlobs().keySet(), new Function<String, StorageMetadata>() {
                  public StorageMetadata apply(String name) {
                     MutableStorageMetadata cmd = create();
                     cmd.setName(name);
                     cmd.setType(StorageType.CONTAINER);
                     return cmd;
                  }
               }), null, null, false));

   }

   protected MutableStorageMetadata create() {
      return new MutableStorageMetadataImpl();
   }

   public ListenableFuture<Boolean> createContainerInLocation(final String location,
            final String name) {
      if (!getContainerToBlobs().containsKey(name)) {
         getContainerToBlobs().put(name, new ConcurrentHashMap<String, Blob>());
      }
      return immediateFuture(getContainerToBlobs().containsKey(name));
   }

   public String getFirstQueryOrNull(String string, @Nullable HttpRequestOptions options) {
      if (options == null)
         return null;
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   protected class DelimiterFilter implements Predicate<StorageMetadata> {
      private final String prefix;
      private final String delimiter;

      public DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(StorageMetadata metadata) {
         if (prefix == null)
            return metadata.getName().indexOf(delimiter) == -1;
         if (metadata.getName().startsWith(prefix))
            return metadata.getName().replaceFirst(prefix, "").indexOf(delimiter) == -1;
         return false;
      }
   }

   protected class CommonPrefixes implements Function<StorageMetadata, String> {
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
            if (working.startsWith(prefix)) {
               working = working.replaceFirst(prefix, "");
            }
         }
         if (working.contains(delimiter)) {
            return working.substring(0, working.indexOf(delimiter));
         }
         return NO_PREFIX;
      }
   }

   public static <T extends Comparable<?>> SortedSet<T> firstSliceOfSize(Iterable<T> elements,
            int size) {
      List<List<T>> slices = Lists.partition(Lists.newArrayList(elements), size);
      return Sets.newTreeSet(slices.get(0));
   }

   public HttpResponseException returnResponseException(int code) {
      HttpResponse response = null;
      response = new HttpResponse(); // TODO: Get real object URL?
      response.setStatusCode(code);
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

         public void redirect(String host, int port) {
         }

         public void redirectAsGet() {
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

         @Override
         public void redirectPath(String newPath) {
         }
      }, response);
   }

   public ListenableFuture<String> putBlob(final String bucketName, final Blob object) {
      Map<String, Blob> container = getContainerToBlobs().get(bucketName);
      if (container == null) {
         new RuntimeException("bucketName not found: " + bucketName);
      }
      try {
         byte[] data = toByteArray(object.getPayload().getRawContent());
         object.getMetadata().setSize(data.length);
         MutableBlobMetadata newMd = copy(object.getMetadata());
         newMd.setLastModified(new Date());
         final byte[] md5 = encryptionService.md5(data);
         final String eTag = encryptionService.toHexString(md5);
         newMd.setETag(eTag);
         newMd.setContentMD5(md5);
         newMd.setContentType(object.getMetadata().getContentType());

         Blob blob = blobProvider.create(newMd);
         blob.setPayload(data);
         container.put(blob.getMetadata().getName(), blob);

         // Set HTTP headers to match metadata
         blob.getAllHeaders().put(HttpHeaders.LAST_MODIFIED,
                  dateService.rfc822DateFormat(newMd.getLastModified()));
         blob.getAllHeaders().put(HttpHeaders.ETAG, eTag);
         blob.getAllHeaders().put(HttpHeaders.CONTENT_TYPE, newMd.getContentType());
         blob.getAllHeaders().put(HttpHeaders.CONTENT_LENGTH, newMd.getSize() + "");
         for (Entry<String, String> userMD : newMd.getUserMetadata().entrySet()) {
            blob.getAllHeaders().put(userMD.getKey(), userMD.getValue());
         }
         return immediateFuture(eTag);
      } catch (IOException e) {
         return immediateFailedFuture(new RuntimeException(e));
      }

   }

   public ListenableFuture<? extends Blob> getBlob(final String bucketName, final String key,
            GetOptions... optionsList) {
      final GetOptions options = (optionsList.length == 0) ? new GetOptions() : optionsList[0];
      if (!getContainerToBlobs().containsKey(bucketName))
         return immediateFailedFuture(new ContainerNotFoundException(bucketName));
      Map<String, Blob> realContents = getContainerToBlobs().get(bucketName);
      if (!realContents.containsKey(key))
         return immediateFailedFuture(new KeyNotFoundException(bucketName, key));

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
            HttpResponse response = new HttpResponse();
            response.setStatusCode(304);
            return immediateFailedFuture(new HttpResponseException(String.format(
                     "%1$s is before %2$s", object.getMetadata().getLastModified(), modifiedSince),
                     null, response));
         }

      }
      if (options.getIfUnmodifiedSince() != null) {
         Date unmodifiedSince = options.getIfUnmodifiedSince();
         if (object.getMetadata().getLastModified().after(unmodifiedSince)) {
            HttpResponse response = new HttpResponse();
            response.setStatusCode(412);
            return immediateFailedFuture(new HttpResponseException(
                     String.format("%1$s is after %2$s", object.getMetadata().getLastModified(),
                              unmodifiedSince), null, response));
         }
      }
      Blob returnVal = copyBlob(object);

      if (options.getRanges() != null && options.getRanges().size() > 0) {
         byte[] data;
         try {
            data = ByteStreams.toByteArray(returnVal.getPayload().getContent());
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
               return immediateFailedFuture(new IllegalArgumentException(
                        "first and last were null!"));
            }

         }
         returnVal.setPayload(out.toByteArray());
         returnVal.setContentLength(out.size());
         returnVal.getMetadata().setSize(new Long(data.length));
      }
      returnVal.setPayload(returnVal.getPayload());
      return immediateFuture(returnVal);
   }

   public ListenableFuture<BlobMetadata> blobMetadata(final String container, final String key) {
      try {
         return immediateFuture((BlobMetadata) copy(getBlob(container, key).get().getMetadata()));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, ContainerNotFoundException.class);
         Throwables.propagateIfPossible(e, KeyNotFoundException.class);
         return immediateFailedFuture(e);
      }
   }

   private Blob copyBlob(Blob object) {
      Blob returnVal = blobProvider.create(copy(object.getMetadata()));
      returnVal.setPayload(object.getPayload());
      return returnVal;
   }

   public ConcurrentMap<String, ConcurrentMap<String, Blob>> getContainerToBlobs() {
      return containerToBlobs;
   }

   public ListenableFuture<Void> clearContainer(final String container) {
      getContainerToBlobs().get(container).clear();
      return immediateFuture(null);
   }

   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            mkdirStrategy.execute(StubAsyncBlobStore.this, container, directory);
            return null;
         }

      }));
   }

   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            try {
               return getDirectoryStrategy.execute(StubAsyncBlobStore.this, container, directory) != null;
            } catch (KeyNotFoundException e) {
               return false;
            }
         }

      }));
   }

   public Blob newBlob(String name) {
      Blob blob = blobProvider.create(null);
      blob.getMetadata().setName(name);
      return blob;
   }

}
