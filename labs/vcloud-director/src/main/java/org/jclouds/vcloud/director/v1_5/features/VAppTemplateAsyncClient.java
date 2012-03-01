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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import com.google.common.util.concurrent.ListenableFuture;

import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.NetworkSection;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.*;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ReferenceToEndpoint;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import javax.ws.rs.*;

/**
 * @author Adam Lowe
 * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface VAppTemplateAsyncClient {

   /**
    * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient#getVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<VAppTemplate> getVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier reference);


   /**
    * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient#editVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.VAppTemplate)
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> editVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier reference,
                                           @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   /**
    * @see VAppTemplateClient#deleteVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> deleteVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#consolidateVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @POST
   @Consumes(TASK)
   @Path("/consolidate")
   @JAXBResponseParser
   ListenableFuture<Task> consolidateVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#disableDownloadVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/disableDownload")
   @JAXBResponseParser
   ListenableFuture<Task> disableDownloadVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#enableDownloadVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   ListenableFuture<Task> enableDownloadVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#relocateVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.RelocateParams)
    */
   @POST
   @Produces(RELOCATE_TEMPLATE)
   @Consumes(TASK)
   @Path("/action/relocate")
   @JAXBResponseParser
   ListenableFuture<Task> relocateVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                               @BinderParam(BindToXMLPayload.class) RelocateParams params);

   /**
    * @see VAppTemplateClient#getVAppTemplateCustomizationSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   CustomizationSection getVAppTemplateCustomizationSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editVAppTemplateCustomizationSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.CustomizationSection)
    */
   @PUT
   @Produces(CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @Path("/customizationSection")
   @JAXBResponseParser
   ListenableFuture<Task> editVAppTemplateCustomizationSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                               @BinderParam(BindToXMLPayload.class) CustomizationSection sectionType);

   /**
    * @see VAppTemplateClient#getVAppTemplateGuestCustomizationSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(GUEST_CUSTOMIZATION_SECTION)
   @Path("/guestCustomizationSection")
   @JAXBResponseParser
   GuestCustomizationSection getVAppTemplateGuestCustomizationSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editVAppTemplateGuestCustomizationSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection)
    */
   @PUT
   @Produces(GUEST_CUSTOMIZATION_SECTION)
   @Consumes(TASK)
   @Path("/guestCustomizationSection")
   @JAXBResponseParser
   Task editVAppTemplateGuestCustomizationSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                  @BinderParam(BindToXMLPayload.class) GuestCustomizationSection section);

   /**
    * @see VAppTemplateClient#getVappTemplateLeaseSettingsSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   LeaseSettingsSection getVappTemplateLeaseSettingsSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editVappTemplateLeaseSettingsSection(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection)
    */
   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   ListenableFuture<Task> editVappTemplateLeaseSettingsSection(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                               @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   /**
    * @see VAppTemplateClient#getMetadataForVappTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(METADATA)
   @Path("/metadata")
   @JAXBResponseParser
   ListenableFuture<Metadata> getMetadataForVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   @PUT
   @Produces(METADATA)
   @Consumes(TASK)
   @Path("/metadata")
   @JAXBResponseParser
   ListenableFuture<Task> editMetadataForVappTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                      @BinderParam(BindToXMLPayload.class) Metadata metadata);

   /**
    * @see VAppTemplateClient#editMetadataEntryForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, String, org.jclouds.vcloud.director.v1_5.domain.MetadataEntry)
    */
   @GET
   @Consumes(METADATA_ENTRY)
   @Path("/metadata/{key}")
   ListenableFuture<MetadataEntry> getMetadataEntryForVAppTemplateAndKey(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                                         @PathParam("key") String key);

   /**
    * @see VAppTemplateClient#editMetadataEntryForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, String, org.jclouds.vcloud.director.v1_5.domain.MetadataEntry)
    */
   @PUT
   @Produces(METADATA_ENTRY)
   @Consumes(TASK)
   @Path("/metadata/{key}")
   ListenableFuture<Task> editMetadataEntryForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                           @PathParam("key") String key,
                                                           @BinderParam(BindToXMLPayload.class) MetadataEntry entry);

   /**
    * @see VAppTemplateClient#deleteMetadataEntryForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, String)
    */
   @DELETE
   @Consumes(TASK)
   @Path("/metadata/{key}")
   ListenableFuture<Task> deleteMetadataEntryForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                             @PathParam("key") String key);

   /**
    * @see VAppTemplateClient#getNetworkConfigSectionForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   ListenableFuture<NetworkConfigSection> getNetworkConfigSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editNetworkConfigSectionForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection)
    */
   @PUT
   @Produces(NETWORK_CONFIG_SECTION)
   @Consumes(TASK)
   @Path("/networkConfigSection")
   ListenableFuture<Task> editNetworkConfigSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                                  @BinderParam(BindToXMLPayload.class) NetworkConfigSection section);

   /**
    * @see VAppTemplateClient#getNetworkConnectionSectionForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(NETWORK_CONNECTION_SECTION)
   @Path("/networkConnectionSection")
   ListenableFuture<NetworkConnectionSection> getNetworkConnectionSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editNetworkConnectionSectionForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection)
    */
   @PUT
   @Produces(NETWORK_CONNECTION_SECTION)
   @Consumes(TASK)
   @Path("/networkConnectionSection")
   ListenableFuture<Task> editNetworkConnectionSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                                      @BinderParam(BindToXMLPayload.class) NetworkConnectionSection section);

   /**
    * @see VAppTemplateClient#getNetworkSectionForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   ListenableFuture<NetworkSection> getNetworkSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   @PUT
   @Produces(NETWORK_SECTION)
   @Consumes(TASK)
   @Path("/networkSection")
   ListenableFuture<Task> editNetworkSectionForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                            @BinderParam(BindToXMLPayload.class) NetworkSection section);

   /**
    * @see VAppTemplateClient#getOvfForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(ENVELOPE)
   @Path("/ovf")
   ListenableFuture<Envelope> getOvfForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#getOwnerOfVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(OWNER)
   @Path("/owner")
   ListenableFuture<Owner> getOwnerOfVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#getProductSectionsForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier)
    */
   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   ListenableFuture<ProductSectionList> getProductSectionsForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference);

   /**
    * @see VAppTemplateClient#editProductSectionsForVAppTemplate(org.jclouds.vcloud.director.v1_5.domain.URISupplier, org.jclouds.vcloud.director.v1_5.domain.ProductSectionList)
    */
   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   ListenableFuture<Task> editProductSectionsForVAppTemplate(@EndpointParam(parser = ReferenceToEndpoint.class) URISupplier templateReference,
                                                             @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   // TODO shadowVms ?
}
