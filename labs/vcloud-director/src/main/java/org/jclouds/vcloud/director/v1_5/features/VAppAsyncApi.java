/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CONTROL_ACCESS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RECOMPOSE_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.STARTUP_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author grkvlt@apache.org
 * @see VAppApi
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppAsyncApi {

   /**
    * @see VAppApi#getVApp(URI)
    */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VApp> getVApp(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editVApp(URI, VApp)
    */
   @PUT
   @Produces(VAPP)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVApp(@EndpointParam URI vAppURI,
                                     @BinderParam(BindToXMLPayload.class) VApp vApp);

   /**
    * @see VAppApi#removeVApp(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> removeVApp(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editControlAccess(URI, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ListenableFuture<ControlAccessParams> editControlAccess(@EndpointParam URI vAppURI,
                                                             @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see VAppApi#deploy(URI, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deploy(@EndpointParam URI vAppURI,
                                 @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VAppApi#discardSuspendedState(URI)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> discardSuspendedState(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#enterMaintenanceMode(URI)
    */
   @POST
   @Path("/action/enterMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> enterMaintenanceMode(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#exitMaintenanceMode(URI)
    */
   @POST
   @Path("/action/exitMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> exitMaintenanceMode(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#recompose(URI, RecomposeVAppParams)
    */
   @POST
   @Path("/action/recomposeVApp")
   @Produces(RECOMPOSE_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> recompose(@EndpointParam URI vAppURI,
                                    @BinderParam(BindToXMLPayload.class) RecomposeVAppParams params);

   /**
    * @see VAppApi#undeploy(URI, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> undeploy(@EndpointParam URI vAppURI,
                                   @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VAppApi#getControlAccess(URI)
    */
   @GET
   @Path("/controlAccess")
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ControlAccessParams> getControlAccess(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#powerOff(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOff(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#powerOn(URI)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOn(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#reboot(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reboot(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#reset(URI)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#shutdown(URI)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> shutdown(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#suspend(URI)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> suspend(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#getLeaseSettingsSection(URI)
    */
   @GET
   @Path("/leaseSettingsSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editLeaseSettingsSection(URI, LeaseSettingsSection)
    */
   @PUT
   @Path("/leaseSettingsSection")
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editLeaseSettingsSection(@EndpointParam URI vAppURI,
                                                     @BinderParam(BindToXMLPayload.class) LeaseSettingsSection section);

   /**
    * @see VAppApi#getNetworkConfigSection(URI)
    */
   @GET
   @Path("/networkConfigSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editNetworkConfigSection(URI, NetworkConfigSection)
    */
   @PUT
   @Path("/networkConfigSection")
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editNetworkConfigSection(@EndpointParam URI vAppURI,
                                                     @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppApi#getNetworkSection(URI)
    */
   @GET
   @Path("/networkSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editOwner(URI, Owner)
    */
   @PUT
   @Path("/owner")
   @Produces(OWNER)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Void> editOwner(@EndpointParam URI vAppURI,
                                      @BinderParam(BindToXMLPayload.class) Owner owner);

   /**
    * @see VAppApi#getProductSections(URI)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editProductSections(URI, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam URI vAppURI,
                                                @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);


   /**
    * @see VAppApi#getStartupSection(URI)
    */
   @GET
   @Path("/startupSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<StartupSection> getStartupSection(@EndpointParam URI vAppURI);

   /**
    * @see VAppApi#editStartupSection(URI, StartupSection)
    */
   @PUT
   @Path("/startupSection")
   @Produces(STARTUP_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editStartupSection(@EndpointParam URI vAppURI,
                                               @BinderParam(BindToXMLPayload.class) StartupSection section);

   /**
    * Asynchronous access to {@link VApp} {@link Metadata} features
    */
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi();
}
