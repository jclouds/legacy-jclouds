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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.ExternalNetwork;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminNetworkClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface AdminNetworkAsyncClient extends NetworkAsyncClient {
   
   /**
    * @see AdminNetworkClient#getNetwork(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   @Override
   ListenableFuture<ExternalNetwork> getNetwork(@EndpointParam URI networkRef);
   
   /**
    * @see AdminNetworkClient#updateNetwork(URI, OrgNetwork)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.ADMIN_ORG_NETWORK)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> updateNetwork(@EndpointParam URI networkRef, 
         @BinderParam(BindToXMLPayload.class) OrgNetwork network);
   
   /**
    * @see AdminNetworkClient#resetNetwork(URI)
    */
   @POST
   @Path("/action/reset")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<Task> resetNetwork(@EndpointParam URI networkRef);
}
