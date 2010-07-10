/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Patterns.NEWLINE_PATTERN;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

/**
 * Signs the S3 request.
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/latest/RESTAuthentication.html" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RequestAuthorizeSignature implements HttpRequestFilter, RequestSigner {
   private final String[] firstHeadersToSign = new String[] { HttpHeaders.DATE };

   public static Set<String> SPECIAL_QUERIES = ImmutableSet.of("acl", "torrent", "logging",
            "location", "requestPayment");
   private final SignatureWire signatureWire;
   private final String accessKey;
   private final String secretKey;
   private final Provider<String> timeStampProvider;
   private final EncryptionService encryptionService;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   private final String authTag;
   private final String headerTag;
   private final String srvExpr;

   @Inject
   public RequestAuthorizeSignature(SignatureWire signatureWire,
            @Named(S3Constants.PROPERTY_S3_AUTH_TAG) String authTag,
            @Named(S3Constants.PROPERTY_S3_SERVICE_EXPR) String srvExpr,
            @Named(S3Constants.PROPERTY_S3_HEADER_TAG) String headerTag,
            @Named(Constants.PROPERTY_IDENTITY) String accessKey,
            @Named(Constants.PROPERTY_CREDENTIAL) String secretKey,
            @TimeStamp Provider<String> timeStampProvider, EncryptionService encryptionService,
            HttpUtils utils) {
      this.srvExpr = srvExpr;
      this.headerTag = headerTag;
      this.authTag = authTag;
      this.signatureWire = signatureWire;
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.timeStampProvider = timeStampProvider;
      this.encryptionService = encryptionService;
      this.utils = utils;
   }

   public void filter(HttpRequest request) throws HttpException {
      replaceDateHeader(request);
      String toSign = createStringToSign(request);
      calculateAndReplaceAuthHeader(request, toSign);
      utils.logRequest(signatureLog, request, "<<");
   }

   public String createStringToSign(HttpRequest request) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendPayloadMetadata(request, buffer);
      appendHttpHeaders(request, buffer);
      appendAmzHeaders(request, buffer);
      appendBucketName(request, buffer);
      appendUriPath(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   private void calculateAndReplaceAuthHeader(HttpRequest request, String toSign)
            throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Utils.toInputStream(signature));
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList(authTag + " " + accessKey + ":" + signature));
   }

   public String sign(String toSign) {
      String signature;
      try {
         signature = encryptionService.base64(encryptionService.hmacSha1(toSign, secretKey
                  .getBytes()));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   private void appendMethod(HttpRequest request, StringBuilder toSign) {
      toSign.append(request.getMethod()).append("\n");
   }

   private void replaceDateHeader(HttpRequest request) {
      request.getHeaders().replaceValues(HttpHeaders.DATE,
               Collections.singletonList(timeStampProvider.get()));
   }

   private void appendAmzHeaders(HttpRequest request, StringBuilder toSign) {
      Set<String> headers = new TreeSet<String>(request.getHeaders().keySet());
      for (String header : headers) {
         if (header.startsWith("x-" + headerTag + "-")) {
            toSign.append(header.toLowerCase()).append(":");
            for (String value : request.getHeaders().get(header)) {
               toSign.append(Utils.replaceAll(value, NEWLINE_PATTERN, "")).append(",");
            }
            toSign.deleteCharAt(toSign.lastIndexOf(","));
            toSign.append("\n");
         }
      }
   }

   private void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      buffer.append(
               utils.valueOrEmpty(request.getPayload() == null ? null : request.getPayload()
                        .getContentMD5())).append("\n");
      buffer.append(
               utils.valueOrEmpty(request.getPayload() == null ? null : request.getPayload()
                        .getContentType())).append("\n");
   }

   private void appendHttpHeaders(HttpRequest request, StringBuilder toSign) {
      for (String header : firstHeadersToSign)
         toSign.append(valueOrEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendBucketName(HttpRequest request, StringBuilder toSign) {
      String hostHeader = request.getFirstHeaderOrNull(HttpHeaders.HOST);
      if (hostHeader == null)
         hostHeader = checkNotNull(request.getEndpoint().getHost(),
                  "request.getEndPoint().getHost()");
      if (hostHeader.matches(".*" + srvExpr))
         toSign.append("/").append(hostHeader.replaceAll(srvExpr, ""));
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {

      toSign.append(request.getEndpoint().getRawPath());

      // ...however, there are a few exceptions that must be included in the
      // signed URI.
      if (request.getEndpoint().getQuery() != null) {
         StringBuilder paramsToSign = new StringBuilder("?");

         String[] params = request.getEndpoint().getQuery().split("&");
         for (String param : params) {
            String[] paramNameAndValue = param.split("=");

            if (SPECIAL_QUERIES.contains(paramNameAndValue[0])) {
               paramsToSign.append(paramNameAndValue[0]);
            }
         }

         if (paramsToSign.length() > 1) {
            toSign.append(paramsToSign);
         }
      }
   }

   private String valueOrEmpty(Collection<String> collection) {
      return (collection != null && collection.size() >= 1) ? collection.iterator().next() : "";
   }
}