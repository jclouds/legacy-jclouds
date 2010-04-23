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
package org.jclouds.gogrid.filters;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.gogrid.reference.GoGridConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Oleksiy Yarmula
 */
public class SharedKeyLiteAuthentication implements HttpRequestFilter {

   private final String apiKey;
   private final String secret;
   private final Long timeStamp;
   private final EncryptionService encryptionService;
   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyLiteAuthentication(@Named(GoGridConstants.PROPERTY_GOGRID_USER) String apiKey,
            @Named(GoGridConstants.PROPERTY_GOGRID_PASSWORD) String secret,
            @TimeStamp Long timeStamp, EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
      this.apiKey = apiKey;
      this.secret = secret;
      this.timeStamp = timeStamp;
   }

   public void filter(HttpRequest request) {
      checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest<?>,
               "this decorator is only valid for GeneratedHttpRequests!");

      String toSign = createStringToSign();
      String signatureMd5 = getMd5For(toSign);

      String query = request.getEndpoint().getQuery();
      Multimap<String, String> decodedParams = RestAnnotationProcessor.parseQueryToMap(query);

      decodedParams.replaceValues("sig", ImmutableSet.of(signatureMd5));
      decodedParams.replaceValues("api_key", ImmutableSet.of(apiKey));

      String updatedQuery = RestAnnotationProcessor.makeQueryLine(decodedParams, null);
      String requestBasePart = request.getEndpoint().toASCIIString();
      String updatedEndpoint = requestBasePart.substring(0, requestBasePart.indexOf("?") + 1)
               + updatedQuery;
      request.setEndpoint(URI.create(updatedEndpoint));

      HttpUtils.logRequest(signatureLog, request, "<<");
   }

   private String createStringToSign() {
      return format("%s%s%s", apiKey, secret, timeStamp);
   }

   private String getMd5For(String stringToHash) {
      try {
         return encryptionService.md5Hex(stringToHash.getBytes());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

}
