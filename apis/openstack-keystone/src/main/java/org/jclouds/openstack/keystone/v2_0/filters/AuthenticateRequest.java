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
package org.jclouds.openstack.keystone.v2_0.filters;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.openstack.keystone.v2_0.config.Authentication;
import org.jclouds.openstack.v2_0.reference.AuthHeaders;

import com.google.common.base.Supplier;

/**
 * Signs the Keystone-based request. This will update the Authentication Token before 24 hours is up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class AuthenticateRequest implements HttpRequestFilter {

   private final Supplier<String> authTokenProvider;

   @Inject
   public AuthenticateRequest(@Authentication Supplier<String> authTokenProvider) {
      this.authTokenProvider = authTokenProvider;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.replaceHeader(request, AuthHeaders.AUTH_TOKEN, authTokenProvider.get());
   }

}