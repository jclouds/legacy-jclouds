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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToAdminHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminNetworkApi
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminNetworkAsyncApi extends NetworkAsyncApi {

   /**
    * @see AdminNetworkApi#get(String)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> get(@EndpointParam(parser = URNToAdminHref.class) String networkUrn);

   /**
    * @see AdminNetworkApi#get(URI)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> get(@EndpointParam URI networkAdminHref);

   /**
    * @see AdminNetworkApi#edit(String, OrgNetwork)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.ADMIN_ORG_NETWORK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam(parser = URNToAdminHref.class) String networkUrn,
            @BinderParam(BindToXMLPayload.class) OrgNetwork network);

   /**
    * @see AdminNetworkApi#edit(URI, OrgNetwork)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.ADMIN_ORG_NETWORK)
   @JAXBResponseParser
   ListenableFuture<Task> edit(@EndpointParam URI networkAdminHref,
            @BinderParam(BindToXMLPayload.class) OrgNetwork network);

   /**
    * @see AdminNetworkApi#reset(String)
    */
   @POST
   @Path("/action/reset")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam(parser = URNToAdminHref.class) String networkUrn);

   /**
    * @see AdminNetworkApi#reset(URI)
    */
   @POST
   @Path("/action/reset")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Task> reset(@EndpointParam URI networkAdminHref);
}
