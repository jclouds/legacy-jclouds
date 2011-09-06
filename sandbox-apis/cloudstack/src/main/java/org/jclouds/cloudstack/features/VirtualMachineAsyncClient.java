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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see VirtualMachineClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface VirtualMachineAsyncClient {

   /**
    * @see VirtualMachineClient#listVirtualMachines
    */
   @GET
   @QueryParams(keys = "command", values = "listVirtualMachines")
   @SelectJson("virtualmachine")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualMachine>> listVirtualMachines(ListVirtualMachinesOptions... options);

   /**
    * @see VirtualMachineClient#getVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "listVirtualMachines")
   @SelectJson("virtualmachine")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualMachine> getVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#deployVirtualMachineInZone
    */
   @GET
   @QueryParams(keys = "command", values = "deployVirtualMachine")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> deployVirtualMachineInZone( @QueryParam("zoneid") long zoneId, @QueryParam("serviceofferingid") long serviceOfferingId,
            @QueryParam("templateid") long templateId,
            DeployVirtualMachineOptions... options);

   /**
    * @see VirtualMachineClient#rebootVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "rebootVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> rebootVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#startVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "startVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> startVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#stopVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "stopVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> stopVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#resetPasswordForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "resetPasswordForVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> resetPasswordForVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#changeServiceForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "changeServiceForVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> changeServiceForVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#updateVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "updateVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Long> updateVirtualMachine(@QueryParam("id") long id);

   /**
    * @see VirtualMachineClient#destroyVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "destroyVirtualMachine")
   @Unwrap(depth = 2)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Long> destroyVirtualMachine(@QueryParam("id") long id);

}
