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

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListDomainChildrenOptions;
import org.jclouds.cloudstack.options.ListDomainsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack Domain features available to Domain
 * Admin users.
 *
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface DomainDomainApi {

   /**
    * List domains with detailed information
    *
    * @param options
    *          list filtering optional arguments
    * @return
    *          set of domain instances or empty
    */
   @Named("listDomains")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDomains", "true" })
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Domain> listDomains(ListDomainsOptions... options);

   /**
    * Get a domain by ID
    *
    * @param domainId
    *          domain ID
    * @return
    *          domain instance or null
    */
   @Named("listDomains")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDomains", "true" })
   @SelectJson("domain")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Domain getDomainById(@QueryParam("id") String domainId);

   /**
    * Lists all children domains belonging to a specified domain
    *
    * @param options
    *          list filtering optional arguments
    * @return
    *          set of domain instances or empty
    */
   @Named("listDomainChildren")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDomainChildren", "true" })
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Domain> listDomainChildren(ListDomainChildrenOptions... options);
}
