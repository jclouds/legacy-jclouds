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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RELOCATE_TEMPLATE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP_TEMPLATE;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.domain.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adam Lowe
 * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface VAppTemplateAsyncClient {

   /**
    * @see VAppTemplateClient#getVAppTemplate(URI)
    */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VAppTemplate> getVAppTemplate(@EndpointParam URI reference);


   /**
    * @see VAppTemplateClient#modifyVAppTemplate(URI, VAppTemplate)
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> modifyVAppTemplate(@EndpointParam URI templateURI,
                                             @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   /**
    * @see VAppTemplateClient#deleteVappTemplate(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deleteVappTemplate(@EndpointParam URI templateUri);

   /**
    * @see VAppTemplateClient#consolidateVm(URI)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/consolidate")
   @JAXBResponseParser
   ListenableFuture<Task> consolidateVm(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#disableDownload(URI)
    */
   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   ListenableFuture<Void> disableDownload(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#enableDownloadVappTemplate(URI)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   ListenableFuture<Task> enableDownload(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#relocateVm(URI, RelocateParams)
    */
   @POST
   @Produces(RELOCATE_TEMPLATE)
   @Consumes(TASK)
   @Path("/action/relocate")
   @JAXBResponseParser
   ListenableFuture<Task> relocateVm(@EndpointParam URI templateURI,
                                     @BinderParam(BindToXMLPayload.class) RelocateParams params);

   /**
    * @see VAppTemplateClient#getCustomizationSection(URI)
    */
   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<CustomizationSection> getCustomizationSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyCustomizationSection(URI, CustomizationSection)
    */
   @PUT
   @Produces(CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @Path("/customizationSection")
   @JAXBResponseParser
   ListenableFuture<Task> modifyCustomizationSection(@EndpointParam URI templateURI,
                                                     @BinderParam(BindToXMLPayload.class) CustomizationSection sectionType);

   /**
    * @see VAppTemplateClient#getGuestCustomizationSection(URI)
    */
   @GET
   @Consumes(GUEST_CUSTOMIZATION_SECTION)
   @Path("/guestCustomizationSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<GuestCustomizationSection> getGuestCustomizationSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyGuestCustomizationSection(URI, org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection)
    */
   @PUT
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @Path("/guestCustomizationSection")
   @JAXBResponseParser
   ListenableFuture<Task> modifyGuestCustomizationSection(@EndpointParam URI templateURI,
                                                          @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   /**
    * @see VAppTemplateClient#getLeaseSettingsSection(URI)
    */
   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyLeaseSettingsSection(URI, LeaseSettingsSection)
    */
   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   ListenableFuture<Task> modifyLeaseSettingsSection(@EndpointParam URI templateURI,
                                                     @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   /**
    * @see VAppTemplateClient#getNetworkConnectionSection(URI)
    */
   @GET
   @Consumes(NETWORK_CONNECTION_SECTION)
   @Path("/networkConnectionSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConnectionSection> getNetworkConnectionSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#getNetworkConfigSection(URI)
    */
   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyNetworkConfigSection(URI, NetworkConfigSection)
    */
   @PUT
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   ListenableFuture<Task> modifyNetworkConfigSection(@EndpointParam URI templateURI,
                                                     @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppTemplateClient#getNetworkConnectionSection(URI)
    */
   @GET
   @Consumes(NETWORK_CONNECTION_SECTION)
   @Path("/networkConnectionSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConnectionSection> getVAppTemplateNetworkConnectionSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyNetworkConnectionSection(URI, NetworkConnectionSection)
    */
   @PUT
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @Path("/networkConnectionSection")
   @JAXBResponseParser
   ListenableFuture<Task> modifyNetworkConnectionSection(@EndpointParam URI templateURI,
                                                         @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   /**
    * @see VAppTemplateClient#getNetworkSection(URI)
    */
   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#getOvf(URI)
    */
   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Envelope> getOvf(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#getOwnerOfVAppTemplate(URI)
    */
   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#getProductSectionsForVAppTemplate(URI)
    */
   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam URI templateURI);

   /**
    * @see VAppTemplateClient#modifyProductSections(URI, ProductSectionList)
    */
   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   ListenableFuture<Task> modifyProductSections(@EndpointParam URI templateURI,
                                                @BinderParam(BindToXMLPayload.class) ProductSectionList sections);
   
   /**
    * @see VAppTemplateClient#getShadowVms(URI)
    */
   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<References> getShadowVms(@EndpointParam URI templateURI);

   /**
    * @return asynchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataAsyncClient.Writeable getMetadataClient();
}
