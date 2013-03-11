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
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.RoundRobinPool;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.GuidHandler;
import org.jclouds.ultradns.ws.xml.RRPoolIDHandler;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;
import org.jclouds.ultradns.ws.xml.RoundRobinPoolListHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see RoundRobinPoolApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface RoundRobinPoolAsyncApi {

   /**
    * @see RoundRobinPoolApi#list()
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(RoundRobinPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>RR</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   ListenableFuture<FluentIterable<RoundRobinPool>> list() throws ResourceNotFoundException;

   /**
    * @see RoundRobinPoolApi#listRecords(String)
    */
   @Named("getRRPoolRecords")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getRRPoolRecords><lbPoolId>{poolId}</lbPoolId></v01:getRRPoolRecords>")
   ListenableFuture<FluentIterable<ResourceRecord>> listRecords(@PayloadParam("poolId") String poolId)
         throws ResourceNotFoundException;

   /**
    * @see RoundRobinPoolApi#createAPoolForHostname
    */
   @Named("addRRLBPool")
   @POST
   @XMLResponseParser(RRPoolIDHandler.class)
   @Payload("<v01:addRRLBPool><transactionID /><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><description>{description}</description><poolRecordType>1</poolRecordType><rrGUID /></v01:addRRLBPool>")
   ListenableFuture<String> createAPoolForHostname(@PayloadParam("description") String name,
         @PayloadParam("hostName") String hostname) throws ResourceAlreadyExistsException;

   /**
    * @see RoundRobinPoolApi#addARecordWithAddressAndTTL
    */
   @Named("addRecordToRRPool")
   @POST
   @XMLResponseParser(GuidHandler.class)
   @Payload("<v01:addRecordToRRPool><transactionID /><roundRobinRecord lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" ZoneName=\"{zoneName}\" Type=\"1\" TTL=\"{ttl}\"/></v01:addRecordToRRPool>")
   ListenableFuture<String> addARecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID,
         @PayloadParam("address") String ipv4Address, @PayloadParam("ttl") int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * @see RoundRobinPoolApi#updateRecordWithAddressAndTTL
    */
   @Named("updateRecordOfRRPool")
   @POST
   @Payload("<v01:updateRecordOfRRPool><transactionID /><resourceRecord rrGuid=\"{guid}\" lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" TTL=\"{ttl}\"/></v01:updateRecordOfRRPool>")
   ListenableFuture<Void> updateRecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID,
         @PayloadParam("guid") String guid, @PayloadParam("address") String ipv4Address,
         @PayloadParam("ttl") int ttl) throws ResourceNotFoundException;

   /**
    * @see RoundRobinPoolApi#deleteRecord(String)
    */
   @Named("deleteRecordOfRRPool")
   @POST
   @Payload("<v01:deleteRecordOfRRPool><transactionID /><guid>{guid}</guid></v01:deleteRecordOfRRPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteRecord(@PayloadParam("guid") String guid);

   /**
    * @see RoundRobinPoolApi#createAAAAPoolForHostname
    */
   @Named("addRRLBPool")
   @POST
   @XMLResponseParser(RRPoolIDHandler.class)
   @Payload("<v01:addRRLBPool><transactionID /><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><description>{description}</description><poolRecordType>28</poolRecordType><rrGUID /></v01:addRRLBPool>")
   ListenableFuture<String> createAAAAPoolForHostname(@PayloadParam("description") String name,
         @PayloadParam("hostName") String hostname) throws ResourceAlreadyExistsException;

   /**
    * @see RoundRobinPoolApi#addAAAARecordWithAddressAndTTL
    */
   @Named("addRecordToRRPool")
   @POST
   @XMLResponseParser(GuidHandler.class)
   @Payload("<v01:addRecordToRRPool><transactionID /><roundRobinRecord lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" ZoneName=\"{zoneName}\" Type=\"28\" TTL=\"{ttl}\"/></v01:addRecordToRRPool>")
   ListenableFuture<String> addAAAARecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID,
         @PayloadParam("address") String ipv6Address, @PayloadParam("ttl") int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * @see RoundRobinPoolApi#delete(String)
    */
   @Named("deleteLBPool")
   @POST
   @Payload("<v01:deleteLBPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll><retainRecordId /></v01:deleteLBPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PayloadParam("lbPoolID") String id);
}
