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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.glesys.domain.*;
import org.jclouds.glesys.options.EmailCreateOptions;
import org.jclouds.glesys.options.EmailEditOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to E-Mail data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.features.EmailClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface EmailAsyncClient {

   /**
    * @see org.jclouds.glesys.features.EmailClient#emailOverview
    */
   @POST
   @Path("/email/overview/format/json")
   @SelectJson("response")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<EmailOverview> getEmailOverview();

   /**
    * @see org.jclouds.glesys.features.EmailClient#listAccounts
    */
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaccounts")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Email>> listAccounts(@FormParam("domain") String domain);

   /**
    * @see org.jclouds.glesys.features.EmailClient#createAccount
    */
   @POST
   @Path("/email/createaccount/format/json")
   ListenableFuture<Void> createAccount(@FormParam("emailaccount") String accountAddress, @FormParam("password") String password, EmailCreateOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailClient#createAlias
    */
   @POST
   @Path("/email/createalias/format/json")
   ListenableFuture<Void> createAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailClient#editAccount
    */
   @POST
   @Path("/email/editaccount/format/json")
   ListenableFuture<Void> editAccount(@FormParam("emailaccount") String accountAddress, EmailEditOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailClient#editAlias
    */
   @POST
   @Path("/email/editalias/format/json")
   ListenableFuture<Void> editAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailClient#delete
    */
   @POST
   @Path("/email/delete/format/json")
   ListenableFuture<Void> delete(@FormParam("email") String accountAddress);

}
