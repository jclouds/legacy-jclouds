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
package org.jclouds.trmk.vcloudexpress;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.INTERNETSERVICE_XML;
import static org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressMediaType.KEYSLIST_XML;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudApi;
import org.jclouds.trmk.vcloud_0_8.binders.BindCreateKeyToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.OrgURIToKeysListEndpoint;
import org.jclouds.trmk.vcloud_0_8.functions.VDCURIToInternetServicesEndpoint;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairByNameHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairsHandler;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkVCloudExpressApi extends TerremarkVCloudApi {

   @POST
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   InternetService addInternetServiceToVDC(
           @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId,
           @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
           @PayloadParam("port") int port, AddInternetServiceOptions... options);

   @GET
   @XMLResponseParser(KeyPairByNameHandler.class)
   @Consumes(KEYSLIST_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   KeyPair findKeyPairInOrg(
           @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, String keyName);

   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<KeyPair> listKeyPairsInOrg(
           @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org);

   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<KeyPair> listKeyPairs(@EndpointParam URI keysList);

   @POST
   @Produces(KEYSLIST_XML)
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairHandler.class)
   @MapBinder(BindCreateKeyToXmlPayload.class)
   KeyPair generateKeyPairInOrg(
           @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, @PayloadParam("name") String name,
           @PayloadParam("isDefault") boolean makeDefault);

   @GET
   @XMLResponseParser(KeyPairHandler.class)
   @Consumes(APPLICATION_XML)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   KeyPair getKeyPair(@EndpointParam URI keyId);

   @DELETE
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void deleteKeyPair(@EndpointParam URI keyId);
}
