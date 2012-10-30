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
package org.jclouds.vcloud.filters;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.vcloud.VCloudToken;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;

/**
 * Adds the VCloud Token to the request as a cookie
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class AddVCloudAuthorizationAndCookieToRequest implements HttpRequestFilter {
   private Supplier<String> vcloudTokenProvider;

   @Inject
   public AddVCloudAuthorizationAndCookieToRequest(@VCloudToken Supplier<String> authTokenProvider) {
      this.vcloudTokenProvider = authTokenProvider;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String token = vcloudTokenProvider.get();
      return request
               .toBuilder()
               .replaceHeaders(
                        ImmutableMultimap.of("x-vcloud-authorization", token, HttpHeaders.COOKIE, "vcloud-token="
                                 + token)).build();
   }
}
