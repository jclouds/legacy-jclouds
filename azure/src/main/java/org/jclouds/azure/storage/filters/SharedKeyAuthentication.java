/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.util.EncryptionService;
import org.jclouds.util.TimeStamp;

import com.google.common.annotations.VisibleForTesting;

/**
 * Signs the Azure Storage request.
 * 
 * @see <a href= "http://msdn.microsoft.com/en-us/library/dd179428.aspx" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class SharedKeyAuthentication implements HttpRequestFilter {
   private final String[] firstHeadersToSign = new String[] { "Content-MD5",
            HttpHeaders.CONTENT_TYPE, HttpHeaders.DATE };

   private final SignatureWire signatureWire;
   private final String account;
   private final byte[] key;
   private final Provider<String> timeStampProvider;
   private final EncryptionService encryptionService;
   @Resource
   @Named(HttpConstants.SIGNATURE_LOGGER)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyAuthentication(SignatureWire signatureWire,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY) String encodedKey,
            @TimeStamp Provider<String> timeStampProvider, EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
      this.signatureWire = signatureWire;
      this.account = account;
      this.key = encryptionService.fromBase64String(encodedKey);
      this.timeStampProvider = timeStampProvider;
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
      appendCanonicalizedHeaders(request, buffer);
      appendCanonicalizedResource(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   private void calculateAndReplaceAuthHeader(HttpRequest request, String toSign)
            throws HttpException {
      String signature = signString(toSign);
      if (signatureWire.enabled())
         signatureWire.input(IOUtils.toInputStream(signature));
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList("SharedKey " + account + ":" + signature));
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = encryptionService.hmacSha256Base64(toSign, key);
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