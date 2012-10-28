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
package org.jclouds.oauth.v2.filters;

import com.google.common.base.Supplier;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.oauth.v2.config.Authentication;
import org.jclouds.oauth.v2.domain.Token;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * To be used by client applications to embed an OAuth authentication in their REST requests.
 * <p/>
 * TODO when we're able to use the OAuthAuthentication an this should be used automatically
 *
 * @author David Alves
 */
@Singleton
public class OAuthAuthenticator implements HttpRequestFilter {

   private Supplier<Token> tokeSupplier;

   @Inject
   OAuthAuthenticator(@Authentication Supplier<Token> tokenSupplier) {
      this.tokeSupplier = tokenSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return request.toBuilder().addHeader("Authorization", String.format("Bearer %s",
              tokeSupplier.get().getAccessToken())).build();
   }
}
