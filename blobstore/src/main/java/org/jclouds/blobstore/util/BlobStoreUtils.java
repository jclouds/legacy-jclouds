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
package org.jclouds.blobstore.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.functions.BlobName;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.Maps;
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

   public static String parseDirectoryFromPath(String path) {
      return checkNotNull(path, "path").substring(0, path.lastIndexOf('/'));
   }

   private static Pattern keyFromContainer = Pattern.compile("/?[^/]+/(.*)");

   public static String getNameFor(GeneratedHttpRequest request) {
      checkNotNull(request, "request");
      List<Object> args = request.getInvocation().getArgs();
      // assume first params are container and key
      if (args.size() >= 2 && args.get(0) instanceof String
            && args.get(1) instanceof String) {
         return args.get(1).toString();
      } else if (args.size() >= 1 && args.get(0) instanceof String) {
         Matcher matcher = keyFromContainer.matcher(args.get(0).toString());
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
