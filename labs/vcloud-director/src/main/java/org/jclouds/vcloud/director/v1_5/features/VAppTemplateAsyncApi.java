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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PRODUCT_SECTION_LIST;
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
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.href.VAppTemplateURNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adam Lowe, Adrian Cole
 * @see VAppTemplateApi
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VAppTemplateAsyncApi {

   /**
    * @see VAppTemplateApi#get(String)
    */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VAppTemplate> get(@EndpointParam(parser = VAppTemplateURNToHref.class) String reference);

   /**
    * @see VAppTemplateApi#edit(String, VAppTemplate)
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn,
            @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   /**
    * @see VAppTemplateApi#remove(String)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam String templateUri);

   /**
    * @see VAppTemplateApi#disableDownload(String)
    */
   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   ListenableFuture<Void> disableDownload(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#enableDownload(String)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   ListenableFuture<Task> enableDownload(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getCustomizationSection(String)
    */
   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<CustomizationSection> getCustomizationSection(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getLeaseSettingsSection(String)
    */
   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#editLeaseSettingsSection(String, LeaseSettingsSection)
    */
   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   ListenableFuture<Task> editLeaseSettingsSection(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn,
            @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   /**
    * @see VAppTemplateApi#getNetworkConfigSection(String)
    */
   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getNetworkSection(String)
    */
   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getOvf(String)
    */
   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Envelope> getOvf(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getOwnerOfVAppTemplate(String)
    */
   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#getProductSectionsForVAppTemplate(String)
    */
   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(
            @EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#editProductSections(String, ProductSectionList)
    */
   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   /**
    * @see VAppTemplateApi#getShadowVms(String)
    */
   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<References> getShadowVms(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   /**
    * @see VAppTemplateApi#get(URI)
    */
   @GET
   @Consumes(VAPP_TEMPLATE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VAppTemplate> get(@EndpointParam URI reference);

   /**
    * @see VAppTemplateApi#edit(URI, VAppTemplate)
    */
   @PUT
   @Produces(VAPP_TEMPLATE)
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI templateHref,
            @BinderParam(BindToXMLPayload.class) VAppTemplate template);

   /**
    * @see VAppTemplateApi#remove(URI)
    */
   @DELETE
   @Consumes(TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@EndpointParam URI templateUri);

   /**
    * @see VAppTemplateApi#disableDownload(URI)
    */
   @POST
   @Path("/action/disableDownload")
   @JAXBResponseParser
   ListenableFuture<Void> disableDownload(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#enableDownload(URI)
    */
   @POST
   @Consumes(TASK)
   @Path("/action/enableDownload")
   @JAXBResponseParser
   ListenableFuture<Task> enableDownload(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getCustomizationSection(URI)
    */
   @GET
   @Consumes(CUSTOMIZATION_SECTION)
   @Path("/customizationSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<CustomizationSection> getCustomizationSection(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getLeaseSettingsSection(URI)
    */
   @GET
   @Consumes(LEASE_SETTINGS_SECTION)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LeaseSettingsSection> getLeaseSettingsSection(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#editLeaseSettingsSection(URI, LeaseSettingsSection)
    */
   @PUT
   @Produces(LEASE_SETTINGS_SECTION)
   @Consumes(TASK)
   @Path("/leaseSettingsSection")
   @JAXBResponseParser
   ListenableFuture<Task> editLeaseSettingsSection(@EndpointParam URI templateHref,
            @BinderParam(BindToXMLPayload.class) LeaseSettingsSection settingsSection);

   /**
    * @see VAppTemplateApi#getNetworkConfigSection(URI)
    */
   @GET
   @Consumes(NETWORK_CONFIG_SECTION)
   @Path("/networkConfigSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkConfigSection> getNetworkConfigSection(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getNetworkSection(URI)
    */
   @GET
   @Consumes(NETWORK_SECTION)
   @Path("/networkSection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkSection> getNetworkSection(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getOvf(URI)
    */
   @GET
   @Consumes
   @Path("/ovf")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Envelope> getOvf(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getOwnerOfVAppTemplate(URI)
    */
   @GET
   @Consumes(OWNER)
   @Path("/owner")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#getProductSectionsForVAppTemplate(URI)
    */
   @GET
   @Consumes(PRODUCT_SECTION_LIST)
   @Path("/productSections")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ProductSectionList> getProductSections(@EndpointParam URI templateHref);

   /**
    * @see VAppTemplateApi#editProductSections(URI, ProductSectionList)
    */
   @PUT
   @Produces(PRODUCT_SECTION_LIST)
   @Consumes(TASK)
   @Path("/productSections")
   @JAXBResponseParser
   ListenableFuture<Task> editProductSections(@EndpointParam URI templateHref,
            @BinderParam(BindToXMLPayload.class) ProductSectionList sections);

   /**
    * @see VAppTemplateApi#getShadowVms(URI)
    */
   @GET
   @Consumes
   @Path("/shadowVms")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<References> getShadowVms(@EndpointParam URI templateHref);

   /**
    * @return asynchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = VAppTemplateURNToHref.class) String templateUrn);

   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI templateHref);

}
