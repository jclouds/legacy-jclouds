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
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapEntityParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.InetAddressToHostAddress;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.CatalogIdToUri;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
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

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(TerremarkVDCHandler.class)
   @Consumes(VDC_XML)
   Future<? extends VDC> getDefaultVDC();

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/action/instantiatevAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @XMLResponseParser(TerremarkVAppHandler.class)
   @MapBinder(InstantiateVAppTemplateOptions.class)
   Future<? extends VApp> instantiateVAppTemplate(@MapEntityParam("name") String appName,
            @MapEntityParam("template") @ParamParser(CatalogIdToUri.class) int templateId,
            InstantiateVAppTemplateOptions... options);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @Path("/internetServices")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   Future<? extends InternetService> addInternetService(@MapEntityParam("name") String serviceName,
            @MapEntityParam("protocol") String protocol, @MapEntityParam("port") int port,
            AddInternetServiceOptions... options);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/publicIps/{ipId}/InternetServices")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(InternetServiceHandler.class)
   @MapBinder(AddInternetServiceOptions.class)
   Future<? extends InternetService> addInternetServiceToExistingIp(
            @PathParam("ipId") int existingIpId, @MapEntityParam("name") String serviceName,
            @MapEntityParam("protocol") String protocol, @MapEntityParam("port") int port,
            AddInternetServiceOptions... options);

   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/internetServices/{internetServiceId}")
   Future<Void> deleteInternetService(@PathParam("internetServiceId") int internetServiceId);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/internetServices/{internetServiceId}")
   @XMLResponseParser(InternetServiceHandler.class)
   Future<? extends InternetService> getInternetService(
            @PathParam("internetServiceId") int internetServiceId);

   @POST
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/internetServices/{internetServiceId}/nodes")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(NodeHandler.class)
   @MapBinder(AddNodeOptions.class)
   Future<? extends Node> addNode(
            @PathParam("internetServiceId") int internetServiceId,
            @MapEntityParam("ipAddress") @ParamParser(InetAddressToHostAddress.class) InetAddress ipAddress,
            @MapEntityParam("name") String name, @MapEntityParam("port") int port,
            AddNodeOptions... options);

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/nodeServices/{nodeId}")
   @XMLResponseParser(NodeHandler.class)
   Future<? extends Node> getNode(@PathParam("nodeId") int nodeId);

   @DELETE
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/nodeServices/{nodeId}")
   Future<Void> deleteNode(@PathParam("nodeId") int nodeId);

   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/vapp/{vAppId}")
   @XMLResponseParser(TerremarkVAppHandler.class)
   Future<? extends VApp> getVApp(@PathParam("vAppId") int vAppId);

   @GET
   @Consumes(VAPP_XML)
   @Endpoint(org.jclouds.vcloud.endpoints.VCloud.class)
   @Path("/vapp/{vAppId}")
   Future<String> getVAppString(@PathParam("vAppId") int vAppId);
}
