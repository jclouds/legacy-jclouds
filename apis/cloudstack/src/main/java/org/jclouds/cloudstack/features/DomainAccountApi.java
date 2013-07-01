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
package org.jclouds.cloudstack.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

/**
 * Provides synchronous access to CloudStack Account features available to Domain
 * Admin users.
 *
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface DomainAccountApi extends AccountApi {

   /**
    * Enable an account
    *
    * @param accountName
    *    the account name you are enabling
    * @param domainId
    *    the domain ID
    */
   @Named("enableAccount")
   @GET
   @QueryParams(keys = "command", values = "enableAccount")
   @SelectJson("account")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Account enableAccount(@QueryParam("account") String accountName, @QueryParam("domainid") String domainId);


   /**
    * Disable or lock an account
    *
    * @param accountName
    *    the account name you are disabling
    * @param domainId
    *    the domain ID
    * @param onlyLock
    *    only lock if true disable otherwise
    */
   @Named("disableAccount")
   @GET
   @QueryParams(keys = "command", values = "disableAccount")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   AsyncCreateResponse disableAccount(@QueryParam("account") String accountName,
      @QueryParam("domainid") String domainId, @QueryParam("lock") boolean onlyLock);
   
}
