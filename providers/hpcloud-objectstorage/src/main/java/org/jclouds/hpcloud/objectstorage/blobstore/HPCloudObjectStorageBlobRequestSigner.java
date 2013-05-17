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
package org.jclouds.hpcloud.objectstorage.blobstore;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.IOException;
import java.security.InvalidKeyException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageAsyncApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.reflect.Invocation;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteProcessor;
import com.google.common.reflect.Invokable;
import com.google.inject.Provider;

/**
 * Signer for HP's variant of temporary signed URLs. They prefix the signature with the tenant id.
 * 
 * @author Andrew Gaul
 */
@Singleton
public class HPCloudObjectStorageBlobRequestSigner implements BlobRequestSigner {

   private final Function<Invocation, HttpRequest> processor;
   private final Crypto crypto;

   private final Provider<Long> unixEpochTimestampProvider;
   private final Supplier<Access> access;
   private final Supplier<Credentials> creds;

   private final BlobToObject blobToObject;
   private final BlobToHttpGetOptions blob2HttpGetOptions;

   private final Invokable<?, ?> getMethod;
   private final Invokable<?, ?> deleteMethod;
   private final Invokable<?, ?> createMethod;

   @Inject
   public HPCloudObjectStorageBlobRequestSigner(Function<Invocation, HttpRequest> processor,
         BlobToObject blobToObject, BlobToHttpGetOptions blob2HttpGetOptions, Crypto crypto,
         @TimeStamp Provider<Long> unixEpochTimestampProvider, Supplier<Access> access,
         @org.jclouds.location.Provider final Supplier<Credentials> creds) throws SecurityException,
         NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.crypto = checkNotNull(crypto, "crypto");

      this.unixEpochTimestampProvider = checkNotNull(unixEpochTimestampProvider, "unixEpochTimestampProvider");
      this.access = checkNotNull(access, "access");
      this.creds = checkNotNull(creds, "creds");

      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");

      this.getMethod = method(HPCloudObjectStorageAsyncApi.class, "getObject", String.class, String.class, GetOptions[].class);
      this.deleteMethod = method(HPCloudObjectStorageAsyncApi.class, "removeObject", String.class, String.class);
      this.createMethod = method(HPCloudObjectStorageAsyncApi.class, "putObject", String.class, SwiftObject.class);
   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.apply(Invocation.create(getMethod, ImmutableList.<Object> of(container, name))));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      HttpRequest request = processor.apply(Invocation.create(getMethod, ImmutableList.<Object> of(container, name)));
      return cleanRequest(signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.apply(Invocation.create(getMethod,
            ImmutableList.of(container, name, blob2HttpGetOptions.apply(checkNotNull(options, "options"))))));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      return cleanRequest(processor.apply(Invocation.create(createMethod,
            ImmutableList.<Object> of(container, blobToObject.apply(blob)))));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      HttpRequest request = processor.apply(Invocation.create(createMethod,
            ImmutableList.<Object> of(container, blobToObject.apply(blob))));
      return cleanRequest(signForTemporaryAccess(request, timeInSeconds));
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.apply(Invocation.create(deleteMethod, ImmutableList.<Object> of(container, name))));
   }

   private HttpRequest signForTemporaryAccess(HttpRequest request, long timeInSeconds) {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      // accessKey is of the form tenantName:accessKeyId (not tenantId)
      String accessKeyId = currentCreds.identity.substring(currentCreds.identity.indexOf(':') + 1);
      String secretKey = currentCreds.credential;
      String tenantId = access.get().getToken().getTenant().get().getId();
      
      HttpRequest.Builder<?> builder = request.toBuilder();
      // HP Cloud does not use X-Auth-Token for temporary signed URLs and
      // leaking this allows clients arbitrary privileges until token timeout.
      builder.filters(filter(request.getFilters(), not(instanceOf(AuthenticateRequest.class))));

      long expiresInSeconds = unixEpochTimestampProvider.get() + timeInSeconds;
      String signature = createSignature(secretKey,
            createStringToSign(request.getMethod().toUpperCase(), request, expiresInSeconds));

      builder.addQueryParam("temp_url_sig", String.format("%s:%s:%s", tenantId, accessKeyId, signature));
      builder.addQueryParam("temp_url_expires", "" + expiresInSeconds);

      return builder.build();
   }

   private String createStringToSign(String method, HttpRequest request, long expiresInSeconds) {
      checkArgument(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("PUT"));
      return String.format("%s\n%d\n%s", method.toUpperCase(), expiresInSeconds, request.getEndpoint().getPath());
   }

   private String createSignature(String key, String toSign) {
      try {
         ByteProcessor<byte[]> hmacSHA1 = asByteProcessor(crypto.hmacSHA1(key.getBytes(UTF_8)));
         return base16().lowerCase().encode(readBytes(toInputStream(toSign), hmacSHA1));
      } catch (InvalidKeyException e) {
         throw Throwables.propagate(e);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
