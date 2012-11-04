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
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AssignVirtualMachineOptions;
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
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface VirtualMachineAsyncClient {

   /**
    * @see VirtualMachineClient#listVirtualMachines
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVirtualMachines", "true" })
   @SelectJson("virtualmachine")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualMachine>> listVirtualMachines(ListVirtualMachinesOptions... options);

   /**
    * @see VirtualMachineClient#getVirtualMachine
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVirtualMachines", "true" })
   @SelectJson("virtualmachine")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualMachine> getVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#deployVirtualMachineInZone
    */
   @GET
   @QueryParams(keys = "command", values = "deployVirtualMachine")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> deployVirtualMachineInZone(@QueryParam("zoneid") String zoneId,
         @QueryParam("serviceofferingid") String serviceOfferingId, @QueryParam("templateid") String templateId,
         DeployVirtualMachineOptions... options);

   /**
    * @see VirtualMachineClient#rebootVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "rebootVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> rebootVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#startVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "startVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> startVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#stopVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "stopVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> stopVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#resetPasswordForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "resetPasswordForVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> resetPasswordForVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#getEncryptedPasswordForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "getVMPassword")
   @SelectJson("encryptedpassword")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> getEncryptedPasswordForVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#changeServiceForVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "changeServiceForVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> changeServiceForVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#updateVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "updateVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> updateVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#destroyVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "destroyVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<String> destroyVirtualMachine(@QueryParam("id") String id);

   /**
    * @see VirtualMachineClient#assinVirtualMachine
    */
   @GET
   @QueryParams(keys = "command", values = "assignVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<VirtualMachine> assignVirtualMachine(@QueryParam("virtualmachineid") String virtualMachineId,
                                                         AssignVirtualMachineOptions... options);

}
