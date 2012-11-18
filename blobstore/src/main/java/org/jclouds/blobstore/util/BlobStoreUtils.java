/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.blobstore.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.functions.BlobName;
import org.jclouds.functions.ExceptionToValueOrPropagate;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class BlobStoreUtils {
   public static <T> HttpRequest cleanRequest(HttpRequest returnVal) {
      checkNotNull(returnVal, "http request");
      for (HttpRequestFilter filter : returnVal.getFilters())
         returnVal = filter.filter(returnVal);
      return HttpRequest.builder().method(returnVal.getMethod()).endpoint(returnVal.getEndpoint())
               .headers(returnVal.getHeaders()).payload(returnVal.getPayload()).build();
   }

   public static final ExceptionToValueOrPropagate<KeyNotFoundException, ?> keyNotFoundToNullOrPropagate = new ExceptionToValueOrPropagate<KeyNotFoundException, Object>(
         KeyNotFoundException.class, null);

   public static final ExceptionToValueOrPropagate<ContainerNotFoundException, ?> containerNotFoundToNullOrPropagate = new ExceptionToValueOrPropagate<ContainerNotFoundException, Object>(
         ContainerNotFoundException.class, null);

   @SuppressWarnings("unchecked")
   public static <T> T keyNotFoundToNullOrPropagate(Exception e) {
      return (T) keyNotFoundToNullOrPropagate.apply(e);
   }

   @SuppressWarnings("unchecked")
   public static <T> T containerNotFoundToNullOrPropagate(Exception e) {
      return (T) containerNotFoundToNullOrPropagate.apply(e);
   }

   public static Blob newBlob(BlobStore blobStore, StorageMetadata blobMeta) {
      Blob blob = checkNotNull(blobStore, "blobStore").blobBuilder(checkNotNull(blobMeta, "blobMeta").getName())
            .userMetadata(blobMeta.getUserMetadata()).build();
      if (blobMeta instanceof BlobMetadata) {
         HttpUtils.copy(((BlobMetadata) blobMeta).getContentMetadata(), blob.getMetadata().getContentMetadata());
      }
      blob.getMetadata().setETag(blobMeta.getETag());
      blob.getMetadata().setId(blobMeta.getProviderId());
      blob.getMetadata().setLastModified(blobMeta.getLastModified());
      blob.getMetadata().setLocation(blobMeta.getLocation());
      blob.getMetadata().setUri(blobMeta.getUri());
      return blob;
   }

   public static String parseContainerFromPath(String path) {
      String container = checkNotNull(path, "path");
      if (path.indexOf('/') != -1)
         container = path.substring(0, path.indexOf('/'));
      return container;
   }

   public static String parsePrefixFromPath(String path) {
      String prefix = null;
      if (checkNotNull(path, "path").indexOf('/') != -1)
         prefix = path.substring(path.indexOf('/') + 1);
      return "".equals(prefix) ? null : prefix;
   }

   public static String parseDirectoryFromPath(String path) {
      return checkNotNull(path, "path").substring(0, path.lastIndexOf('/'));
   }

   private static Pattern keyFromContainer = Pattern.compile("/?[^/]+/(.*)");

   public static String getNameFor(GeneratedHttpRequest request) {
      checkNotNull(request, "request");
      // assume first params are container and key
      if (request.getArgs().size() >= 2 && request.getArgs().get(0) instanceof String
            && request.getArgs().get(1) instanceof String) {
         return request.getArgs().get(1).toString();
      } else if (request.getArgs().size() >= 1 && request.getArgs().get(0) instanceof String) {
         Matcher matcher = keyFromContainer.matcher(request.getArgs().get(0).toString());
         if (matcher.find())
            return matcher.group(1);
      }
      String objectKey = request.getEndpoint().getPath();
      if (objectKey.startsWith("/")) {
         // Trim initial slash from object key name.
         objectKey = objectKey.substring(1);
      }
      return objectKey;
   }

   public static String getContentAsStringOrNullAndClose(Blob blob) throws IOException {
      checkNotNull(blob, "blob");
      checkNotNull(blob.getPayload(), "blob.payload");
      if (blob.getPayload().getInput() == null)
         return null;
      Object o = blob.getPayload().getInput();
      if (o instanceof InputStream) {
         return Strings2.toStringAndClose((InputStream) o);
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }

   private static final BlobName blobName = new BlobName();

   public static ListenableFuture<Void> createParentIfNeededAsync(AsyncBlobStore asyncBlobStore, String container,
         Blob blob) {
      checkNotNull(asyncBlobStore, "asyncBlobStore");
      checkNotNull(container, "container");

      String name = blobName.apply(blob);
      if (name.indexOf('/') > 0) {
         return asyncBlobStore.createDirectory(container, parseDirectoryFromPath(name));
      } else {
         return Futures.immediateFuture(null);
      }
   }

   @Deprecated
   public static Iterable<String> getSupportedProviders() {
      return org.jclouds.rest.Providers.getSupportedProvidersOfType(TypeToken.of(BlobStoreContext.class));
   }
   
   public static MutableBlobMetadata copy(MutableBlobMetadata in) {
      MutableBlobMetadata metadata = new MutableBlobMetadataImpl(in);
      convertUserMetadataKeysToLowercase(metadata);
      return metadata;
   }

   public static MutableBlobMetadata copy(MutableBlobMetadata in, String newKey) {
      MutableBlobMetadata newMd = BlobStoreUtils.copy(in);
      newMd.setName(newKey);
      return newMd;
   }

   private static void convertUserMetadataKeysToLowercase(MutableBlobMetadata metadata) {
      Map<String, String> lowerCaseUserMetadata = Maps.newHashMap();
      for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
         lowerCaseUserMetadata.put(entry.getKey().toLowerCase(), entry.getValue());
      }
      metadata.setUserMetadata(lowerCaseUserMetadata);
   }
}
