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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.ecloud.features.DataCenterOperationsApi;
import org.jclouds.trmk.ecloud.features.TagOperationsApi;
import org.jclouds.trmk.ecloud.xml.ECloudOrgHandler;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudApi;
import org.jclouds.trmk.vcloud_0_8.binders.BindCreateKeyToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.binders.OrgNameVDCNameNetworkNameToEndpoint;
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

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=645&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkECloudApi extends TerremarkVCloudApi {
   /**
    * Provides synchronous access to Data Center Operations.
    * 
    */
   @Delegate
   DataCenterOperationsApi getDataCenterOperationsApi();

   /**
    * Provides synchronous access to Data Center Operations.
    * 
    */
   @Delegate
   TagOperationsApi getTagOperationsApi();

   /**
    * {@inheritDoc}
    */
   @Override
   @GET
   @XMLResponseParser(ECloudOrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ECloudOrg getOrg(@EndpointParam URI orgId);

   @Override
   @GET
   @XMLResponseParser(ECloudOrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   ECloudOrg findOrgNamed(
           @Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Override
   Set<InternetService> getAllInternetServicesInVDC(
           @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId);

   /**
    * Allocate a new public IP
    * 
    * @param vDCId
    * @return
    * @throws org.jclouds.rest.InsufficientResourcesException
    *            if there's no additional ips available
    */
   @POST
   @Consumes(PUBLICIP_XML)
   @XMLResponseParser(PublicIpAddressHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   PublicIpAddress activatePublicIpInVDC(
           @EndpointParam(parser = VDCURIToPublicIPsEndpoint.class) URI vDCId);

   @POST
   @Path("/internetServices")
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   @Override
   InternetService addInternetServiceToExistingIp(@EndpointParam URI existingIpId,
                                                  @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
                                                  @PayloadParam("port") int port, AddInternetServiceOptions... options);

   @GET
   @Path("/internetServices")
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServicesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Override
   Set<InternetService> getInternetServicesOnPublicIp(@EndpointParam URI ipId);

   @GET
   @Consumes(INTERNETSERVICESLIST_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Override
   InternetService getInternetService(@EndpointParam URI internetServiceId);

   @Override
   @GET
   @XMLResponseParser(KeyPairByNameHandler.class)
   @Consumes(KEYSLIST_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   KeyPair findKeyPairInOrg(@Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, String keyName);

   @Override
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<KeyPair> listKeyPairsInOrg(@Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org);

   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<KeyPair> listKeyPairs(@EndpointParam URI keysList);

   @Override
   @POST
   @Produces(KEYSLIST_XML)
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairHandler.class)
   @MapBinder(BindCreateKeyToXmlPayload.class)
   KeyPair generateKeyPairInOrg(
           @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, @PayloadParam("name") String name,
           @PayloadParam("isDefault") boolean makeDefault);

   @Override
   @GET
   @XMLResponseParser(KeyPairHandler.class)
   @Consumes(APPLICATION_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   KeyPair getKeyPair(@EndpointParam URI keyId);

   @Override
   @DELETE
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteKeyPair(@EndpointParam URI keyId);

   @Override
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameNetworkNameToEndpoint.class)
   Network findNetworkInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
                                    @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String networkName);

   @Override
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(NetworkHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Network getNetwork(@EndpointParam URI network);

   @GET
   @XMLResponseParser(NetworkExtendedInfoHandler.class)
   @Consumes(APPLICATION_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   NetworkExtendedInfo getNetworkExtendedInfo(@EndpointParam URI network);

   @GET
   @Consumes(IPADDRESS_LIST_XML)
   @XMLResponseParser(IpAddressesHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<IpAddress> getIpAddresses(@EndpointParam URI network);

   /**
    * Returns extended information for the vApp.
    * 
    * @param vApp
    *           The URI at which the vApp information is available.
    * @return Extended vApp information like tags, long name, network adapter
    *         information.
    */
   @GET
   @Consumes(VAPPEXTINFO_XML)
   @XMLResponseParser(VAppExtendedInfoHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VAppExtendedInfo getVAppExtendedInfo(@EndpointParam URI href);
}
