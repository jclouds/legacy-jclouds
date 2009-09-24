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
package org.jclouds.azure.storage.filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.reference.AzureStorageConstants;
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
 * Signs the Azure Storage request. This will update timestamps at most once per second.
 * 
 * @see <a href= "http://msdn.microsoft.com/en-us/library/dd179428.aspx" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class SharedKeyAuthentication implements HttpRequestFilter {
   private final String[] firstHeadersToSign = new String[] { "Content-MD5",
            HttpHeaders.CONTENT_TYPE, HttpHeaders.DATE };

   private final String account;
   private byte[] key;
   private final DateService dateService;

   public final long BILLION = 1000000000;
   private final AtomicReference<String> timeStamp;
   private final AtomicLong trigger = new AtomicLong(System.nanoTime() + 1 * BILLION);

   /**
    * Start the time update service. Azure clocks need to be within 900 seconds of the request time.
    * This method updates the clock every second. This is not performed per-request, as creation of
    * the date object is a slow, synchronized command.
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
   public SharedKeyAuthentication(
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY) String encodedKey,
            DateService dateService) {
      this.account = account;
      this.key = HttpUtils.fromBase64String(encodedKey);
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
      appendCanonicalizedHeaders(request, buffer);
      appendCanonicalizedResource(request, buffer);
      return buffer.toString();
   }

   private void calculateAndReplaceAuthHeader(HttpRequest request, String toSign)
            throws HttpException {
      String signature = signString(toSign);
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList("SharedKey " + account + ":" + signature));
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = HttpUtils.hmacSha256Base64(toSign, key);
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

   private void appendCanonicalizedHeaders(HttpRequest request, StringBuilder toSign) {
      Set<String> headers = new TreeSet<String>(request.getHeaders().keySet());
      for (String header : headers) {
         if (header.startsWith("x-ms-")) {
            toSign.append(header.toLowerCase()).append(":");
            for (String value : request.getHeaders().get(header))
               toSign.append(value.replaceAll("\r?\n", "")).append(",");
            toSign.deleteCharAt(toSign.lastIndexOf(","));
            toSign.append("\n");
         }
//      }
//      // Retrieve all headers for the resource that begin with x-ms-, including the x-ms-date
//      // header.
//      Set<String> matchingHeaders = Sets.filter(request.getHeaders().keySet(),
//               new Predicate<String>() {
//                  public boolean apply(String input) {
//                     return input.startsWith("x-ms-");
//                  }
//               });
//
//      // Convert each HTTP header name to lowercase.
//      // Sort the container of headers lexicographically by header name, in ascending order.
//      SortedSet<String> lowercaseHeaders = Sets.newTreeSet(Iterables.transform(matchingHeaders,
//               new Function<String, String>() {
//                  public String apply(String from) {
//                     return from.toLowerCase();
//                  }
//               }));
//
//      for (String header : lowercaseHeaders) {
//         // Combine headers with the same name into one header. The resulting header should be a
//         // name-value pair of the format "header-name:comma-separated-value-list", without any
//         // white
//         // space between values.
//         toSign.append(header).append(":");
//         // Trim any white space around the colon in the header.
//         // TODO: not sure why there would be...
//         for (String value : request.getHeaders().get(header))
//            // Replace any breaking white space with a single space.
//            toSign.append(value.replaceAll("\r?\n", " ")).append(",");
//         toSign.deleteCharAt(toSign.lastIndexOf(","));
//         // Finally, append a new line character to each canonicalized header in the resulting list.
//         // Construct the CanonicalizedHeaders string by concatenating all headers in this list into
//         // a
//         // single string.
//         toSign.append("\n");
      }
   }

   private void appendHttpHeaders(HttpRequest request, StringBuilder toSign) {
      for (String header : firstHeadersToSign)
         toSign.append(valueOrEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendCanonicalizedResource(HttpRequest request, StringBuilder toSign) {

      // 1. Beginning with an empty string (""), append a forward slash (/), followed by the name of
      // the account that owns the resource being accessed.
      toSign.append("/").append(account);
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

   private String valueOrEmpty(Collection<String> collection) {
      return (collection != null && collection.size() >= 1) ? collection.iterator().next() : "";
   }
}