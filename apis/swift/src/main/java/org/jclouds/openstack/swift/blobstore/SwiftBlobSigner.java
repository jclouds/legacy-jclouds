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
package org.jclouds.openstack.swift.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import java.lang.reflect.Method;
import java.security.InvalidKeyException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.TemporaryUrlKey;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

/**
 * @author Adrian Cole
 */
@Singleton
public class SwiftBlobSigner<T extends CommonSwiftAsyncClient> implements BlobRequestSigner {

   private final RestAnnotationProcessor<T> processor;
   private final Crypto crypto;

   private final Provider<Long> unixEpochTimestampProvider;
   private final Supplier<String> temporaryUrlKeySupplier;

   private final BlobToObject blobToObject;
   private final BlobToHttpGetOptions blob2HttpGetOptions;

   private final Method getMethod;
   private final Method deleteMethod;
   private final Method createMethod;

   /**
    * create a signer for this subtype of swift
    *
    * @param processor
    *           bound to the current subclass of {@link CommonSwiftAsyncClient}
    */
   @Inject
   protected SwiftBlobSigner(BlobToObject blobToObject, BlobToHttpGetOptions blob2HttpGetOptions, Crypto crypto,
         @TimeStamp Provider<Long> unixEpochTimestampProvider,
         @TemporaryUrlKey Supplier<String> temporaryUrlKeySupplier,
         RestAnnotationProcessor<T> processor)
         throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.crypto = checkNotNull(crypto, "crypto");

      this.unixEpochTimestampProvider = checkNotNull(unixEpochTimestampProvider, "unixEpochTimestampProvider");
      this.temporaryUrlKeySupplier = checkNotNull(temporaryUrlKeySupplier, "temporaryUrlKeyProvider");

      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");

      this.getMethod = processor.getDeclaring().getMethod("getObject", String.class, String.class, GetOptions[].class);
      this.deleteMethod = processor.getDeclaring().getMethod("removeObject", String.class, String.class);
      this.createMethod = processor.getDeclaring().getMethod("putObject", String.class, SwiftObject.class);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return cleanRequest(processor.createRequest(getMethod, container, name));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      HttpRequest request = processor.createRequest(getMethod, container, name);
      return cleanRequest(signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      return cleanRequest(processor.createRequest(getMethod, container, name, blob2HttpGetOptions.apply(options)));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return cleanRequest(processor.createRequest(createMethod, container, blobToObject.apply(blob)));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      HttpRequest request = processor.createRequest(createMethod, container, blobToObject.apply(blob));
      return cleanRequest(signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      return cleanRequest(processor.createRequest(deleteMethod, container, name));
   }

   private HttpRequest signForTemporaryAccess(HttpRequest request, long timeInSeconds) {
      HttpRequest.Builder<?> builder = request.toBuilder().filters(ImmutableSet.<HttpRequestFilter>of());

      String key = temporaryUrlKeySupplier.get();
      if (key == null) {
         throw new UnsupportedOperationException();
      }
      long expiresInSeconds = unixEpochTimestampProvider.get() + timeInSeconds;

      builder.addQueryParam("temp_url_sig",
            createSignature(key, createStringToSign(request.getMethod().toUpperCase(), request, expiresInSeconds)));
      builder.addQueryParam("temp_url_expires", "" + expiresInSeconds);

      return builder.build();
   }

   private String createStringToSign(String method, HttpRequest request, long expiresInSeconds) {
      checkArgument(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("PUT"));
      return String.format("%s\n%d\n%s", method.toUpperCase(), expiresInSeconds, request.getEndpoint().getPath());
   }

   private String createSignature(String key, String stringToSign) {
      try {
         return CryptoStreams.hex(crypto.hmacSHA1(key.getBytes()).doFinal(stringToSign.getBytes()));
      } catch (InvalidKeyException e) {
         throw Throwables.propagate(e);
      }
   }
}
