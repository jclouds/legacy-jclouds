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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.HardwareConfiguration;
import org.jclouds.tmrk.enterprisecloud.domain.network.AssignedIpAddresses;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineConfigurationOptions;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachines;
import org.jclouds.tmrk.enterprisecloud.functions.ReturnEmptyVirtualMachinesOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.net.URI;

/**
 * Provides asynchronous access to VirtualMachine via their REST API.
 * <p/>
 * 
 * @see TaskClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Headers(keys = "x-tmrk-version", values = "{jclouds.api-version}")
public interface VirtualMachineAsyncClient {

   /**
    * @see VirtualMachineClient#getVirtualMachines
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.virtualMachine; type=collection")
   @JAXBResponseParser
   @ExceptionParser(ReturnEmptyVirtualMachinesOnNotFoundOr404.class)
   ListenableFuture<VirtualMachines> getVirtualMachines(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#getVirtualMachine
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.virtualMachine")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualMachine> getVirtualMachine(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#getVirtualMachine
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.virtualMachineAssignedIps")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AssignedIpAddresses> getAssignedIpAddresses(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#getConfigurationOptions
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.virtualMachineConfigurationOptions")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualMachineConfigurationOptions> getConfigurationOptions(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#getHardwareConfiguration
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.virtualMachineHardware")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<HardwareConfiguration> getHardwareConfiguration(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#powerOn
    */
   @POST
   @Path("/action/powerOn")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> powerOn(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#powerOff
    */
   @POST
   @Path("/action/powerOff")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> powerOff(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#reboot
    */
   @POST
   @Path("/action/reboot")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> reboot(@EndpointParam URI uri);

   /**
    * @see VirtualMachineClient#shutdown
    */
   @POST
   @Path("/action/shutdown")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> shutdown(@EndpointParam URI uri);

/**
    * @see VirtualMachineClient#mountTools
    */
   @POST
   @Path("/tools/action/mount")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> mountTools(@EndpointParam URI uri);

/**
    * @see VirtualMachineClient#unmountTools
    */
   @POST
   @Path("/tools/action/unmount")
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> unmountTools(@EndpointParam URI uri);
}
