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
package org.jclouds.hpcloud.objectstorage.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import java.lang.reflect.Method;
import java.security.InvalidKeyException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.inject.Provider;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageAsyncApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.internal.RestAnnotationProcessor;

/**
 * Signer for HP's variant of temporary signed URLs.  They prefix the signature
 * with the tenant id.
 *
 * @author Andrew Gaul
 */
@Singleton
public class HPCloudObjectStorageBlobRequestSigner implements BlobRequestSigner {

   private final RestAnnotationProcessor<HPCloudObjectStorageAsyncApi> processor;
   private final Crypto crypto;

   private final Provider<Long> unixEpochTimestampProvider;
   private final Supplier<Access> access;
   private String tenantId;
   private final String accessKeyId;
   private final String secretKey;

   private final BlobToObject blobToObject;
   private final BlobToHttpGetOptions blob2HttpGetOptions;

   private final Method getMethod;
   private final Method deleteMethod;
   private final Method createMethod;

   @Inject
   public HPCloudObjectStorageBlobRequestSigner(RestAnnotationProcessor<HPCloudObjectStorageAsyncApi> processor, BlobToObject blobToObject,
            BlobToHttpGetOptions blob2HttpGetOptions,
            Crypto crypto, @TimeStamp Provider<Long> unixEpochTimestampProvider,
            Supplier<Access> access,
            @Identity String accessKey, @Credential String secretKey)
            throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.crypto = checkNotNull(crypto, "crypto");

      this.unixEpochTimestampProvider = checkNotNull(unixEpochTimestampProvider, "unixEpochTimestampProvider");
      this.access = checkNotNull(access, "access");
      // accessKey is of the form tenantName:accessKeyId (not tenantId)
      this.accessKeyId = accessKey.substring(accessKey.indexOf(':') + 1);
      this.secretKey = secretKey;

      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");

      this.getMethod = HPCloudObjectStorageAsyncApi.class.getMethod("getObject", String.class, String.class,
               GetOptions[].class);
      this.deleteMethod = HPCloudObjectStorageAsyncApi.class.getMethod("removeObject", String.class, String.class);
      this.createMethod = HPCloudObjectStorageAsyncApi.class.getMethod("putObject", String.class, SwiftObject.class);
   }

   @PostConstruct
   public void populateTenantId() {
      // Defer call from constructor since access.get issues an RPC.
      this.tenantId = access.get().getToken().getTenant().getId();
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
      HttpRequest.Builder builder = request.toBuilder();
      // HP Cloud does not use X-Auth-Token for temporary signed URLs and
      // leaking this allows clients arbitrary privileges until token timeout.
      builder.filters(filter(request.getFilters(), not(instanceOf(AuthenticateRequest.class))));

      long expiresInSeconds = unixEpochTimestampProvider.get() + timeInSeconds;
      String signature = createSignature(secretKey, createStringToSign(
               request.getMethod().toUpperCase(), request, expiresInSeconds));

      builder.addQueryParam("temp_url_sig",
            String.format("%s:%s:%s", tenantId, accessKeyId, signature));
      builder.addQueryParam("temp_url_expires", "" + expiresInSeconds);

      return builder.build();
   }

   private String createStringToSign(String method, HttpRequest request, long expiresInSeconds) {
      checkArgument(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("PUT"));
      return String.format("%s\n%d\n%s", method.toUpperCase(), expiresInSeconds,
          request.getEndpoint().getPath());
   }

   private String createSignature(String key, String stringToSign) {
      try {
         return CryptoStreams.hex(crypto.hmacSHA1(key.getBytes()).doFinal(stringToSign.getBytes()));

      } catch (InvalidKeyException e) {
         throw Throwables.propagate(e);
      }
   }
}
