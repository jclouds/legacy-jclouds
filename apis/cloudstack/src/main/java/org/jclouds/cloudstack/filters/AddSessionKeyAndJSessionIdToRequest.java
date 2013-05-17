/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.filters;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;

import com.google.common.base.Supplier;
import com.google.common.net.HttpHeaders;

/**
 * 
 * @author Andrei Savu, Adrian Cole
 * @see <a href="http://docs.cloud.com/CloudStack_Documentation/Customizing_the_CloudStack_UI#Cross_Site_Request_Forgery_%28CSRF%29"
 *      />
 */
@Singleton
public class AddSessionKeyAndJSessionIdToRequest implements AuthenticationFilter {

   private final Supplier<LoginResponse> loginResponseSupplier;

   @Inject
   public AddSessionKeyAndJSessionIdToRequest(Supplier<LoginResponse> loginResponseSupplier) {
      this.loginResponseSupplier = loginResponseSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      LoginResponse loginResponse = loginResponseSupplier.get();
      Builder<?> builder = request.toBuilder();
      builder.replaceHeader(HttpHeaders.COOKIE, "JSESSIONID=" + loginResponse.getJSessionId());
      builder.replaceQueryParam("sessionkey", loginResponse.getSessionKey());
      return builder.build();

   }

}
