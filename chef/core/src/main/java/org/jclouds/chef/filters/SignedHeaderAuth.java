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

package org.jclouds.chef.filters;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.io.InputSuppliers;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

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
   private final Crypto crypto;
   private final String emptyStringHash;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SignedHeaderAuth(SignatureWire signatureWire, @Named(PROPERTY_IDENTITY) String userId, PrivateKey privateKey,
            @TimeStamp Provider<String> timeStampProvider, Crypto crypto, HttpUtils utils) {
      this.signatureWire = signatureWire;
      this.userId = userId;
      this.privateKey = privateKey;
      this.timeStampProvider = timeStampProvider;
      this.crypto = crypto;
      this.emptyStringHash = hashBody(Payloads.newStringPayload(""));
      this.utils = utils;
   }

   public void filter(HttpRequest request) throws HttpException {

      String contentHash = hashBody(request.getPayload());
      request.getHeaders().replaceValues("X-Ops-Content-Hash", Collections.singletonList(contentHash));
      String timestamp = timeStampProvider.get();
      String toSign = createStringToSign(request.getMethod(), hashPath(request.getEndpoint().getPath()), contentHash,
               timestamp);
      request.getHeaders().replaceValues("X-Ops-Userid", Collections.singletonList(userId));
      request.getHeaders().replaceValues("X-Ops-Sign", Collections.singletonList(SIGNING_DESCRIPTION));
      calculateAndReplaceAuthorizationHeaders(request, toSign);
      request.getHeaders().replaceValues("X-Ops-Timestamp", Collections.singletonList(timestamp));
      utils.logRequest(signatureLog, request, "<<");
   }

   @VisibleForTesting
   void calculateAndReplaceAuthorizationHeaders(HttpRequest request, String toSign) throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Utils.toInputStream(signature));
      String[] signatureLines = Iterables.toArray(Splitter.fixedLength(60).split(signature), String.class);
      for (int i = 0; i < signatureLines.length; i++) {
         request.getHeaders().replaceValues("X-Ops-Authorization-" + (i + 1),
                  Collections.singletonList(signatureLines[i]));
      }
   }

   public String createStringToSign(String request, String hashedPath, String contentHash, String timestamp) {

      return new StringBuilder().append("Method:").append(request).append("\n").append("Hashed Path:").append(
               hashedPath).append("\n").append("X-Ops-Content-Hash:").append(contentHash).append("\n").append(
               "X-Ops-Timestamp:").append(timestamp).append("\n").append("X-Ops-UserId:").append(userId).toString();

   }

   @VisibleForTesting
   String hashPath(String path) {
      try {
         return CryptoStreams.base64(CryptoStreams.digest(InputSuppliers.of(canonicalPath(path)), crypto.sha1()));
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
      payload = useTheFilePartIfForm(payload);
      checkArgument(payload != null, "payload was null");
      checkArgument(payload.isRepeatable(), "payload must be repeatable: " + payload);
      try {
         return CryptoStreams.base64(CryptoStreams.digest(payload, crypto.sha1()));
      } catch (Exception e) {
         Throwables.propagateIfPossible(e);
         throw new HttpException("error creating sigature for payload: " + payload, e);
      }
   }

   private Payload useTheFilePartIfForm(Payload payload) {
      if (payload instanceof MultipartForm) {
         Iterable<? extends Part> parts = MultipartForm.class.cast(payload).getRawContent();
         try {
            payload = Iterables.find(parts, new Predicate<Part>() {

               @Override
               public boolean apply(Part input) {
                  return "file".equals(input.getName());
               }

            });
         } catch (NoSuchElementException e) {

         }
      }
      return payload;
   }

   public String sign(String toSign) {
      try {
         byte[] encrypted = ByteStreams.toByteArray(new RSAEncryptingPayload(Payloads.newStringPayload(toSign),
                  privateKey));
         return CryptoStreams.base64(encrypted);
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
   }

}