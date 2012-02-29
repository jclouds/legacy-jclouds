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
package org.jclouds.openstack.nova.v1_1.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroup;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Security Groups via the REST API.
 * <p/>
 * 
 * @see SecurityGroupClient
 * @author Jeremy Daggett
 */
@SkipEncoding({ '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface SecurityGroupAsyncClient {

   /**
    * @see SecurityGroupClient#listSecurityGroups
    */
   @GET
   @SelectJson("security_groups")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/os-security-groups")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SecurityGroup>> listSecurityGroups();
   
   /**
    * @see SecurityGroupClient#getSecurityGroup
    */
   @GET
   @Path("/os-security-groups/{id}")
   @SelectJson("security_group")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> getSecurityGroup(@PathParam("id") String id);

   
   /**
    * @see SecurityGroupClient#createSecurityGroup
    */
   @POST
   @Path("/os-security-groups")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Payload("name {name}\n")
   @Produces(MediaType.TEXT_PLAIN)
   ListenableFuture<SecurityGroup> createSecurityGroup(@PayloadParam("name") String name);

}
