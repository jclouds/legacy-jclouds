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
package org.jclouds.glesys.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.EditAccountOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to E-Mail data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.features.EmailApi
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface EmailAsyncApi {

   /**
    * @see org.jclouds.glesys.features.EmailApi#getEmailOverview
    */
   @POST
   @Path("/email/overview/format/json")
   @SelectJson("overview")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<EmailOverview> getEmailOverview();

   /**
    * @see org.jclouds.glesys.features.EmailApi#listAccounts
    */
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaccounts")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<EmailAccount>> listAccounts(@FormParam("domainname") String domain);

   /**
    * @see org.jclouds.glesys.features.EmailApi#listAccounts
    */
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaliases")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<EmailAlias>> listAliases(@FormParam("domainname") String domain);

   /**
    * @see org.jclouds.glesys.features.EmailApi#createAccount
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/createaccount/format/json")
   ListenableFuture<EmailAccount> createAccount(@FormParam("emailaccount") String accountAddress, @FormParam("password") String password, CreateAccountOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailApi#createAlias
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/createalias/format/json")
   ListenableFuture<EmailAlias> createAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailApi#editAccount
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/editaccount/format/json")
   ListenableFuture<EmailAccount> editAccount(@FormParam("emailaccount") String accountAddress, EditAccountOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailApi#editAlias
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/editalias/format/json")
   ListenableFuture<EmailAlias> editAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailApi#delete
    */
   @POST
   @Path("/email/delete/format/json")
   ListenableFuture<Boolean> delete(@FormParam("email") String accountAddress);

}
