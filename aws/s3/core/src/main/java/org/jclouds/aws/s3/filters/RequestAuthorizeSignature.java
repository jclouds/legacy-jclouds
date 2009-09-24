/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.inject.Named;

/**
 * Signs the S3 request. This will update timestamps at most once per second.
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/latest/RESTAuthentication.html" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RequestAuthorizeSignature implements HttpRequestFilter {
   private final String[] firstHeadersToSign = new String[] { "Content-MD5",
            HttpHeaders.CONTENT_TYPE, HttpHeaders.DATE };

   private final String accessKey;
   private final String secretKey;
   private final DateService dateService;

   public final long BILLION = 1000000000;
   private final AtomicReference<String> timeStamp;
   private final AtomicLong trigger = new AtomicLong(System.nanoTime() + 1 * BILLION);

   /**
    * Start the time update service. Amazon clocks need to be within 900 seconds of the request
    * time. This method updates the clock every second. This is not performed per-request, as
    * creation of the date object is a slow, synchronized command.
    */
   synchronized void updateIfTimeOut() {

      if (trigger.get() - System.nanoTime() <= 0) {
         timeStamp.set(createNewStamp());
         trigger.set(System.nanoTime() + 1 * BILLION);
      }

   }

   // this is a hotspot when submitted concurrently, so be lazy.
   // amazon is ok with up to 15 minutes off their time, so let's
   // be as lazy as possible.
   String createNewStamp() {
      return dateService.rfc822DateFormat();
   }

   public String timestampAsHeaderString() {
      updateIfTimeOut();
      return timeStamp.get();
   }

   @Inject
   public RequestAuthorizeSignature(@Named(S3Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
            @Named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey,
            DateService dateService) {
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.dateService = dateService;
      timeStamp = new AtomicReference<String>(createNewStamp());
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      replaceDateHeader(request);
      String toSign = createStringToSign(request);
      calculateAndReplaceAuthHeader(request, toSign);
      return request;
   }

   public String createStringToSign(HttpRequest request) {
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendHttpHeaders(request, buffer);
      appendAmzHeaders(request, buffer);
      appendBucketName(request, buffer);
      appendUriPath(request, buffer);
      return buffer.toString();
   }

   private void calculateAndReplaceAuthHeader(HttpRequest request, String toSign)
            throws HttpException {
      String signature = signString(toSign);
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList("AWS " + accessKey + ":" + signature));
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = HttpUtils.hmacSha1Base64(toSign, secretKey.getBytes());
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
               Collections.singletonList(timestampAsHeaderString()));
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

            if ("acl".equals(paramNameAndValue[0])) {
               paramsToSign.append("acl");
            }
            // TODO: Other special cases not yet handled: torrent, logging, location, requestPayment
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