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
package org.jclouds.vcloud.terremark;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.vcloud.terremark.TerremarkVCloudExpressMediaType.KEYSLIST_XML;
import static org.jclouds.vcloud.terremark.TerremarkVCloudMediaType.INTERNETSERVICE_XML;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.terremark.binders.BindCreateKeyToXmlPayload;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.functions.OrgURIToKeysListEndpoint;
import org.jclouds.vcloud.terremark.functions.VDCURIToInternetServicesEndpoint;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairByNameHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairsHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
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
            @MapPayloadParam("name") String serviceName, @MapPayloadParam("protocol") Protocol protocol,
            @MapPayloadParam("port") int port, AddInternetServiceOptions... options);

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
    * @see TerremarkVCloudExpressClient#listKeyPairsInOrgNamed
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listKeyPairsInOrg(
            @Nullable @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org);

   /**
    * @see TerremarkVCloudExpressClient#listKeyPairs
    */
   @GET
   @Consumes(KEYSLIST_XML)
   @XMLResponseParser(KeyPairsHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
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
            @EndpointParam(parser = OrgURIToKeysListEndpoint.class) URI org, @MapPayloadParam("name") String name,
            @MapPayloadParam("isDefault") boolean makeDefault);

   /**
    * @see TerremarkVCloudExpressClient#getKeyPair
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
    * @see TerremarkVCloudExpressClient#deleteKeyPair
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteKeyPair(@EndpointParam URI keyId);

}
