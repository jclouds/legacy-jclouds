/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8;

import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOGITEMCUSTOMIZATIONPARAMETERS_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.INTERNETSERVICESLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.INTERNETSERVICE_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.NODESERVICE_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.PUBLICIPSLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.PUBLICIP_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.CATALOGITEM_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.CATALOG_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.ORG_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.VAPP_XML;
import static org.jclouds.trmk.vcloud_0_8.VCloudMediaType.VDC_XML;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.trmk.vcloud_0_8.binders.BindNodeConfigurationToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.BindVAppConfigurationToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.TerremarkBindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CustomizationParameters;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkCatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkOrg;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkVDC;
import org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudExpressVApp;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameAndVDCNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameCatalogNameItemNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.ParseTaskFromLocationHeader;
import org.jclouds.trmk.vcloud_0_8.functions.ReturnVoidOnDeleteDefaultIp;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToInternetServicesEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToPublicIPsEndpoint;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.AddNodeOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.xml.CustomizationParametersHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServicesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodeHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.PublicIpAddressesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TerremarkCatalogItemHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TerremarkOrgHandler;
import org.jclouds.trmk.vcloud_0_8.xml.TerremarkVDCHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VCloudExpressCatalogHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VCloudExpressVAppHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkVCloudAsyncClient extends VCloudExpressAsyncClient {
   
   /**
    * @see VCloudExpressClient#getCatalogItemInOrg
    */
   @Override
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(TerremarkCatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TerremarkCatalogItem> findCatalogItemInOrgCatalogNamed(
            @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String catalogName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameItemNameToEndpoint.class) String itemName);

   /**
    * @see VCloudExpressClient#getCatalogItem
    */
   @Override
   @GET
   @Consumes(CATALOGITEM_XML)
   @XMLResponseParser(TerremarkCatalogItemHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TerremarkCatalogItem> getCatalogItem(@EndpointParam URI catalogItem);

   @Override
   @GET
   @XMLResponseParser(TerremarkOrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends TerremarkOrg> getOrg(@EndpointParam URI orgId);

   /**
    * @see VCloudExpressClient#findOrgNamed
    */
   @Override
   @GET
   @XMLResponseParser(TerremarkOrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends TerremarkOrg> findOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * Terremark does not have multiple catalogs, so we ignore this parameter.
    */
   @GET
   @Override
   @XMLResponseParser(VCloudExpressCatalogHandler.class)
   @Consumes(CATALOG_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Catalog> getCatalog(@EndpointParam URI catalogId);

   /**
    * @see TerremarkVCloudExpressClient#getVDC
    */
   @Override
   @GET
   @XMLResponseParser(TerremarkVDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends TerremarkVDC> getVDC(@EndpointParam URI vdc);

   /**
    * @see VCloudExpressClient#findVDCInOrgNamed
    */
   @GET
   @Override
   @XMLResponseParser(TerremarkVDCHandler.class)
   @Consumes(VDC_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VDC> findVDCInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameAndVDCNameToEndpoint.class) String vdcName);

   /**
    * @see VCloudExpressClient#instantiateVAppTemplateInVDC
    */
   @Override
   @POST
   @Path("/action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VCloudExpressVAppHandler.class)
   @MapBinder(TerremarkBindInstantiateVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<? extends VCloudExpressVApp> instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
            @PayloadParam("template") URI template,
            @PayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
            InstantiateVAppTemplateOptions... options);

   /**
    * @see TerremarkVCloudExpressClient#getAllInternetServicesInVDC
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getAllInternetServicesInVDC(
            @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId);

   /**
    * @see TerremarkVCloudExpressClient#addInternetServiceToExistingIp
    */
   @POST
   @Path("/internetServices")
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   ListenableFuture<? extends InternetService> addInternetServiceToExistingIp(@EndpointParam URI publicIpId,
            @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
            @PayloadParam("port") int port, AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudExpressClient#deletePublicIp
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnDeleteDefaultIp.class)
   ListenableFuture<Void> deletePublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkVCloudExpressClient#getInternetServicesOnPublicIP
    */
   @GET
   @Path("/internetServices")
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getInternetServicesOnPublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkVCloudExpressClient#getPublicIp
    */
   @GET
   @Consumes(PUBLICIP_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<InternetService>> getPublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkVCloudExpressClient#getPublicIpsAssociatedWithVDC
    */
   @GET
   @Consumes(PUBLICIPSLIST_XML)
   @XMLResponseParser(PublicIpAddressesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<PublicIpAddress>> getPublicIpsAssociatedWithVDC(
            @EndpointParam(parser = VDCURIToPublicIPsEndpoint.class) URI vDCId);

   /**
    * @see TerremarkVCloudExpressClient#deleteInternetService
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteInternetService(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkVCloudExpressClient#getInternetService
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends InternetService> getInternetService(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkVCloudExpressClient#addNode
    */
   @POST
   @Path("/nodeServices")
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   ListenableFuture<? extends Node> addNode(@EndpointParam URI internetServiceId,
            @PayloadParam("ipAddress") String ipAddress, @PayloadParam("name") String name,
            @PayloadParam("port") int port, AddNodeOptions... options);

   /**
    * @see TerremarkVCloudExpressClient#getNodes
    */
   @GET
   @Path("/nodeServices")
   @XMLResponseParser(NodesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Consumes(NODESERVICE_XML)
   ListenableFuture<? extends Set<Node>> getNodes(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkVCloudExpressClient#getNode
    */
   @GET
   @XMLResponseParser(NodeHandler.class)
   @Consumes(NODESERVICE_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Node> getNode(@EndpointParam URI nodeId);

   /**
    * @see TerremarkVCloudExpressClient#configureNode
    */
   @PUT
   @Produces(NODESERVICE_XML)
   @Consumes(NODESERVICE_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(BindNodeConfigurationToXmlPayload.class)
   ListenableFuture<? extends Node> configureNode(@EndpointParam URI nodeId, @PayloadParam("name") String name,
            @PayloadParam("enabled") boolean enabled, @Nullable @PayloadParam("description") String description);

   /**
    * @see TerremarkVCloudExpressClient#deleteNode
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteNode(@EndpointParam URI nodeId);

   /**
    * @see TerremarkVCloudExpressClient#configureVApp
    */
   @PUT
   @Produces(VAPP_XML)
   @Consumes(VAPP_XML)
   @MapBinder(BindVAppConfigurationToXmlPayload.class)
   @ResponseParser(ParseTaskFromLocationHeader.class)
   ListenableFuture<? extends Task> configureVApp(
            @EndpointParam(parser = BindVAppConfigurationToXmlPayload.class) VCloudExpressVApp vApp,
            VAppConfiguration configuration);

   /**
    * @see TerremarkVCloudClient#getCustomizationOptions
    */
   @GET
   @XMLResponseParser(CustomizationParametersHandler.class)
   @Consumes(CATALOGITEMCUSTOMIZATIONPARAMETERS_XML)
   ListenableFuture<? extends CustomizationParameters> getCustomizationOptions(@EndpointParam URI customization);

}
