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
package org.jclouds.fujitsu.fgcp.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Non-blocking API relating to a built-in server, also called extended function
 * module (EFM), of type load balancer (SLB).
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface LoadBalancerAsyncApi extends BuiltinServerAsyncApi {

   /*
   SLB_RULE,         getLBConfiguration(String id)
   SLB_LOAD_STATISTICS,   getLoadBalancerStats(String id)
   SLB_ERROR_STATISTICS,   getLoadBalancerErrorStats(String id)
   SLB_CERTIFICATE_LIST,   getLoadBalancerCerts(String id)
   SLB_CONNECTION,    getLoadBalancerConnection(String id)

    */
}
