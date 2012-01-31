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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.functions.ParseLoginResponseFromHttpResponse;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
   @GET
   @QueryParams(keys = "command", values = "login")
   @ResponseParser(ParseLoginResponseFromHttpResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LoginResponse> loginUserInDomainWithHashOfPassword(@QueryParam("username") String userName,
      @QueryParam("domain") String domain, @QueryParam("password") String hashedPassword);

   /**
    * @see SessionClient#getAccountByNameUsingSession
    */
   @GET
   @QueryParams(keys = "comand", values = "listAccounts")
   @SelectJson("account")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Account> getAccountByNameUsingSession(@QueryParam("name") String accountName,
      @QueryParam("sessionkey") String sessionKey, @CookieParam("JSESSIONID") String jSessionId);

   /**
    * @see SessionClient#logoutUser
    */
   @GET
   @QueryParams(keys = "command", values = "logout")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> logoutUser(@QueryParam("sessionkey") String sessionKey);
}
