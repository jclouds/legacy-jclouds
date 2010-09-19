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

package org.jclouds.blobstore.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.getSupportedProvidersOfType;
import static org.jclouds.util.Utils.toStringAndClose;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.functions.ExceptionToValueOrPropagate;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
public class BlobStoreUtils {
   public static <T> HttpRequest cleanRequest(GeneratedHttpRequest<T> returnVal) {
      for (HttpRequestFilter filter : returnVal.getFilters())
         filter.filter(returnVal);
      HttpRequest toReturn = new HttpRequest(returnVal.getMethod(), returnVal.getEndpoint(), ImmutableMultimap
               .copyOf(returnVal.getHeaders()));
      if (returnVal.getPayload() != null)
         toReturn.setPayload(returnVal.getPayload());
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   public static final ExceptionToValueOrPropagate keyNotFoundToNullOrPropagate = new ExceptionToValueOrPropagate(
            KeyNotFoundException.class, null);
   @SuppressWarnings("unchecked")
   public static final ExceptionToValueOrPropagate containerNotFoundToNullOrPropagate = new ExceptionToValueOrPropagate(
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
      Blob blob = blobStore.newBlob(blobMeta.getName());
      if (blobMeta instanceof BlobMetadata) {
         HttpUtils.copy(((BlobMetadata) blobMeta).getContentMetadata(), blob.getMetadata().getContentMetadata());
      }
      blob.getMetadata().setETag(blobMeta.getETag());
      blob.getMetadata().setId(blobMeta.getProviderId());
      blob.getMetadata().setLastModified(blobMeta.getLastModified());
      blob.getMetadata().setLocation(blobMeta.getLocation());
      blob.getMetadata().setUri(blobMeta.getUri());
      blob.getMetadata().setUserMetadata(blobMeta.getUserMetadata());
      return blob;
   }

   public static String parseContainerFromPath(String path) {
      String container = path;
      if (path.indexOf('/') != -1)
         container = path.substring(0, path.indexOf('/'));
      return container;
   }

   public static String parsePrefixFromPath(String path) {
      String prefix = null;
      if (path.indexOf('/') != -1)
         prefix = path.substring(path.indexOf('/') + 1);
      return "".equals(prefix) ? null : prefix;
   }

   public static String parseDirectoryFromPath(String path) {
      return path.substring(0, path.lastIndexOf('/'));
   }

   private static Pattern keyFromContainer = Pattern.compile("/?[^/]+/(.*)");

   public static String getKeyFor(GeneratedHttpRequest<?> request, HttpResponse from) {
      checkNotNull(request, "request");
      checkNotNull(from, "from");
      // assume first params are container and key
      if (request.getArgs().length >= 2 && request.getArgs()[0] instanceof String
               && request.getArgs()[1] instanceof String) {
         return request.getArgs()[1].toString();
      } else if (request.getArgs().length >= 1 && request.getArgs()[0] instanceof String) {
         Matcher matcher = keyFromContainer.matcher(request.getArgs()[0].toString());
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
         return toStringAndClose((InputStream) o);
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }

   public static void createParentIfNeededAsync(AsyncBlobStore asyncBlobStore, String container, Blob blob) {
      String name = blob.getMetadata().getName();
      if (name.indexOf('/') > 0) {
         asyncBlobStore.createDirectory(container, parseDirectoryFromPath(name));
      }
   }

   public static Iterable<String> getSupportedProviders() {
      return getSupportedProvidersOfType(BlobStoreContextBuilder.class);
   }
}