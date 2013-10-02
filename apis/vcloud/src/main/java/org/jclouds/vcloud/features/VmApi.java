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
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.DEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.NETWORKCONNECTIONSECTION_XML;
import static org.jclouds.vcloud.VCloudMediaType.RASDITEM_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.UNDEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.VM_XML;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.binders.BindCPUCountToXmlPayload;
import org.jclouds.vcloud.binders.BindDeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindGuestCustomizationSectionToXmlPayload;
import org.jclouds.vcloud.binders.BindMemoryToXmlPayload;
import org.jclouds.vcloud.binders.BindNetworkConnectionSectionToXmlPayload;
import org.jclouds.vcloud.binders.BindUndeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VmHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VM functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VmApi {

   @GET
   @Consumes(VM_XML)
   @XMLResponseParser(VmHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Vm getVm(@EndpointParam URI href);

   /**
    * To deploy a vApp, the client makes a request to its action/deploy URL. Deploying a vApp
    * automatically deploys all of the virtual machines it contains. To deploy a virtual machine,
    * the client makes a request to its action/deploy URL.
    * <p/>
    * Deploying a Vm implicitly deploys the parent vApp if that vApp is not already deployed.
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   Task deployVm(@EndpointParam URI href);

   /**
    * like {@link #deploy(URI)}, except deploy transitions to power on state
    * 
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   Task deployAndPowerOnVm(@EndpointParam URI href);

   /**
    * Undeploying a vApp powers off or suspends any running virtual machines it contains, then frees
    * the resources reserved for the vApp and sets the vApp’s deploy attribute to a value of false
    * to indicate that it is not deployed.
    * <p/>
    * Undeploying a virtual machine powers off or suspends the virtual machine, then frees the
    * resources reserved for it and sets the its deploy attribute to a value of false to indicate
    * that it is not deployed. This operation has no effect on the containing vApp.
    * <h4>NOTE</h4>
    * Using this method will simply power off the vms. In order to save their state, use
    * {@link #undeployAndSaveStateOf}
    * 
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   Task undeployVm(@EndpointParam URI href);

   /**
    * like {@link #undeploy(URI)}, where the undeployed virtual machines are suspended and their
    * suspend state saved
    * 
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "saveState", values = "true")
   @XMLResponseParser(TaskHandler.class)
   Task undeployAndSaveStateOfVm(@EndpointParam URI href);

   /**
    * A powerOn request to a vApp URL powers on all of the virtual machines in the vApp, as
    * specified in the vApp’s StartupSection field.
    * <p/>
    * A powerOn request to a virtual machine URL powers on the specified virtual machine and forces
    * deployment of the parent vApp.
    * <p/>
    * <h4>NOTE</h4> A powerOn request to a vApp or virtual machine that is undeployed forces
    * deployment.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   Task powerOnVm(@EndpointParam URI href);

   /**
    * A powerOff request to a vApp URL powers off all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A powerOff request to a virtual machine URL powers off the specified virtual machine.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   Task powerOffVm(@EndpointParam URI href);

   /**
    * A shutdown request to a vApp URL shuts down all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A shutdown request to a virtual machine URL shuts down the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   @POST
   @Path("/power/action/shutdown")
   void shutdownVm(@EndpointParam URI href);

   /**
    * A reset request to a vApp URL resets all of the virtual machines in the vApp, as specified in
    * its StartupSection field.
    * <p/>
    * A reset request to a virtual machine URL resets the specified virtual machine.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   Task resetVm(@EndpointParam URI href);

   /**
    * A reboot request to a vApp URL reboots all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A reboot request to a virtual machine URL reboots the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4> Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   @POST
   @Path("/power/action/reboot")
   void rebootVm(@EndpointParam URI href);

   /**
    * A suspend request to a vApp URL suspends all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A suspend request to a virtual machine URL suspends the specified virtual machine.
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   Task suspendVm(@EndpointParam URI href);

   /**
    * Get a Screen Thumbnail for a Virtual Machine
    * 
    * @param href
    *           to snapshot
    */
   @GET
   @Path("/screen")
   @Consumes("image/png")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   InputStream getScreenThumbnailForVm(@EndpointParam URI vm);

   /**
    * Modify the Guest Customization Section of a Virtual Machine
    * 
    * @param href
    *           uri to modify
    * @param updated
    *           guestCustomizationSection
    * @return task in progress
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(GUESTCUSTOMIZATIONSECTION_XML)
   @Path("/guestCustomizationSection")
   @XMLResponseParser(TaskHandler.class)
   Task updateGuestCustomizationOfVm(
           @BinderParam(BindGuestCustomizationSectionToXmlPayload.class) GuestCustomizationSection guestCustomizationSection,
           @EndpointParam URI href);

   /**
    * Modify the Network Connection Section of a Virtual Machine
    * 
    * @param href
    *           uri to modify
    * @param updated
    *           networkConnectionSection
    * @return task in progress
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(NETWORKCONNECTIONSECTION_XML)
   @Path("/networkConnectionSection")
   @XMLResponseParser(TaskHandler.class)
   Task updateNetworkConnectionOfVm(
           @BinderParam(BindNetworkConnectionSectionToXmlPayload.class) NetworkConnectionSection networkConnectionSection,
           @EndpointParam URI href);

   /**
    * update the cpuCount of an existing VM
    * 
    * @param href
    *           to update
    * @param cpuCount
    *           count to change the primary cpu to
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(RASDITEM_XML)
   @Path("/virtualHardwareSection/cpu")
   @XMLResponseParser(TaskHandler.class)
   Task updateCPUCountOfVm(@BinderParam(BindCPUCountToXmlPayload.class) int cpuCount,
                                             @EndpointParam URI href);

   /**
    * update the memoryInMB of an existing VM
    * 
    * @param href
    *           to update
    * @param memoryInMB
    *           memory in MB to assign to the VM
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(RASDITEM_XML)
   @Path("/virtualHardwareSection/memory")
   @XMLResponseParser(TaskHandler.class)
   Task updateMemoryMBOfVm(@BinderParam(BindMemoryToXmlPayload.class) int memoryInMB,
                                             @EndpointParam URI href);
}
