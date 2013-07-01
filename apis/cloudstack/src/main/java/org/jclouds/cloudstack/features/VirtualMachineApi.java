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
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AssignVirtualMachineOptions;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.cloudstack.options.StopVirtualMachineOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface VirtualMachineApi {

   /**
    * Lists VirtualMachines
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return VirtualMachines matching query, or empty set, if no
    *         VirtualMachines are found
    */
   @Named("listVirtualMachines")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVirtualMachines", "true" })
   @SelectJson("virtualmachine")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<VirtualMachine> listVirtualMachines(ListVirtualMachinesOptions... options);

   /**
    * get a specific VirtualMachine by id
    * 
    * @param id
    *           VirtualMachine to get
    * @return VirtualMachine or null if not found
    */
   @Named("listVirtualMachines")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVirtualMachines", "true" })
   @SelectJson("virtualmachine")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   VirtualMachine getVirtualMachine(@QueryParam("id") String id);

   /**
    * Creates and automatically starts a virtual machine based on a service
    * offering, disk offering, and template.
    * 
    * @param zoneId
    *           availability zone for the virtual machine
    * @param serviceOfferingId
    *           the ID of the service offering for the virtual machine
    * @param templateId
    *           the ID of the template for the virtual machine
    * 
    * @return virtual machine
    */
   @Named("deployVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "deployVirtualMachine")
   @SelectJson({ "deployvirtualmachine", "deployvirtualmachineresponse" })
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse deployVirtualMachineInZone(@QueryParam("zoneid") String zoneId,
         @QueryParam("serviceofferingid") String serviceOfferingId, @QueryParam("templateid") String templateId,
         DeployVirtualMachineOptions... options);

   /**
    * Reboots a virtual machine.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("rebootVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "rebootVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String rebootVirtualMachine(@QueryParam("id") String id);

   /**
    * Starts a virtual machine.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("startVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "startVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String startVirtualMachine(@QueryParam("id") String id);

   /**
    * Stops a virtual machine.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("stopVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "stopVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String stopVirtualMachine(@QueryParam("id") String id);

   /**
    * Stops a virtual machine.
    * 
    * @param id
    *           The ID of the virtual machine
    * @param options
    *           If present, whether to force stop.
    * @return job id related to destroying the VM
    */
   @GET
   @QueryParams(keys = "command", values = "stopVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String stopVirtualMachine(@QueryParam("id") String id,
                                               StopVirtualMachineOptions options);

   /**
    * Resets the password for virtual machine. The virtual machine must be in a
    * "Stopped" state and the template must already support this feature for
    * this command to take effect.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("resetPasswordForVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "resetPasswordForVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String resetPasswordForVirtualMachine(@QueryParam("id") String id);

   /**
    * Return an encrypted password for the virtual machine. The command
    * is asynchronous.
    *
    * @param id
    *          the ID of the virtual machine
    * @return encrypted password
    */
   @Named("getVMPassword")
   @GET
   @QueryParams(keys = "command", values = "getVMPassword")
   @SelectJson("encryptedpassword")
   @Consumes(MediaType.APPLICATION_JSON)
   String getEncryptedPasswordForVirtualMachine(@QueryParam("id") String id);

   /**
    * Changes the service offering for a virtual machine. The virtual machine
    * must be in a "Stopped" state for this command to take effect.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("changeServiceForVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "changeServiceForVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String changeServiceForVirtualMachine(@QueryParam("id") String id);

   /**
    * Updates parameters of a virtual machine.
    * 
    * @param id
    *           The ID of the virtual machine
    * @return job id related to destroying the VM
    */
   @Named("updateVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "updateVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   String updateVirtualMachine(@QueryParam("id") String id);

   /**
    * Destroys a virtual machine. Once destroyed, only the administrator can
    * recover it.
    * 
    * @param id
    *           vm to destroy
    * @return job id related to destroying the VM, or null if resource was not
    *         found
    */
   @Named("destroyVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "destroyVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String destroyVirtualMachine(@QueryParam("id") String id);

   /**
    * Re-assign a virtual machine to a different account/domain.
    * 
    * @param virtualMachineId
    *           VirtualMachine to re-assign
    * @param options
    *           AssignVirtualMachineOptions specifying account and domain to transfer to, and optional network and security group IDs.
    * @return VirtualMachine or null if not found
    */
   @Named("assignVirtualMachine")
   @GET
   @QueryParams(keys = "command", values = "assignVirtualMachine")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   VirtualMachine assignVirtualMachine(@QueryParam("virtualmachineid") String virtualMachineId,
                                                         AssignVirtualMachineOptions... options);

}
