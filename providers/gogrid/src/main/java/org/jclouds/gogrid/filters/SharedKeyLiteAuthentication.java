/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Oleksiy Yarmula
 */
public class SharedKeyLiteAuthentication implements HttpRequestFilter {

   private final Supplier<Credentials> creds;
   private final Long timeStamp;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyLiteAuthentication(@Provider Supplier<Credentials> creds, @TimeStamp Long timeStamp, HttpUtils utils) {
      this.creds = creds;
      this.timeStamp = timeStamp;
      this.utils = utils;
   }

   @Override
   public HttpRequest filter(HttpRequest request) {
      String toSign = createStringToSign();
      String signatureMd5 = getMd5For(toSign);
      request = request.toBuilder()
            .replaceQueryParams(ImmutableMap.of("sig", signatureMd5, "api_key", creds.get().identity)).build();
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   private String createStringToSign() {
      return format("%s%s%s", creds.get().identity, creds.get().credential, timeStamp);
   }

   private String getMd5For(String stringToHash) {
      return base16().lowerCase().encode(md5().hashString(stringToHash, UTF_8).asBytes());
   }

}
