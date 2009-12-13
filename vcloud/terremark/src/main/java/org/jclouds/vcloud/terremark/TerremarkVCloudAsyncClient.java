/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import java.net.InetAddress;
import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.InetAddressToHostAddress;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.CatalogIdToUri;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.binders.TerremarkBindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.terremark.domain.ComputeOption;
import org.jclouds.vcloud.terremark.domain.CustomizationParameters;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.options.ReturnVoidOnDeleteDefaultIp;
import org.jclouds.vcloud.terremark.xml.ComputeOptionsHandler;
import org.jclouds.vcloud.terremark.xml.CustomizationParametersHandler;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
import org.jclouds.vcloud.terremark.xml.NodesHandler;
import org.jclouds.vcloud.terremark.xml.PublicIpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVAppHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkVCloudAsyncClient extends VCloudAsyncClient {
   /**
    * @see TerremarkVCloudClient#getDefaultVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(TerremarkVDCHandler.class)
   @Consumes(VDC_XML)
   Future<? extends VDC> getDefaultVDC();

   /**
    * @see TerremarkVCloudClient#instantiateVAppTemplate
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/action/instantiatevAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @XMLResponseParser(TerremarkVAppHandler.class)
   @MapBinder(TerremarkBindInstantiateVAppTemplateParamsToXmlPayload.class)
   @Override
   Future<? extends TerremarkVApp> instantiateVAppTemplate(@MapPayloadParam("name") String appName,
            @MapPayloadParam("template") @ParamParser(CatalogIdToUri.class) String templateId,
            InstantiateVAppTemplateOptions... options);

   /**
    * @see TerremarkVCloudClient#addInternetService
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/internetServices")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   Future<? extends InternetService> addInternetService(@MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#getAllInternetServices
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/internetServices")
   @XMLResponseParser(InternetServicesHandler.class)
   Future<? extends SortedSet<InternetService>> getAllInternetServices();

   /**
    * @see TerremarkVCloudClient#addInternetServiceToExistingIp
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}/InternetServices")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   Future<? extends InternetService> addInternetServiceToExistingIp(
            @PathParam("ipId") int existingIpId, @MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#deletePublicIp
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/PublicIps/{ipId}")
   @ExceptionParser(ReturnVoidOnDeleteDefaultIp.class)
   Future<Void> deletePublicIp(@PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getInternetServicesOnPublicIP
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}/InternetServices")
   @XMLResponseParser(InternetServicesHandler.class)
   Future<? extends SortedSet<InternetService>> getInternetServicesOnPublicIp(
            @PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getPublicIpsAssociatedWithVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/publicIps")
   @XMLResponseParser(PublicIpAddressesHandler.class)
   Future<? extends SortedSet<PublicIpAddress>> getPublicIpsAssociatedWithVDC();

   /**
    * @see TerremarkVCloudClient#deleteInternetService
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}")
   Future<Void> deleteInternetService(@PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#getInternetService
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}")
   @XMLResponseParser(InternetServiceHandler.class)
   Future<? extends InternetService> getInternetService(
            @PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#addNode
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}/nodes")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   Future<? extends Node> addNode(
            @PathParam("internetServiceId") int internetServiceId,
            @MapPayloadParam("ipAddress") @ParamParser(InetAddressToHostAddress.class) InetAddress ipAddress,
            @MapPayloadParam("name") String name, @MapPayloadParam("port") int port,
            AddNodeOptions... options);

   /**
    * @see TerremarkVCloudClient#getNodes
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}/nodes")
   @XMLResponseParser(NodesHandler.class)
   Future<? extends SortedSet<Node>> getNodes(@PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#getNode
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/nodeServices/{nodeId}")
   @XMLResponseParser(NodeHandler.class)
   Future<? extends Node> getNode(@PathParam("nodeId") int nodeId);

   /**
    * @see TerremarkVCloudClient#deleteNode
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/nodeServices/{nodeId}")
   Future<Void> deleteNode(@PathParam("nodeId") int nodeId);

   /**
    * @see TerremarkVCloudClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}")
   @XMLResponseParser(TerremarkVAppHandler.class)
   @Override
   Future<? extends TerremarkVApp> getVApp(@PathParam("vAppId") String vAppId);

   /**
    * @see TerremarkVCloudClient#getComputeOptions
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}/options/compute")
   @XMLResponseParser(ComputeOptionsHandler.class)
   Future<? extends SortedSet<ComputeOption>> getComputeOptions(@PathParam("vAppId") String vAppId);

   /**
    * @see TerremarkVCloudClient#getCustomizationOptions
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}/options/customization")
   @XMLResponseParser(CustomizationParametersHandler.class)
   Future<? extends CustomizationParameters> getCustomizationOptions(
            @PathParam("vAppId") String vAppId);
}
