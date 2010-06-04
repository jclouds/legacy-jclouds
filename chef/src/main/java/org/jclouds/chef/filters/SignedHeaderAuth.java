/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.chef.filters;

import static com.google.common.base.Preconditions.checkArgument;

import java.security.PrivateKey;
import java.util.Collections;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.Payload;
import org.jclouds.http.Payloads;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * Ported from mixlib-authentication in order to sign Chef requests.
 * 
 * @see <a href= "http://github.com/opscode/mixlib-authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class SignedHeaderAuth implements HttpRequestFilter {
   public static final String SIGNING_DESCRIPTION = "version=1.0";

   private final SignatureWire signatureWire;
   private final String userId;
   private final PrivateKey privateKey;
   private final Provider<String> timeStampProvider;
   private final EncryptionService encryptionService;
   private final String emptyStringHash;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SignedHeaderAuth(SignatureWire signatureWire,
            @Named(ChefConstants.PROPERTY_CHEF_IDENTITY) String userId, PrivateKey privateKey,
            @TimeStamp Provider<String> timeStampProvider, EncryptionService encryptionService) {
      this.signatureWire = signatureWire;
      this.userId = userId;
      this.privateKey = privateKey;
      this.timeStampProvider = timeStampProvider;
      this.encryptionService = encryptionService;
      this.emptyStringHash = hashBody(Payloads.newStringPayload(""));
   }

   public void filter(HttpRequest request) throws HttpException {

      String contentHash = hashBody(request.getPayload());
      request.getHeaders().replaceValues("X-Ops-Content-Hash",
               Collections.singletonList(contentHash));
      String timestamp = timeStampProvider.get();
      String toSign = createStringToSign(request.getMethod(), hashPath(request.getEndpoint()
               .getPath()), contentHash, timestamp);
      request.getHeaders().replaceValues("X-Ops-Userid", Collections.singletonList(userId));
      request.getHeaders().replaceValues("X-Ops-Sign",
               Collections.singletonList(SIGNING_DESCRIPTION));
      calculateAndReplaceAuthorizationHeaders(request, toSign);
      request.getHeaders().replaceValues("X-Ops-Timestamp", Collections.singletonList(timestamp));
      HttpUtils.logRequest(signatureLog, request, "<<");
   }

   @VisibleForTesting
   void calculateAndReplaceAuthorizationHeaders(HttpRequest request, String toSign)
            throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Utils.toInputStream(signature));
      String[] signatureLines = Iterables.toArray(Splitter.fixedLength(60).split(signature),
               String.class);
      for (int i = 0; i < signatureLines.length; i++) {
         request.getHeaders().replaceValues("X-Ops-Authorization-" + (i + 1),
                  Collections.singletonList(signatureLines[i]));
      }
   }

   public String createStringToSign(String httpMethod, String hashedPath, String contentHash,
            String timestamp) {

      return new StringBuilder().append("Method:").append(httpMethod).append("\n").append(
               "Hashed Path:").append(hashedPath).append("\n").append("X-Ops-Content-Hash:")
               .append(contentHash).append("\n").append("X-Ops-Timestamp:").append(timestamp)
               .append("\n").append("X-Ops-UserId:").append(userId).toString();

   }

   @VisibleForTesting
   String hashPath(String path) {
      try {
         return encryptionService.sha1Base64(canonicalPath(path));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e);
         throw new HttpException("error creating sigature for path: " + path, e);
      }
   }

   /**
    * Build the canonicalized path, which collapses multiple slashes (/) and removes a trailing
    * slash unless the path is only "/"
    */
   @VisibleForTesting
   String canonicalPath(String path) {
      path = path.replaceAll("\\/+", "/");
      return path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
   }

   @VisibleForTesting
   String hashBody(Payload payload) {
      if (payload == null)
         return emptyStringHash;
      checkArgument(payload != null, "payload was null");
      checkArgument(payload.isRepeatable(), "payload must be repeatable");
      try {
         return encryptionService.sha1Base64(Utils.toStringAndClose(payload.getContent()));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e);
         throw new HttpException("error creating sigature for payload: " + payload, e);
      }
   }

   public String sign(String toSign) {
      try {
         byte[] encrypted = encryptionService.rsaPrivateEncrypt(toSign, privateKey);
         return encryptionService.toBase64String(encrypted);
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
   }

}