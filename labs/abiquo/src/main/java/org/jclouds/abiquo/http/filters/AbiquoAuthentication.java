/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.config.AbiquoProperties.CREDENTIAL_IS_TOKEN;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;

/**
 * Authenticates using Basic Authentication or a generated token from previous
 * API sessions.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AbiquoAuthentication implements HttpRequestFilter {
   /** The name of the authentication token. */
   public static final String AUTH_TOKEN_NAME = "auth";

   protected String identity;

   protected String credential;

   protected boolean credentialIsToken;

   @Inject
   public AbiquoAuthentication(@Identity final String identity, @Credential final String credential,
         @Named(CREDENTIAL_IS_TOKEN) final String credentialIsToken) {
      this.identity = checkNotNull(identity, "identity");
      this.credential = checkNotNull(credential, "credential");
      this.credentialIsToken = Boolean.valueOf(credentialIsToken);
   }

   @Override
   public HttpRequest filter(final HttpRequest request) throws HttpException {
      String header = credentialIsToken ? tokenAuth(credential) : basicAuth(identity, credential);
      return request.toBuilder()
            .replaceHeader(credentialIsToken ? HttpHeaders.COOKIE : HttpHeaders.AUTHORIZATION, header).build();
   }

   @VisibleForTesting
   static String basicAuth(final String user, final String password) {
      return "Basic "
            + CryptoStreams.base64(String.format("%s:%s", checkNotNull(user, "user"),
                  checkNotNull(password, "password")).getBytes(Charsets.UTF_8));
   }

   @VisibleForTesting
   static String tokenAuth(final String token) {
      return AUTH_TOKEN_NAME + "=" + checkNotNull(token, "token");
   }
}
