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
package org.jclouds.glesys.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.TrueOnNotFoundOr404;
import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.UpdateAccountOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to E-Mail data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.features.EmailAccountApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface EmailAccountAsyncApi {

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#getOverview
    */
   @Named("email:overview")
   @POST
   @Path("/email/overview/format/json")
   @SelectJson("overview")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<EmailOverview> getOverview();

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#listDomain
    */
   @Named("email:list:accounts")
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaccounts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<EmailAccount>> listDomain(@FormParam("domainname") String domain);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#listAliasesInDomain
    */
   @Named("email:list:aliases")
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaliases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<EmailAlias>> listAliasesInDomain(@FormParam("domainname") String domain);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#createWithPassword
    */
   @Named("email:createaccount")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/createaccount/format/json")
   ListenableFuture<EmailAccount> createWithPassword(@FormParam("emailaccount") String accountAddress, @FormParam("password") String password, CreateAccountOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#createAlias
    */
   @Named("email:createalias")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/createalias/format/json")
   ListenableFuture<EmailAlias> createAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#update
    */
   @Named("email:editaccount")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/editaccount/format/json")
   ListenableFuture<EmailAccount> update(@FormParam("emailaccount") String accountAddress, UpdateAccountOptions... options);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#updateAlias
    */
   @Named("email:editalias")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/editalias/format/json")
   ListenableFuture<EmailAlias> updateAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * @see org.jclouds.glesys.features.EmailAccountApi#delete
    */
   @Named("email:delete")
   @POST
   @Path("/email/delete/format/json")
   @Fallback(TrueOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@FormParam("email") String accountAddress);

}
