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
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.NETWORK_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.binders.OrgNameVDCNameNetworkNameToEndpoint;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.xml.OrgNetworkHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Network functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface NetworkAsyncClient {

   /**
    * @see NetworkClient#findNetworkInOrgVDCNamed
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(OrgNameVDCNameNetworkNameToEndpoint.class)
   ListenableFuture<OrgNetwork> findNetworkInOrgVDCNamed(@Nullable @PayloadParam("orgName") String orgName,
         @Nullable @PayloadParam("vdcName") String vdcName, @PayloadParam("resourceName") String networkName);

   /**
    * @see NetworkClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgNetwork> getNetwork(@EndpointParam URI network);

}
