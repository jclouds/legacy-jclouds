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
package org.jclouds.ultradns.ws.features;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.LBPool;
import org.jclouds.ultradns.ws.domain.LBPool.Type;
import org.jclouds.ultradns.ws.domain.PoolRecord;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.LBPoolListHandler;
import org.jclouds.ultradns.ws.xml.PoolRecordListHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see LBPoolApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface LBPoolAsyncApi {

   /**
    * @see LBPoolApi#list()
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(LBPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>all</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   ListenableFuture<FluentIterable<LBPool>> list() throws ResourceNotFoundException;

   /**
    * @see LBPoolApi#listRecords(String)
    */
   @Named("getPoolRecords")
   @POST
   @XMLResponseParser(PoolRecordListHandler.class)
   @Payload("<v01:getPoolRecords><poolId>{poolId}</poolId></v01:getPoolRecords>")
   ListenableFuture<FluentIterable<PoolRecord>> listRecords(@PayloadParam("poolId") String poolId) throws ResourceNotFoundException;

   /**
    * @see LBPoolApi#listByType(String)
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(LBPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>{type}</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   ListenableFuture<FluentIterable<LBPool>> listByType(@PayloadParam("type") Type type)
         throws ResourceNotFoundException;

   /**
    * @see LBPoolApi#delete(String)
    */
   @Named("deleteLBPool")
   @POST
   @Payload("<v01:deleteLBPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll></v01:deleteLBPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PayloadParam("lbPoolID") String id);
}
