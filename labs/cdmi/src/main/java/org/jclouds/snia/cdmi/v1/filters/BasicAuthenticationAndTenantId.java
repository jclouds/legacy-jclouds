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
package org.jclouds.snia.cdmi.v1.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.jclouds.http.filters.BasicAuthentication.basic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.location.Provider;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Supplier;

/**
 * Uses Basic Authentication to sign the request, and adds the {@code TID} header.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthenticationAndTenantId implements HttpRequestFilter {
   private final Supplier<Credentials> creds;

   @Inject
   public BasicAuthenticationAndTenantId(@Provider Supplier<Credentials> creds) {
      this.creds = checkNotNull(creds, "creds");
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      if (currentCreds.identity.indexOf(':') == -1) {
         throw new AuthorizationException(String.format("Identity %s does not match format tenantId:username",
               currentCreds.identity), null);
      }
      String tenantId = currentCreds.identity.substring(0, currentCreds.identity.indexOf(':'));
      String username = currentCreds.identity.substring(currentCreds.identity.indexOf(':') + 1);
      return request.toBuilder().replaceHeader("TID", tenantId)
            .replaceHeader(AUTHORIZATION, basic(username, currentCreds.credential)).build();
   }
}
