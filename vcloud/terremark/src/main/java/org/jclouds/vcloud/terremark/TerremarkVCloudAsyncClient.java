/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import java.net.InetAddress;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.InetAddressToHostAddress;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.CatalogIdToUri;
import org.jclouds.vcloud.functions.VAppId;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.binders.BindInternetServiceConfigurationToXmlPayload;
import org.jclouds.vcloud.terremark.binders.BindNodeConfigurationToXmlPayload;
import org.jclouds.vcloud.terremark.binders.BindVAppConfigurationToXmlPayload;
import org.jclouds.vcloud.terremark.binders.TerremarkBindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.terremark.domain.ComputeOptions;
import org.jclouds.vcloud.terremark.domain.CustomizationParameters;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.InternetServiceConfiguration;
import org.jclouds.vcloud.terremark.domain.IpAddress;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.VAppConfiguration;
import org.jclouds.vcloud.terremark.functions.ParseTaskFromLocationHeader;
import org.jclouds.vcloud.terremark.functions.ReturnVoidOnDeleteDefaultIp;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.xml.ComputeOptionsHandler;
import org.jclouds.vcloud.terremark.xml.CustomizationParametersHandler;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;
import org.jclouds.vcloud.terremark.xml.IpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
import org.jclouds.vcloud.terremark.xml.NodesHandler;
import org.jclouds.vcloud.terremark.xml.PublicIpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.VAppHandler;

import com.google.common.util.concurrent.ListenableFuture;

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
   @Override
   ListenableFuture<? extends VDC> getDefaultVDC();

   /**
    * Terremark does not have multiple catalogs, so we ignore this parameter.
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @XMLResponseParser(CatalogHandler.class)
   @Consumes(CATALOG_XML)
   ListenableFuture<? extends Catalog> getCatalog(String catalogId);

   /**
    * @see TerremarkVCloudClient#getVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}")
   @XMLResponseParser(TerremarkVDCHandler.class)
   @Consumes(VDC_XML)
   @Override
   ListenableFuture<? extends VDC> getVDC(@PathParam("vDCId") String vDCId);

   /**
    * @see TerremarkVCloudClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @MapBinder(TerremarkBindInstantiateVAppTemplateParamsToXmlPayload.class)
   @Override
   ListenableFuture<? extends VApp> instantiateVAppTemplateInVDC(
            @PathParam("vDCId") String vDCId, @MapPayloadParam("name") String appName,
            @MapPayloadParam("template") @ParamParser(CatalogIdToUri.class) String templateId,
            InstantiateVAppTemplateOptions... options);

   /**
    * @see TerremarkVCloudClient#addInternetService
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/internetServices")
   @Produces(APPLICATION_XML)
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   ListenableFuture<? extends InternetService> addInternetServiceToVDC(
            @PathParam("vDCId") String vDCId, @MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#getAllInternetServices
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/internetServices")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   ListenableFuture<? extends SortedSet<InternetService>> getAllInternetServicesInVDC(
            @PathParam("vDCId") String vDCId);

   /**
    * @see TerremarkVCloudClient#addInternetServiceToExistingIp
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}/InternetServices")
   @Produces(APPLICATION_XML)
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   ListenableFuture<? extends InternetService> addInternetServiceToExistingIp(
            @PathParam("ipId") int existingIpId, @MapPayloadParam("name") String serviceName,
            @MapPayloadParam("protocol") Protocol protocol, @MapPayloadParam("port") int port,
            AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudClient#deletePublicIp
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}")
   @ExceptionParser(ReturnVoidOnDeleteDefaultIp.class)
   ListenableFuture<Void> deletePublicIp(@PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getInternetServicesOnPublicIP
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}/InternetServices")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   ListenableFuture<? extends SortedSet<InternetService>> getInternetServicesOnPublicIp(
            @PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getPublicIp
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/publicIps/{ipId}")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   ListenableFuture<? extends SortedSet<InternetService>> getPublicIp(@PathParam("ipId") int ipId);

   /**
    * @see TerremarkVCloudClient#getPublicIpsAssociatedWithVDC
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vdc/{vDCId}/publicIps")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(PublicIpAddressesHandler.class)
   ListenableFuture<? extends SortedSet<PublicIpAddress>> getPublicIpsAssociatedWithVDC(
            @PathParam("vDCId") String vDCId);

   /**
    * @see TerremarkVCloudClient#deleteInternetService
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}")
   ListenableFuture<Void> deleteInternetService(
            @PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#configureInternetService
    */
   @PUT
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}")
   @Produces(APPLICATION_XML)
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   ListenableFuture<? extends InternetService> configureInternetService(
            @PathParam("internetServiceId") int internetServiceId,
            @BinderParam(BindInternetServiceConfigurationToXmlPayload.class) InternetServiceConfiguration nodeConfiguration);

   /**
    * @see TerremarkVCloudClient#getInternetService
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   ListenableFuture<? extends InternetService> getInternetService(
            @PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#addNode
    */
   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/internetServices/{internetServiceId}/nodes")
   @Produces(APPLICATION_XML)
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   ListenableFuture<? extends Node> addNode(
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
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends SortedSet<Node>> getNodes(
            @PathParam("internetServiceId") int internetServiceId);

   /**
    * @see TerremarkVCloudClient#getNode
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/nodeServices/{nodeId}")
   @XMLResponseParser(NodeHandler.class)
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends Node> getNode(@PathParam("nodeId") int nodeId);

   /**
    * @see TerremarkVCloudClient#configureNode
    */
   @PUT
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/nodeServices/{nodeId}")
   @Produces(APPLICATION_XML)
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(NodeHandler.class)
   ListenableFuture<? extends Node> configureNode(
            @PathParam("nodeId") int nodeId,
            @BinderParam(BindNodeConfigurationToXmlPayload.class) NodeConfiguration nodeConfiguration);

   /**
    * @see TerremarkVCloudClient#deleteNode
    */
   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/nodeServices/{nodeId}")
   ListenableFuture<Void> deleteNode(@PathParam("nodeId") int nodeId);

   /**
    * @see TerremarkVCloudClient#configureVApp
    */
   @PUT
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}")
   @Produces(VAPP_XML)
   @Consumes(VAPP_XML)
   @MapBinder(BindVAppConfigurationToXmlPayload.class)
   @ResponseParser(ParseTaskFromLocationHeader.class)
   ListenableFuture<? extends Task> configureVApp(
            @PathParam("vAppId") @ParamParser(VAppId.class) VApp vApp,
            VAppConfiguration configuration);

   /**
    * @see TerremarkVCloudClient#getComputeOptionsOfVApp
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}/options/compute")
   @XMLResponseParser(ComputeOptionsHandler.class)
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends SortedSet<ComputeOptions>> getComputeOptionsOfVApp(
            @PathParam("vAppId") String vAppId);

   /**
    * @see TerremarkVCloudClient#getCustomizationOptionsOfVApp
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/vapp/{vAppId}/options/customization")
   @XMLResponseParser(CustomizationParametersHandler.class)
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends CustomizationParameters> getCustomizationOptionsOfVApp(
            @PathParam("vAppId") String vAppId);

   /**
    * @see TerremarkVCloudClient#getComputeOptionsOfCatalogItem
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/catalogItem/{catalogItemId}/options/compute")
   @XMLResponseParser(ComputeOptionsHandler.class)
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends SortedSet<ComputeOptions>> getComputeOptionsOfCatalogItem(
            @PathParam("catalogItemId") String catalogItemId);

   /**
    * @see TerremarkVCloudClient#getCustomizationOptionsOfCatalogItem
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/catalogItem/{catalogItemId}/options/customization")
   @XMLResponseParser(CustomizationParametersHandler.class)
   @Consumes(APPLICATION_XML)
   ListenableFuture<? extends CustomizationParameters> getCustomizationOptionsOfCatalogItem(
            @PathParam("catalogItemId") String catalogItemId);

   /**
    * @see TerremarkVCloudClient#getIpAddressesForNetwork
    */
   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   @Path("/network/{networkId}/ipAddresses")
   @Consumes(APPLICATION_XML)
   @XMLResponseParser(IpAddressesHandler.class)
   ListenableFuture<? extends SortedSet<IpAddress>> getIpAddressesForNetwork(
            @PathParam("networkId") String networkId);
}
