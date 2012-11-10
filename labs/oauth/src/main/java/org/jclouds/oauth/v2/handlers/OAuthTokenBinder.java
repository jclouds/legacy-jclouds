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
package org.jclouds.oauth.v2.handlers;

import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.domain.TokenRequestFormat;
import org.jclouds.rest.Binder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Generic implementation of a token binder. Uses a provided {@link TokenRequestFormat} to actually bind tokens to
 * requests.
 *
 * @author David Alves
 */
@Singleton
public class OAuthTokenBinder implements Binder {

   private final TokenRequestFormat tokenRequestFormat;

   @Inject
   OAuthTokenBinder(TokenRequestFormat tokenRequestFormat) {
      this.tokenRequestFormat = tokenRequestFormat;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return tokenRequestFormat.formatRequest(request, (TokenRequest) input);
   }
}
