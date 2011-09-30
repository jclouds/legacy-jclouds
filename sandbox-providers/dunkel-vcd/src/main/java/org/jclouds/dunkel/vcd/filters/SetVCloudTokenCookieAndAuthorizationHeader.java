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
package org.jclouds.dunkel.vcd.filters;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;

/**
 * Adds the VCloud Token to the request as a cookie
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class SetVCloudTokenCookieAndAuthorizationHeader extends SetVCloudTokenCookie {

   private final Provider<String> vcloudTokenProvider;

   @Inject
   public SetVCloudTokenCookieAndAuthorizationHeader(@VCloudToken Provider<String> authTokenProvider) {
      super(authTokenProvider);
      this.vcloudTokenProvider = authTokenProvider;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.replaceHeader(super.filter(request), "x-vcloud-authorization", vcloudTokenProvider.get());
   }

}
