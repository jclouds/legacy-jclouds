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

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindDeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindUndeployVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VApp functionality in vCloud
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppApi {
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameResourceEntityNameToEndpoint.class)
   VApp findVAppInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
                              @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String vAppName);

   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   Task copyVAppToVDCAndName(@PayloadParam("Source") URI sourceVApp,
                             @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
                             CloneVAppOptions... options);

   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @PayloadParams(keys = "IsSourceDelete", values = "true")
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   Task moveVAppToVDCAndRename(@PayloadParam("Source") URI sourceVApp,
                               @EndpointParam URI vdc, @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String newName,
                               CloneVAppOptions... options);

   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VApp getVApp(@EndpointParam URI href);

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
   Task deployVApp(@EndpointParam URI href);

   /**
    * like {@link #deployVApp(URI)}, except deploy transitions to power on state
    * 
    */
   @POST
   @Consumes(TASK_XML)
   @Produces(DEPLOYVAPPPARAMS_XML)
   @Path("/action/deploy")
   @MapBinder(BindDeployVAppParamsToXmlPayload.class)
   @PayloadParams(keys = "powerOn", values = "true")
   @XMLResponseParser(TaskHandler.class)
   Task deployAndPowerOnVApp(@EndpointParam URI href);

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
   Task undeployVApp(@EndpointParam URI href);

   /**
    * like {@link #undeployVApp(URI)}, where the undeployed virtual machines are suspended and their
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
   Task undeployAndSaveStateOfVApp(@EndpointParam URI href);

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
   Task powerOnVApp(@EndpointParam URI href);

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
   Task powerOffVApp(@EndpointParam URI href);

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
   void shutdownVApp(@EndpointParam URI href);

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
   Task resetVApp(@EndpointParam URI href);

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
   void rebootVApp(@EndpointParam URI href);

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
   Task suspendVApp(@EndpointParam URI href);

   /**
    * delete a vAppTemplate, vApp, or media image. You cannot delete an object if it is in use. Any
    * object that is being copied or moved is in use. Other criteria that determine whether an
    * object is in use depend on the object type.
    * <ul>
    * <li>A vApptemplate is in use if it is being instantiated. After instantiation is complete, the
    * template is no longer in use.</li>
    * <li>A vApp is in use if it is deployed.</li>
    * <li>A media image is in use if it is inserted in a Vm.</li>
    * </ul>
    * 
    * @param href
    *           href of the vApp
    * @return task of the operation in progress
    */
   @DELETE
   @Consumes(TASK_XML)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   @XMLResponseParser(TaskHandler.class)
   Task deleteVApp(@EndpointParam URI href);
}
