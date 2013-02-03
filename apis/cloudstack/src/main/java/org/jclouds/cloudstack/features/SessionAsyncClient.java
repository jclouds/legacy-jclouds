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
package org.jclouds.cloudstack.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.functions.ParseLoginResponseFromHttpResponse;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloudstack Sessions
 * <p/>
 * 
 * @see org.jclouds.cloudstack.features.SessionClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Andrei Savu
 */
@QueryParams(keys = "response", values = "json")
public interface SessionAsyncClient {

   /**
    * @see SessionClient#loginUserInDomainWithHashOfPassword
    */
   @Named("login")
   @GET
   @QueryParams(keys = "command", values = "login")
   @ResponseParser(ParseLoginResponseFromHttpResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<LoginResponse> loginUserInDomainWithHashOfPassword(@QueryParam("username") String userName,
      @QueryParam("domain") String domain, @QueryParam("password") String hashedPassword);

   /**
    * @see SessionClient#logoutUser
    */
   @Named("logout")
   @GET
   @QueryParams(keys = "command", values = "logout")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> logoutUser(@QueryParam("sessionkey") String sessionKey);
}
