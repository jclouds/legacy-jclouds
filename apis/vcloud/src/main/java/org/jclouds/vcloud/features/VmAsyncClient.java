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

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
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
 * Provides access to Vm functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VmAsyncClient {

   /**
    * @see VmClient#getVm
    */
   @GET
   @Consumes(VM_XML)
   @XMLResponseParser(VmHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Vm> getVm(@EndpointParam URI href);

   /**
    * @see VmClient#deployVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deployVm(@EndpointParam URI href);

   /**
    * @see VmClient#deployAndPowerOnVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deployAndPowerOnVm(@EndpointParam URI href);

   /**
    * @see VmClient#undeployVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> undeployVm(@EndpointParam URI href);

   /**
    * @see VmClient#undeployAndSaveStateOfVm
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "saveState", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> undeployAndSaveStateOfVm(@EndpointParam URI href);

   /**
    * @see VmClient#powerOnVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> powerOnVm(@EndpointParam URI href);

   /**
    * @see VmClient#powerOffVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> powerOffVm(@EndpointParam URI href);

   /**
    * @see VmClient#shutdownVm
    */
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVm(@EndpointParam URI href);

   /**
    * @see VmClient#resetVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> resetVm(@EndpointParam URI href);

   /**
    * @see VmClient#rebootVm
    */
   @POST
   @Path("/power/action/reboot")
   ListenableFuture<Void> rebootVm(@EndpointParam URI href);

   /**
    * @see VmClient#suspendVm
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> suspendVm(@EndpointParam URI href);

   /**
    * @see VmClient#updateCPUCountOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(RASDITEM_XML)
   @Path("/virtualHardwareSection/cpu")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> updateCPUCountOfVm(@BinderParam(BindCPUCountToXmlPayload.class) int cpuCount,
            @EndpointParam URI href);

   /**
    * @see VmClient#updateMemoryMBOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(RASDITEM_XML)
   @Path("/virtualHardwareSection/memory")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> updateMemoryMBOfVm(@BinderParam(BindMemoryToXmlPayload.class) int memoryInMB,
            @EndpointParam URI href);

   /**
    * @see VmClient#updateGuestCustomizationOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(GUESTCUSTOMIZATIONSECTION_XML)
   @Path("/guestCustomizationSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> updateGuestCustomizationOfVm(
            @BinderParam(BindGuestCustomizationSectionToXmlPayload.class) GuestCustomizationSection guestCustomizationSection,
            @EndpointParam URI href);

   /**
    * @see VmClient#updateNetworkConnectionOfVm
    */
   @PUT
   @Consumes(TASK_XML)
   @Produces(NETWORKCONNECTIONSECTION_XML)
   @Path("/networkConnectionSection")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> updateNetworkConnectionOfVm(
            @BinderParam(BindNetworkConnectionSectionToXmlPayload.class) NetworkConnectionSection networkConnectionSection,
            @EndpointParam URI href);

   /**
    * 
    * @see VmClient#getScreenThumbnailForVm
    */
   @GET
   @Path("/screen")
   @Consumes("image/png")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getScreenThumbnailForVm(@EndpointParam URI vm);
}
