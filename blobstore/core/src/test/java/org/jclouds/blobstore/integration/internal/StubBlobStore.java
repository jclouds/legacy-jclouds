/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.util.DateService;
import org.jclouds.util.Utils;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * Implementation of {@link S3BlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class StubBlobStore<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements BlobStore<C, M, B> {

   protected final DateService dateService;
   private final Map<String, Map<String, B>> containerToBlobs;
   protected final Provider<C> containerMetaProvider;
   protected final Provider<B> blobProvider;

   @Inject
   protected StubBlobStore(Map<String, Map<String, B>> containerToBlobs, DateService dateService,
            Provider<C> containerMetaProvider, Provider<B> blobProvider) {
      this.dateService = dateService;
      this.containerToBlobs = containerToBlobs;
      this.containerMetaProvider = containerMetaProvider;
      this.blobProvider = blobProvider;
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
         bytes = IOUtils.toByteArray(io);
         IOUtils.closeQuietly(io);
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return bytes;

   }

   public Future<B> getBlob(final String bucketName, final String key) {
      return new FutureBase<B>() {
         public B get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(bucketName))
               throw new ContainerNotFoundException(bucketName);
            Map<String, B> realContents = getContainerToBlobs().get(bucketName);
            if (!realContents.containsKey(key))
               throw new KeyNotFoundException(bucketName, key);

            B object = realContents.get(key);
            B returnVal = blobProvider.get();
            returnVal.setMetadata(copy(object.getMetadata()));
            returnVal.setData(new ByteArrayInputStream((byte[]) object.getData()));
            return returnVal;
         }
      };
   }

   public Future<? extends SortedSet<M>> listBlobs(final String name) {
      return new FutureBase<SortedSet<M>>() {
         public SortedSet<M> get() throws InterruptedException, ExecutionException {
            final Map<String, B> realContents = getContainerToBlobs().get(name);

            if (realContents == null)
               throw new ContainerNotFoundException(name);
            SortedSet<M> contents = Sets.newTreeSet(Iterables.transform(realContents.keySet(),
                     new Function<String, M>() {
                        public M apply(String key) {
                           return realContents.get(key).getMetadata();
                        }
                     }));

            return Sets.newTreeSet(contents);
         }
      };
   }

   @SuppressWarnings("unchecked")
   public M copy(M in) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutput os;
      try {
         os = new ObjectOutputStream(bout);
         os.writeObject(in);
         ObjectInput is = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
         M metadata = (M) is.readObject();
         convertUserMetadataKeysToLowercase(metadata);
         return metadata;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void convertUserMetadataKeysToLowercase(M metadata) {
      Multimap<String, String> lowerCaseUserMetadata = HashMultimap.create();
      for (Entry<String, String> entry : metadata.getUserMetadata().entries()) {
         lowerCaseUserMetadata.put(entry.getKey().toLowerCase(), entry.getValue());
      }
      metadata.setUserMetadata(lowerCaseUserMetadata);
   }

   public M copy(M in, String newKey) {
      M newMd = copy(in);
      newMd.setKey(newKey);
      return newMd;
   }

   public M metadata(final String container, final String key) {
      if (!getContainerToBlobs().containsKey(container))
         throw new ContainerNotFoundException(container);
      Map<String, B> realContents = getContainerToBlobs().get(container);
      if (!realContents.containsKey(key))
         throw new KeyNotFoundException(container, key);
      return realContents.get(key).getMetadata();
   }

   public Future<Void> removeBlob(final String container, final String key) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               getContainerToBlobs().get(container).remove(key);
            }
            return null;
         }
      };
   }

   public Future<Void> deleteContainer(final String container) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               getContainerToBlobs().remove(container);
            }
            return null;
         }
      };
   }

   public Future<Boolean> deleteContainerImpl(final String container) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               if (getContainerToBlobs().get(container).size() == 0)
                  getContainerToBlobs().remove(container);
               else
                  return false;
            }
            return true;
         }
      };
   }

   public boolean containerExists(final String container) {
      return getContainerToBlobs().containsKey(container);
   }

   public static abstract class FutureBase<V> implements Future<V> {
      public boolean cancel(boolean b) {
         return false;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean isDone() {
         return true;
      }

      public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
               TimeoutException {
         return get();
      }
   }

   public SortedSet<C> listContainers() {
      return Sets.newTreeSet(Iterables.transform(getContainerToBlobs().keySet(),
               new Function<String, C>() {
                  public C apply(String name) {
                     C cmd = containerMetaProvider.get();
                     cmd.setName(name);
                     return cmd;
                  }

               }));
   }

   public Future<Boolean> createContainer(final String name) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(name)) {
               getContainerToBlobs().put(name, new ConcurrentHashMap<String, B>());
            }
            return getContainerToBlobs().containsKey(name);
         }
      };
   }

   public String getFirstQueryOrNull(String string, @Nullable HttpRequestOptions options) {
      if (options == null)
         return null;
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   protected class DelimiterFilter implements Predicate<M> {
      private final String prefix;
      private final String delimiter;

      public DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(M metadata) {
         if (prefix == null)
            return metadata.getKey().indexOf(delimiter) == -1;
         if (metadata.getKey().startsWith(prefix))
            return metadata.getKey().replaceFirst(prefix, "").indexOf(delimiter) == -1;
         return false;
      }
   }

   protected class CommonPrefixes implements Function<M, String> {
      private final String prefix;
      private final String delimiter;
      public static final String NO_PREFIX = "NO_PREFIX";

      public CommonPrefixes(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public String apply(M metadata) {
         String working = metadata.getKey();

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

   protected void throwResponseException(int code) throws ExecutionException {
      HttpResponse response = null;
      response = new HttpResponse(); // TODO: Get real object URL?
      response.setStatusCode(code);
      throw new ExecutionException(new HttpResponseException(new HttpCommand() {

         public int getRedirectCount() {
            return 0;
         }

         public int incrementRedirectCount() {
            return 0;
         }

         public boolean isReplayable() {
            return false;
         }

         public HttpRequest setHostAndPort(String host, int port) {
            return null;
         }

         public HttpRequest setMethod(String method) {
            return null;
         }

         public Exception getException() {
            return null;
         }

         public int getFailureCount() {
            return 0;
         }

         public HttpRequest getRequest() {
            return null;
         }

         public int incrementFailureCount() {
            return 0;
         }

         public void setException(Exception exception) {

         }
      }, response));
   }

   public Future<byte[]> putBlob(final String bucketName, final B object) {
      Map<String, B> container = getContainerToBlobs().get(bucketName);
      if (container == null) {
         new RuntimeException("bucketName not found: " + bucketName);
      }
      try {
         M newMd = copy(object.getMetadata());
         newMd.setLastModified(new DateTime());
         byte[] data = toByteArray(object.getData());
         final byte[] eTag = HttpUtils.md5(data);
         newMd.setETag(eTag);
         newMd.setContentMD5(eTag);
         newMd.setContentType(object.getMetadata().getContentType());

         B blob = blobProvider.get();
         blob.setMetadata(newMd);
         blob.setData(data);
         container.put(object.getKey(), blob);

         // Set HTTP headers to match metadata
         newMd.getAllHeaders().put(HttpHeaders.LAST_MODIFIED,
                  dateService.rfc822DateFormat(newMd.getLastModified()));
         newMd.getAllHeaders().put(HttpHeaders.ETAG, HttpUtils.toHexString(eTag));
         newMd.getAllHeaders().put(HttpHeaders.CONTENT_TYPE, newMd.getContentType());
         newMd.getAllHeaders().put(HttpHeaders.CONTENT_LENGTH, newMd.getSize() + "");
         for (Entry<String, String> userMD : newMd.getUserMetadata().entries()) {
            newMd.getAllHeaders().put(userMD.getKey(), userMD.getValue());
         }

         return new FutureBase<byte[]>() {
            public byte[] get() throws InterruptedException, ExecutionException {
               return eTag;
            }
         };
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   public Future<B> getBlob(final String bucketName, final String key,
            @Nullable GetOptions nullableOptions) {
      final GetOptions options = (nullableOptions == null) ? new GetOptions() : nullableOptions;
      return new FutureBase<B>() {
         public B get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(bucketName))
               throw new ContainerNotFoundException(bucketName);
            Map<String, B> realContents = getContainerToBlobs().get(bucketName);
            if (!realContents.containsKey(key))
               throw new KeyNotFoundException(bucketName, key);

            B object = realContents.get(key);

            if (options.getIfMatch() != null) {
               if (!Arrays.equals(object.getMetadata().getETag(), HttpUtils.fromHexString(options
                        .getIfMatch().replaceAll("\"", ""))))
                  throwResponseException(412);
            }
            if (options.getIfNoneMatch() != null) {
               if (Arrays.equals(object.getMetadata().getETag(), HttpUtils.fromHexString(options
                        .getIfNoneMatch().replaceAll("\"", ""))))
                  throwResponseException(304);
            }
            if (options.getIfModifiedSince() != null) {
               DateTime modifiedSince = dateService.rfc822DateParse(options.getIfModifiedSince());
               if (object.getMetadata().getLastModified().isBefore(modifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is before %2$s", object.getMetadata().getLastModified(),
                           modifiedSince)));

            }
            if (options.getIfUnmodifiedSince() != null) {
               DateTime unmodifiedSince = dateService.rfc822DateParse(options
                        .getIfUnmodifiedSince());
               if (object.getMetadata().getLastModified().isAfter(unmodifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is after %2$s", object.getMetadata().getLastModified(),
                           unmodifiedSince)));
            }
            B returnVal = copyBlob(object);

            if (options.getRange() != null) {
               byte[] data = (byte[]) returnVal.getData();
               ByteArrayOutputStream out = new ByteArrayOutputStream();
               for (String s : options.getRange().replaceAll("bytes=", "").split(",")) {
                  if (s.startsWith("-")) {
                     int length = Integer.parseInt(s.replaceAll("\\-", ""));
                     out.write(data, data.length - length, length);
                  } else if (s.endsWith("-")) {
                     int offset = Integer.parseInt(s.replaceAll("\\-", ""));
                     out.write(data, offset, data.length - offset);
                  } else if (s.contains("-")) {
                     String[] firstLast = s.split("\\-");
                     int offset = Integer.parseInt(firstLast[0]);
                     int last = Integer.parseInt(firstLast[1]);
                     int length = (last < data.length) ? last + 1 : data.length - offset;

                     out.write(data, offset, length);
                  } else {
                     throw new IllegalArgumentException("first and last were null!");
                  }

               }
               returnVal.setData(out.toByteArray());
               returnVal.setContentLength(out.size());
               returnVal.getMetadata().setSize(data.length);
            }
            returnVal.setData(new ByteArrayInputStream((byte[]) returnVal.getData()));
            return returnVal;
         }
      };
   }

   public M blobMetadata(String container, String key) {
      try {
         return getBlob(container, key).get().getMetadata();
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         Utils.<KeyNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new RuntimeException(e);// TODO
      }
   }

   private B copyBlob(B object) {
      B returnVal = blobProvider.get();
      returnVal.setMetadata(copy(object.getMetadata()));
      returnVal.setData(object.getData());
      return returnVal;
   }

   public Map<String, Map<String, B>> getContainerToBlobs() {
      return containerToBlobs;
   }
}
