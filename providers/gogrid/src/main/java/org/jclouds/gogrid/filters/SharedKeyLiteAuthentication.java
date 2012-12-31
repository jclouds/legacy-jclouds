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
package org.jclouds.gogrid.filters;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
import static java.lang.String.format;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import com.google.common.collect.ImmutableMap;

/**
 * @author Oleksiy Yarmula
 */
public class SharedKeyLiteAuthentication implements HttpRequestFilter {

   private final String apiKey;
   private final String secret;
   private final Long timeStamp;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyLiteAuthentication(@Identity String apiKey, @Credential String secret, @TimeStamp Long timeStamp,
            HttpUtils utils) {
      this.apiKey = apiKey;
      this.secret = secret;
      this.timeStamp = timeStamp;
      this.utils = utils;
   }

   @Override
   public HttpRequest filter(HttpRequest request) {
      String toSign = createStringToSign();
      String signatureMd5 = getMd5For(toSign);
      request = request.toBuilder().replaceQueryParams(ImmutableMap.of("sig", signatureMd5, "api_key" ,apiKey)).build();
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   private String createStringToSign() {
      return format("%s%s%s", apiKey, secret, timeStamp);
   }

   private String getMd5For(String stringToHash) {
      return base16().lowerCase().encode(md5().hashString(stringToHash, UTF_8).asBytes());
   }

}
