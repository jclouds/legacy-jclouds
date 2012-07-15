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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

/**
 * Uses Basic Authentication to sign the request, and adds the {@code TID} header.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthenticationAndTenantId implements HttpRequestFilter {
   private final String tenantId;
   private final BasicAuthentication basicAuthentication;

   @Inject
   public BasicAuthenticationAndTenantId(@Identity String tenantIdAndUsername, @Credential String password,
            Crypto crypto) {
      if (tenantIdAndUsername.indexOf(':') == -1) {
         throw new AuthorizationException(String.format("Identity %s does not match format tenantId:username",
                  tenantIdAndUsername), null);
      }
      this.tenantId = tenantIdAndUsername.substring(0, tenantIdAndUsername.indexOf(':'));
      String username = tenantIdAndUsername.substring(tenantIdAndUsername.indexOf(':') + 1);
      this.basicAuthentication = new BasicAuthentication(username, password, crypto);
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return basicAuthentication.filter(request.toBuilder().replaceHeader("TID", tenantId).build());
   }
}