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
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.NETWORK_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
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
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<OrgNetwork> findNetworkInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String networkName);

   /**
    * @see NetworkClient#getNetwork
    */
   @GET
   @Consumes(NETWORK_XML)
   @XMLResponseParser(OrgNetworkHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<OrgNetwork> getNetwork(@EndpointParam URI network);

}
