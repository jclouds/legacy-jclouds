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
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.UNDEPLOYVAPPPARAMS_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindDeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindUndeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VApp functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppAsyncClient {

   /**
    * @see VAppClient#copyVAppToVDCAndName
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<Task> copyVAppToVDCAndName(@PayloadParam("Source") URI sourceVApp,
            @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
            CloneVAppOptions... options);

   /**
    * @see VAppClient#moveVAppToVDCAndRename
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @PayloadParams(keys = "IsSourceDelete", values = "true")
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<Task> moveVAppToVDCAndRename(@PayloadParam("Source") URI sourceVApp,
            @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
            CloneVAppOptions... options);

   /**
    * @see VAppClient#findVAppInOrgVDCNamed
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VApp> findVAppInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String vAppName);

   /**
    * @see VAppClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VApp> getVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#deployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deployVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#deployAndPowerOnVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deployAndPowerOnVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#undeployVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> undeployVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#undeployAndSaveStateOfVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(UNDEPLOYVAPPPARAMS_XML)
   @Path("/action/undeploy")
   @MapBinder(BindUndeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "saveState", values = "true")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> undeployAndSaveStateOfVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#powerOnVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOn")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> powerOnVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#powerOffVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/powerOff")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> powerOffVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#shutdownVApp
    */
   @POST
   @Path("/power/action/shutdown")
   ListenableFuture<Void> shutdownVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#resetVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/reset")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> resetVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#rebootVApp
    */
   @POST
   @Path("/power/action/reboot")
   ListenableFuture<Void> rebootVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#suspendVApp
    */
   @POST
   @Consumes(TASK_XML)
   @Path("/power/action/suspend")
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> suspendVApp(@EndpointParam URI href);

   /**
    * @see VAppClient#deleteVApp
    */
   @DELETE
   @Consumes(TASK_XML)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   ListenableFuture<Task> deleteVApp(@EndpointParam URI href);

}
