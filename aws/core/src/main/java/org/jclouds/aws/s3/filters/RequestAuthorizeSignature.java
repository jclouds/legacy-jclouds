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

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.util.RequestSigner;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
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
   private final String[] firstHeadersToSign = new String[] { "Content-MD5",
            HttpHeaders.CONTENT_TYPE, HttpHeaders.DATE };

   public static Set<String> SPECIAL_QUERIES = ImmutableSet.of("acl", "torrent", "logging",
            "location", "requestPayment");
   private final SignatureWire signatureWire;
   private final String accessKey;
   private final String secretKey;
   private final Provider<String> timeStampProvider;
   private final EncryptionService encryptionService;

   @Resource
   @Named(HttpConstants.SIGNATURE_LOGGER)
   Logger signatureLog = Logger.NULL;

   @Inject
   public RequestAuthorizeSignature(SignatureWire signatureWire,
            @Named(S3Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
            @Named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey,
            @TimeStamp Provider<String> timeStampProvider, EncryptionService encryptionService) {
      this.signatureWire = signatureWire;
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.timeStampProvider = timeStampProvider;
      this.encryptionService = encryptionService;
   }

   public void filter(HttpRequest request) throws HttpException {
      replaceDateHeader(request);
      String toSign = createStringToSign(request);
      calculateAndReplaceAuthHeader(request, toSign);
      HttpUtils.logRequest(signatureLog, request, "<<");
   }

   public String createStringToSign(HttpRequest request) {
      HttpUtils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
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
      String signature = signString(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Utils.toInputStream(signature));
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList("AWS " + accessKey + ":" + signature));
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = encryptionService.hmacSha1Base64(toSign, secretKey.getBytes());
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
         if (header.startsWith("x-amz-")) {
            toSign.append(header.toLowerCase()).append(":");
            for (String value : request.getHeaders().get(header))
               toSign.append(value.replaceAll("\r?\n", "")).append(",");
            toSign.deleteCharAt(toSign.lastIndexOf(","));
            toSign.append("\n");
         }
      }
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
      if (hostHeader.endsWith(".amazonaws.com") && !hostHeader.equals("s3.amazonaws.com"))
         toSign.append("/").append(hostHeader.substring(0, hostHeader.lastIndexOf(".s3")));
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {

      toSign.append(request.getEndpoint().getRawPath());

      // ...however, there are a few exceptions that must be included in the signed URI.
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