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
package org.jclouds.http.filters;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static java.lang.String.format;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

/**
 * Uses Basic Authentication to sign the request.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthentication implements HttpRequestFilter {

   private final String header;

   @Inject
   public BasicAuthentication(@Identity String user, @Credential String password, Crypto crypto) {
      checkNotNull(user, "user");
      checkNotNull(password, "password");
      this.header = basic(user, password);
   }

   public static String basic(String user, String password) {
      return new StringBuilder("Basic ").append(base64().encode(format("%s:%s", user, password).getBytes(UTF_8)))
            .toString();
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return request.toBuilder().replaceHeader(AUTHORIZATION, header).build();
   }
}
