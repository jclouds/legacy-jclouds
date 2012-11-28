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
import org.jclouds.vcloud.director.v1_5.functions.href.VAppURNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author grkvlt@apache.org
 * @see VAppApi
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppAsyncApi {

   /**
    * @see VAppApi#get(String)
    */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VApp> get(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#edit(String, VApp)
    */
   @PUT
   @Produces(VAPP)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) VApp vApp);

   /**
    * @see VAppApi#remove(String)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editControlAccess(String, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ListenableFuture<ControlAccessParams> editControlAccess(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see VAppApi#deploy(String, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deploy(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VAppApi#discardSuspendedState(String)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> discardSuspendedState(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#enterMaintenanceMode(String)
    */
   @POST
   @Path("/action/enterMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> enterMaintenanceMode(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#exitMaintenanceMode(String)
    */
   @POST
   @Path("/action/exitMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> exitMaintenanceMode(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#recompose(String, RecomposeVAppParams)
    */
   @POST
   @Path("/action/recomposeVApp")
   @Produces(RECOMPOSE_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> recompose(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) RecomposeVAppParams params);

   /**
    * @see VAppApi#undeploy(String, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> undeploy(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VAppApi#getAccessControl(String)
    */
   @GET
   @Path("/controlAccess")
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ControlAccessParams> getAccessControl(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#powerOff(String)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOff(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#powerOn(String)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOn(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#reboot(String)
    */
   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reboot(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#reset(String)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#shutdown(String)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> shutdown(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#suspend(String)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> suspend(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#getLeaseSettingsSection(String)
    */
   @GET
   @Path("/leaseSettingsSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(
            @EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editLeaseSettingsSection(String, LeaseSettingsSection)
    */
   @PUT
   @Path("/leaseSettingsSection")
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editLeaseSettingsSection(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) LeaseSettingsSection section);

   /**
    * @see VAppApi#getNetworkConfigSection(String)
    */
   @GET
   @Path("/networkConfigSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(
            @EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editNetworkConfigSection(String, NetworkConfigSection)
    */
   @PUT
   @Path("/networkConfigSection")
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editNetworkConfigSection(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppApi#getNetworkSection(String)
    */
   @GET
   @Path("/networkSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#getOwner(String)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editOwner(String, Owner)
    */
   @PUT
   @Path("/owner")
   @Produces(OWNER)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Void> editOwner(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) Owner owner);

   /**
    * @see VAppApi#getProductSections(String)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editProductSections(String, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * @see VAppApi#getStartupSection(String)
    */
   @GET
   @Path("/startupSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<StartupSection> getStartupSection(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   /**
    * @see VAppApi#editStartupSection(String, StartupSection)
    */
   @PUT
   @Path("/startupSection")
   @Produces(STARTUP_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editStartupSection(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn,
            @BinderParam(BindToXMLPayload.class) StartupSection section);

   /**
    * @see VAppApi#get(URI)
    */
   @GET
   @Consumes(VAPP)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VApp> get(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#edit(URI, VApp)
    */
   @PUT
   @Produces(VAPP)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI vAppHref, @BinderParam(BindToXMLPayload.class) VApp vApp);

   /**
    * @see VAppApi#remove(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editControlAccess(URI, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ListenableFuture<ControlAccessParams> editControlAccess(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see VAppApi#deploy(URI, DeployVAppParams)
    */
   @POST
   @Path("/action/deploy")
   @Produces(DEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deploy(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) DeployVAppParams params);

   /**
    * @see VAppApi#discardSuspendedState(URI)
    */
   @POST
   @Path("/action/discardSuspendedState")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> discardSuspendedState(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#enterMaintenanceMode(URI)
    */
   @POST
   @Path("/action/enterMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> enterMaintenanceMode(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#exitMaintenanceMode(URI)
    */
   @POST
   @Path("/action/exitMaintenanceMode")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> exitMaintenanceMode(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#recompose(URI, RecomposeVAppParams)
    */
   @POST
   @Path("/action/recomposeVApp")
   @Produces(RECOMPOSE_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> recompose(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) RecomposeVAppParams params);

   /**
    * @see VAppApi#undeploy(URI, UndeployVAppParams)
    */
   @POST
   @Path("/action/undeploy")
   @Produces(UNDEPLOY_VAPP_PARAMS)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> undeploy(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) UndeployVAppParams params);

   /**
    * @see VAppApi#getAccessControl(URI)
    */
   @GET
   @Path("/controlAccess")
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ControlAccessParams> getAccessControl(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#powerOff(URI)
    */
   @POST
   @Path("/power/action/powerOff")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOff(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#powerOn(URI)
    */
   @POST
   @Path("/power/action/powerOn")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> powerOn(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#reboot(URI)
    */
   @POST
   @Path("/power/action/reboot")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reboot(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#reset(URI)
    */
   @POST
   @Path("/power/action/reset")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#shutdown(URI)
    */
   @POST
   @Path("/power/action/shutdown")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> shutdown(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#suspend(URI)
    */
   @POST
   @Path("/power/action/suspend")
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> suspend(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#getLeaseSettingsSection(URI)
    */
   @GET
   @Path("/leaseSettingsSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editLeaseSettingsSection(URI, LeaseSettingsSection)
    */
   @PUT
   @Path("/leaseSettingsSection")
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editLeaseSettingsSection(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) LeaseSettingsSection section);

   /**
    * @see VAppApi#getNetworkConfigSection(URI)
    */
   @GET
   @Path("/networkConfigSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editNetworkConfigSection(URI, NetworkConfigSection)
    */
   @PUT
   @Path("/networkConfigSection")
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editNetworkConfigSection(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppApi#getNetworkSection(URI)
    */
   @GET
   @Path("/networkSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editOwner(URI, Owner)
    */
   @PUT
   @Path("/owner")
   @Produces(OWNER)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Void> editOwner(@EndpointParam URI vAppHref, @BinderParam(BindToXMLPayload.class) Owner owner);

   /**
    * @see VAppApi#getProductSections(URI)
    */
   @GET
   @Path("/productSections")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editProductSections(URI, ProductSectionList)
    */
   @PUT
   @Path("/productSections")
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sectionList);

   /**
    * @see VAppApi#getStartupSection(URI)
    */
   @GET
   @Path("/startupSection")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<StartupSection> getStartupSection(@EndpointParam URI vAppHref);

   /**
    * @see VAppApi#editStartupSection(URI, StartupSection)
    */
   @PUT
   @Path("/startupSection")
   @Produces(STARTUP_SECTION)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editStartupSection(@EndpointParam URI vAppHref,
            @BinderParam(BindToXMLPayload.class) StartupSection section);

   /**
    * Asynchronous access to {@link VApp} {@link Metadata} features
    */
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI vAppHref);

}
