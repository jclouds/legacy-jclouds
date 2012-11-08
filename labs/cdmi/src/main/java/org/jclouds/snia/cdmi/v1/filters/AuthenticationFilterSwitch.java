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
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

/**
 * Authentication filter switch. This method decides the type of authentication mechanism. Currently, support its for
 * basic authentication with TID and openstack keystone token authentication. The default is openstack keystone token
 * authentication.
 * 
 * @see org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId
 * @see org.jclouds.snia.cdmi.v1.filters.OpenstackKeystoneAuthReqFilter
 * 
 * @author Kenneth Nagin
 * 
 */
@Singleton
public class AuthenticationFilterSwitch implements HttpRequestFilter {
   private final HttpRequestFilter httpRequestFilter;

   /*
    * AuthenticationFilterSwitch: decides which httpRequestFilter to use.
    * 
    * @param identity user's identify in form: <pre> <tenant>:<username>[?authType=[basicAuthTid|openstackKeystone]
    * Defaults to basicAuthTid. <pre> Examples: {@code Admin:admin?authType=openstackKeystone
    * 
    * Admin:admin?authType=basicAuthTid
    * 
    * Test:tester // this defaults to openstackKeystone } </pre>
    * 
    * @param credential user's credenitial, i.e. password.
    * 
    * @param crypto
    */
   @Inject
   public AuthenticationFilterSwitch(@Identity String identity, @Credential String password, Crypto crypto) {
      String tenantAndUsername = identity;
      String authType = "openstackKeystone"; // default to opentack keystone
                                             // token authentication
      int identityParamIndex = identity.indexOf('?');
      if (identityParamIndex > -1) {
         String keyValuePairs[] = identity.substring(identityParamIndex + 1).split("[,]+");
         tenantAndUsername = identity.substring(0, identityParamIndex);
         for (String pair : keyValuePairs) {
            if (pair.startsWith("authType")) {
               authType = pair.substring(pair.indexOf("=") + 1).trim();
            }
         }
      }
      if (authType.matches("basicAuthTid")) {
         httpRequestFilter = new BasicAuthenticationAndTenantId(tenantAndUsername, password, crypto);
      } else if (authType.matches("openstackKeystone")) {
         httpRequestFilter = new OpenstackKeystoneAuthReqFilter(tenantAndUsername, password);
      } else {
         httpRequestFilter = new BasicAuthenticationAndTenantId(tenantAndUsername, password, crypto);
      }
   }

   @Override
   public HttpRequest filter(HttpRequest httpRequestIn) throws HttpException {
      return httpRequestFilter.filter(httpRequestIn);
   }
}
