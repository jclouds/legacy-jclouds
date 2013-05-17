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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(TerremarkVCloudExpressClient.class)} as
 *             {@link TerremarkVCloudExpressAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters(SetVCloudTokenCookie.class)
public interface TerremarkVCloudExpressAsyncClient extends TerremarkVCloudAsyncClient {
   /**
    * @see TerremarkVCloudExpressClient#addInternetServiceToVDC
    */
   @POST
   @Produces(INTERNETSERVICE_XML)
   @Consumes(INTERNETSERVICE_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   ListenableFuture<? extends InternetService> addInternetServiceToVDC(
            @EndpointParam(parser = VDCURIToInternetServicesEndpoint.class) URI vDCId,
            @PayloadParam("name") String serviceName, @PayloadParam("protocol") Protocol protocol,
            @PayloadParam("port") int port, AddInternetServiceOptions... options);

   /**
    * @see TerremarkVCloudExpressClient#findKeyPairInOrgNamed
    */
   @GET
   @XMLResponseParser(KeyPairByNameHandler.class)
   @Consumes(KEYSLIST_XML)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends KeyPair> findKeyPairInOrg(
            @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, String keyName);

   /**
    * @see TerremarkVCloudExpressClient#listKeyPairsInOrgNamed
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listKeyPairsInOrg(
            @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org);

   /**
    * @see TerremarkVCloudExpressClient#listKeyPairs
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listKeyPairs(@EndpointParam URI keysList);

   /**
    * @see TerremarkVCloudExpressClient#generateKeyPairInOrg
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
    * @see TerremarkVCloudExpressClient#getKeyPair
    */
   @GET
   @XMLResponseParser(KeyPairHandler.class)
   @Consumes(APPLICATION_XML)
   @Fallback(NullOnNotFoundOr404.class)
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
    * @see TerremarkVCloudExpressClient#deleteKeyPair
    */
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteKeyPair(@EndpointParam URI keyId);

}
