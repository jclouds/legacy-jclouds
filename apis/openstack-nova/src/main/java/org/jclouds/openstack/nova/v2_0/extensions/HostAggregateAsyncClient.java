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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to Aggregates in Nova.
 *
 * @author Adam Lowe
 * @see HostAggregateClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.AGGREGATES)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-aggregates")
public interface HostAggregateAsyncClient {

   /**
    * @see HostAggregateClient#listAggregates()
    */
   @GET
   @SelectJson("aggregates")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<HostAggregate>> listAggregates();

   /**
    * @see HostAggregateClient#getAggregate(String)
    */
   @GET
   @Path("/{id}")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<HostAggregate> getAggregate(@PathParam("id") String id);

   /**
    * @see HostAggregateClient#createAggregate(String, String)
    */
   @POST
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("aggregate")
   ListenableFuture<HostAggregate> createAggregate(@PayloadParam("name") String name,
                                                   @PayloadParam("availability_zone") String availablityZone);

   /**
    * @see HostAggregateClient#updateName
    */
   @POST
   @Path("/{id}")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @WrapWith("aggregate")
   ListenableFuture<HostAggregate> updateName(@PathParam("id") String id, @PayloadParam("name") String name);

   /**
    * @see HostAggregateClient#updateAvailabilityZone
    */
   @POST
   @Path("/{id}")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @WrapWith("aggregate")
   ListenableFuture<HostAggregate> updateAvailabilityZone(@PathParam("id") String id, @PayloadParam("availability_zone") String availabilityZone);
   
   /**
    * @see HostAggregateClient#deleteAggregate(String)
    */
   @DELETE
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteAggregate(@PathParam("id") String id);

   /**
    * @see HostAggregateClient#addHost(String,String)
    */
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("add_host")
   ListenableFuture<HostAggregate> addHost(@PathParam("id") String id, @PayloadParam("host") String host);


   /**
    * @see HostAggregateClient#removeHost(String,String)
    */
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("remove_host")
   ListenableFuture<HostAggregate> removeHost(@PathParam("id") String id, @PayloadParam("host") String host);

   /**
    * @see HostAggregateClient#setMetadata
    */
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("set_metadata")
   ListenableFuture<HostAggregate> setMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);
}
