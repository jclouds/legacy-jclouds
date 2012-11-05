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
package org.jclouds.azure.storage.filters;

import static org.jclouds.util.Patterns.NEWLINE_PATTERN;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

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
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Signs the Azure Storage request.
 * 
 * @see <a href= "http://msdn.microsoft.com/en-us/library/dd179428.aspx" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class SharedKeyLiteAuthentication implements HttpRequestFilter {
   private static final Collection<String> FIRST_HEADERS_TO_SIGN = ImmutableList.of(HttpHeaders.DATE);

   private final SignatureWire signatureWire;
   private final String identity;
   private final byte[] key;
   private final Provider<String> timeStampProvider;
   private final Crypto crypto;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyLiteAuthentication(SignatureWire signatureWire, @Identity String identity,
         @Credential String encodedKey, @TimeStamp Provider<String> timeStampProvider,
         Crypto crypto, HttpUtils utils) {
      this.crypto = crypto;
      this.utils = utils;
      this.signatureWire = signatureWire;
      this.identity = identity;
      this.key = CryptoStreams.base64(encodedKey);
      this.timeStampProvider = timeStampProvider;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      request = replaceDateHeader(request);
      String signature = calculateSignature(createStringToSign(request));
      request = replaceAuthorizationHeader(request, signature);
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      return request.toBuilder().replaceHeader(HttpHeaders.AUTHORIZATION, "SharedKeyLite " + identity + ":"
            + signature).build();
   }

   HttpRequest replaceDateHeader(HttpRequest request) {
      Builder<String, String> builder = ImmutableMap.builder();
      String date = timeStampProvider.get();
      builder.put(HttpHeaders.DATE, date);
      request = request.toBuilder().replaceHeaders(Multimaps.forMap(builder.build())).build();
      return request;
   }

   public String createStringToSign(HttpRequest request) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendPayloadMetadata(request, buffer);
      appendHttpHeaders(request, buffer);
      appendCanonicalizedHeaders(request, buffer);
      appendCanonicalizedResource(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   private void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      buffer.append(
            HttpUtils.nullToEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                  .getContentMD5())).append("\n");
      buffer.append(
            Strings.nullToEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                  .getContentType())).append("\n");
   }

   private String calculateSignature(String toSign) throws HttpException {
      String signature = signString(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Strings2.toInputStream(signature));
      return signature;
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = CryptoStreams.base64(CryptoStreams.mac(InputSuppliers.of(toSign), crypto.hmacSHA256(key)));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   private void appendMethod(HttpRequest request, StringBuilder toSign) {
      toSign.append(request.getMethod()).append("\n");
   }

   private void appendCanonicalizedHeaders(HttpRequest request, StringBuilder toSign) {
      // TreeSet == Sort the headers alphabetically.
      Set<String> headers = Sets.newTreeSet(request.getHeaders().keySet());
      for (String header : headers) {
         if (header.startsWith("x-ms-")) {
            toSign.append(header.toLowerCase()).append(":");
            for (String value : request.getHeaders().get(header)) {
               toSign.append(Strings2.replaceAll(value, NEWLINE_PATTERN, "")).append(",");
            }
            toSign.deleteCharAt(toSign.lastIndexOf(","));
            toSign.append("\n");
         }
      }
   }

   private void appendHttpHeaders(HttpRequest request, StringBuilder toSign) {
      for (String header : FIRST_HEADERS_TO_SIGN)
         toSign.append(HttpUtils.nullToEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendCanonicalizedResource(HttpRequest request, StringBuilder toSign) {

      // 1. Beginning with an empty string (""), append a forward slash (/), followed by the name of
      // the identity that owns the resource being accessed.
      toSign.append("/").append(identity);
      appendUriPath(request, toSign);
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {
      // 2. Append the resource's encoded URI path
      toSign.append(request.getEndpoint().getRawPath());

      // If the request URI addresses a component of the
      // resource, append the appropriate query string. The query string should include the question
      // mark and the comp parameter (for example, ?comp=metadata). No other parameters should be
      // included on the query string.
      if (request.getEndpoint().getQuery() != null) {
         StringBuilder paramsToSign = new StringBuilder("?");

         String[] params = request.getEndpoint().getQuery().split("&");
         for (String param : params) {
            String[] paramNameAndValue = param.split("=");

            if ("comp".equals(paramNameAndValue[0])) {
               paramsToSign.append(param);
            }
         }

         if (paramsToSign.length() > 1) {
            toSign.append(paramsToSign);
         }
      }
   }

}
