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
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.CreateAccountOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to CloudStack Account features available to Global
 * Admin users.
 *
 * @author Adrian Cole, Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalAccountAsyncClient extends DomainAccountAsyncClient {

   /**
    * @see GlobalAccountClient#createAccount
    */
   @GET
   @QueryParams(keys = "command", values = "createAccount")
   @SelectJson("account")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Account> createAccount(@QueryParam("username") String userName,
      @QueryParam("accounttype") Account.Type accountType, @QueryParam("email") String email,
      @QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName,
      @QueryParam("password") String hashedPassword, CreateAccountOptions... options);

   /**
    * @see GlobalAccountClient#deleteAccount
    */
   @GET
   @QueryParams(keys = "command", values = "deleteAccount")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Void> deleteAccount(@QueryParam("id") long id);
}
