/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see OfferingClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface SecurityGroupAsyncClient {

   /**
    * @see SecurityGroupClient#listSecurityGroups
    */
   @GET
   @QueryParams(keys = "command", values = "listSecurityGroups")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SecurityGroup>> listSecurityGroups(ListSecurityGroupsOptions... options);

   /**
    * @see SecurityGroupClient#getSecurityGroup
    */
   @GET
   @QueryParams(keys = "command", values = "listSecurityGroups")
   @Unwrap(depth = 3, edgeCollection = Set.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> getSecurityGroup(@QueryParam("id") long id);

   /**
    * @see SecurityGroupClient#createSecurityGroup
    */
   @GET
   @QueryParams(keys = "command", values = "createSecurityGroup")
   @Unwrap(depth = 3, edgeCollection = Set.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<SecurityGroup> createSecurityGroup(@QueryParam("name") String name);

   /**
    * @see SecurityGroupClient#deleteSecurityGroup
    */
   @GET
   @QueryParams(keys = "command", values = "deleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSecurityGroup(@QueryParam("id") long id);

}
