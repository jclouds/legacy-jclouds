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
package org.jclouds.trmk.ecloud;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.INTERNETSERVICESLIST_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.INTERNETSERVICE_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.IPADDRESS_LIST_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.KEYSLIST_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.PUBLICIP_XML;
import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.VAPPEXTINFO_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.NETWORK_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.ORG_XML;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.ecloud.features.DataCenterOperationsAsyncClient;
import org.jclouds.trmk.ecloud.features.TagOperationsAsyncClient;
import org.jclouds.trmk.ecloud.xml.ECloudOrgHandler;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.binders.BindCreateKeyToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.IpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.VAppExtendedInfo;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameVDCNameNetworkNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.OrgURIToKeysListEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToInternetServicesEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToPublicIPsEndpoint;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServicesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.IpAddressesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairByNameHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairsHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NetworkExtendedInfoHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NetworkHandler;
import org.jclouds.trmk.vcloud_0_8.xml.PublicIpAddressHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppExtendedInfoHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to eCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=645&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkECloudAsyncClient extends TerremarkVCloudAsyncClient {
   /**
    * Provides asynchronous access to Data Center Operations.
    * 
    */
   @Delegate
   DataCenterOperationsAsyncClient getDataCenterOperationsClient();

   /**
    * Provides asynchronous access to Tag Operations.
    * 
    */
   @Delegate
   TagOperationsAsyncClient getTagOperationsClient();

   @Override
   @GET
   @XMLResponseParser(ECloudOrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends ECloudOrg> getOrg(@EndpointParam URI orgId);

   @Override
   @GET
   @XMLResponseParser(ECloudOrgHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ListenableFuture<? extends ECloudOrg> findOrgNamed(
         @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   /**
    * @see TerremarkVCloudExpressClient#getAllInternetServices
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends Set<InternetService>> getAllInternetServicesInVDC(
         @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId);

   /**
    * @see TerremarkVCloudExpressClient#activatePublicIpInVDC
    */
   @POST
   @Consumes(PUBLICIP_XML)
   @XMLResponseParser(PublicIpAddressHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<PublicIpAddress> activatePublicIpInVDC(
         @EndpointParam(parser = VDCURIToPublicIPsEndpoint.class) URI vDCId);

   /**
    * @see TerremarkVCloudExpressClient#addInternetServiceToExistingIp
    */
   @POST
   @Path("/internetServices")
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   @Override
   ListenableFuture<? extends InternetService> addInternetServiceToExistingIp(@EndpointParam URI existingIpId,
         @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
         @PayloadParam("port") int port, AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudExpressClient#getInternetServicesOnPublicIP
    */
   @GET
   @Path("/internetServices")
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends Set<InternetService>> getInternetServicesOnPublicIp(@EndpointParam URI ipId);

   /**
    * @see TerremarkVCloudExpressClient#getInternetService
    */
   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Override
   ListenableFuture<? extends InternetService> getInternetService(@EndpointParam URI internetServiceId);

   /**
    * @see TerremarkVCloudExpressClient#findKeyPairInOrgNamed
    */
   @GET
   @XMLResponseParser(KeyPairByNameHandler.class)
   @Consumes(KEYSLIST_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends KeyPair> findKeyPairInOrg(
         @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, String keyName);

   /**
    * @see TerremarkVCloudExpressClient#listKeyPairsInOrg
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listKeyPairsInOrg(
         @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org);

   /**
    * @see TerremarkECloudClient#listKeyPairs
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listKeyPairs(@EndpointParam URI keysList);

   /**
    * @see TerremarkECloudClient#generateKeyPairInOrg
    */
   @POST
   @Produces(KEYSLIST_XML)
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairHandler.class)
   @MapBinder(BindCreateKeyToXmlPayload.class)
   ListenableFuture<? extends KeyPair> generateKeyPairInOrg(
         @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, @PayloadParam("name") String name,
         @PayloadParam("isDefault") boolean makeDefault);

   /**
    * @see TerremarkECloudClient#getKeyPair
    */
   @GET
   @XMLResponseParser(KeyPairHandler.class)
   @Consumes(APPLICATION_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends KeyPair> getKeyPair(@EndpointParam URI keyId);

   // TODO
   // /**
   // * @see TerremarkVCloudClient#configureKeyPair
   // */
   // @PUT
   // @Endpoint(org.jclouds.vcloud.endpoints.VCloudApi.class)
   // @Path("/extensions/key/{keyId}")
   // @Produces(APPLICATION_XML)
   // @Consumes(APPLICATION_XML)
   // @XMLResponseParser(KeyPairHandler.class)
   // ListenableFuture<? extends KeyPair> configureKeyPair(
   // @PathParam("keyId") int keyId,
   // @BinderParam(BindKeyPairConfigurationToXmlPayload.class)
   // KeyPairConfiguration keyConfiguration);

   /**
    * @see TerremarkECloudClient#deleteKeyPair
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteKeyPair(@EndpointParam URI keyId);

   /**
    * @see TerremarkECloudClient#findNetworkInOrgVDCNamed
    */
   @Override
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> findNetworkInOrgVDCNamed(
         @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String orgName,
         @Nullable @EndpointParam(parser = OrgNameVDCNameNetworkNameToEndpoint.class) String catalogName,
         @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String networkName);

   /**
    * @see TerremarkECloudClient#getNetwork
    */
   @Override
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> getNetwork(@EndpointParam URI network);

   /**
    * @see TerremarkECloudClient#getNetworkExtendedInfo
    */
   @GET
   @XMLResponseParser(NetworkExtendedInfoHandler.class)
   @Consumes(APPLICATION_XML)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends NetworkExtendedInfo> getNetworkExtendedInfo(@EndpointParam URI network);

   /**
    * @see TerremarkECloudClient#getIpAddresses
    */
   @GET
   @Consumes(IPADDRESS_LIST_XML)
   @XMLResponseParser(IpAddressesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<IpAddress>> getIpAddresses(@EndpointParam URI network);

   /**
    * @see TerremarkVCloudExpressClient#getInternetService
    */
   @GET
   @Consumes(VAPPEXTINFO_XML)
   @XMLResponseParser(VAppExtendedInfoHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppExtendedInfo> getVAppExtendedInfo(@EndpointParam URI href);

}
