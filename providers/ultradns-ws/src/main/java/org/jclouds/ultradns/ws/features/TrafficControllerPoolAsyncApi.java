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
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.IDHandler;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolListHandler;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolRecordListHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see TrafficControllerPoolApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface TrafficControllerPoolAsyncApi {

   /**
    * @see RoundRobinPoolApi#createPoolForHostname
    */
   @Named("addTCLBPool")
   @POST
   @XMLResponseParser(IDHandler.TCPool.class)
   @Payload("<v01:addTCLBPool><transactionID /><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><description>{description}</description><poolRecordType>1</poolRecordType><failOver>Enabled</failOver><probing>Enabled</probing><maxActive>0</maxActive><rrGUID /></v01:addTCLBPool>")
   ListenableFuture<String> createPoolForHostname(@PayloadParam("description") String name,
         @PayloadParam("hostName") String hostname) throws ResourceAlreadyExistsException;

   /**
    * @see TrafficControllerPoolApi#list()
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(TrafficControllerPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>TC</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   ListenableFuture<FluentIterable<TrafficControllerPool>> list() throws ResourceNotFoundException;

   /**
    * @see TrafficControllerPoolApi#listRecords(String)
    */
   @Named("getPoolRecords")
   @POST
   @XMLResponseParser(TrafficControllerPoolRecordListHandler.class)
   @Payload("<v01:getPoolRecords><poolId>{poolId}</poolId></v01:getPoolRecords>")
   ListenableFuture<FluentIterable<TrafficControllerPoolRecord>> listRecords(@PayloadParam("poolId") String poolId)
         throws ResourceNotFoundException;

   /**
    * @see TrafficControllerPoolApi#delete(String)
    */
   @Named("deleteLBPool")
   @POST
   @Payload("<v01:deleteLBPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll><retainRecordId /></v01:deleteLBPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PayloadParam("lbPoolID") String id);

   /**
    * @see TrafficControllerPoolApi#addRecordToPoolWithTTL
    */
   @Named("addPoolRecord")
   @POST
   @XMLResponseParser(IDHandler.PoolRecord.class)
   @Payload("<v01:addPoolRecord><transactionID /><poolID>{poolID}</poolID><pointsTo>{pointsTo}</pointsTo><priority /><failOverDelay /><ttl>{ttl}</ttl><weight /><mode /><threshold /></v01:addPoolRecord>")
   ListenableFuture<String> addRecordToPoolWithTTL(@PayloadParam("pointsTo") String pointsTo,
         @PayloadParam("poolID") String lbPoolID, @PayloadParam("ttl") int ttl) throws ResourceAlreadyExistsException;

   /**
    * @see TrafficControllerPoolApi#deleteRecord(String)
    */
   @Named("deletePoolRecord")
   @POST
   @Payload("<v01:deletePoolRecord><transactionID /><poolRecordID>{poolRecordID}</poolRecordID><parentPoolId /><childPoolId /></v01:deletePoolRecord>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteRecord(@PayloadParam("poolRecordID") String poolRecordID);

}
