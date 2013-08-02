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

import org.jclouds.Fallbacks;
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

/**
 * Provides synchronous access to E-Mail requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface EmailAccountApi {

   /**
    * Get a summary of e-mail accounts associated with this Glesys account
    *
    * @return the relevant summary data
    */
   @Named("email:overview")
   @POST
   @Path("/email/overview/format/json")
   @SelectJson("overview")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   EmailOverview getOverview();

   /**
    * Get the set of detailed information about e-mail accounts
    *
    * @return the relevant set of details
    */
   @Named("email:list:accounts")
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaccounts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<EmailAccount> listDomain(@FormParam("domainname") String domain);

   /**
    * Get the set of details about e-mail aliases
    *
    * @return the relevant set of details
    */
   @Named("email:list:aliases")
   @POST
   @Path("/email/list/format/json")
   @SelectJson("emailaliases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<EmailAlias> listAliasesInDomain(@FormParam("domainname") String domain);

   /**
    * Create a new e-mail account
    *
    * @param accountAddress the e-mail address to use (the domain should already exist)
    * @param password       the password to use for the mailbox
    * @param options        optional parameters
    * @see DomainApi#create
    */
   @Named("email:createaccount")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/createaccount/format/json")
   EmailAccount createWithPassword(@FormParam("emailaccount") String accountAddress, @FormParam("password") String password, CreateAccountOptions... options);

   /**
    * Create an e-mail alias for an e-mail account
    *
    * @param aliasAddress   the address to use for the alias  (the domain should already exist)
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    * @see DomainApi#create
    */
   @Named("email:createalias")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/createalias/format/json")
   EmailAlias createAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * Adjust an e-mail account's settings
    *
    * @param accountAddress the existing e-mail account address
    * @param options        optional parameters
    */
   @Named("email:editaccount")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("emailaccount")
   @Path("/email/editaccount/format/json")
   EmailAccount update(@FormParam("emailaccount") String accountAddress, UpdateAccountOptions... options);

   /**
    * Adjust (re-target) an e-mail alias
    *
    * @param aliasAddress   the existing alias e-mail address
    * @param toEmailAddress the existing e-mail account address the alias should forward to
    */
   @Named("email:editalias")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("alias")
   @Path("/email/editalias/format/json")
   EmailAlias updateAlias(@FormParam("emailalias") String aliasAddress, @FormParam("goto") String toEmailAddress);

   /**
    * Delete an e-mail account or alias
    *
    * @param accountAddress the existing alias e-mail account or alias address
    */
   @Named("email:delete")
   @POST
   @Path("/email/delete/format/json")
   @Fallback(Fallbacks.TrueOnNotFoundOr404.class)
   boolean delete(@FormParam("email") String accountAddress);

}
