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
package org.jclouds.ultradns.ws.features;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.jclouds.ultradns.ws.domain.RoundRobinPool;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.internal.RoundRobinPoolRecordTypeToString;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;
import org.jclouds.ultradns.ws.xml.RoundRobinPoolListHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface RoundRobinPoolApi {

   /**
    * Returns all round robin pools in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(RoundRobinPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>RR</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   FluentIterable<RoundRobinPool> list() throws ResourceNotFoundException;

   /**
    * Returns all records in the round robin pool.
    * 
    * @throws ResourceNotFoundException
    *            if the pool doesn't exist
    */
   @Named("getRRPoolRecords")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getRRPoolRecords><lbPoolId>{poolId}</lbPoolId></v01:getRRPoolRecords>")
   FluentIterable<ResourceRecordDetail> listRecords(@PayloadParam("poolId") String poolId)
         throws ResourceNotFoundException;

   /**
    * creates a round robin pool.
    * 
    * @param name
    *           {@link RoundRobinPool#getName() name} of the RR pool
    * @param dname
    *           {@link RoundRobinPool#getDName() dname} of the RR pool {ex.
    *           www.jclouds.org.}
    * @param rrType
    *           the {@link RoundRobinPool.RecordType record type} supported.
    * @return the {@code guid} of the new pool
    * @throws ResourceAlreadyExistsException
    *            if a pool already exists with the same attrs
    */
   @Named("addRRLBPool")
   @POST
   @XMLResponseParser(ElementTextHandler.RRPoolID.class)
   @Payload("<v01:addRRLBPool><transactionID /><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><description>{description}</description><poolRecordType>{poolRecordType}</poolRecordType><rrGUID /></v01:addRRLBPool>")
   String createForDNameAndType(@PayloadParam("description") String name, @PayloadParam("hostName") String dname,
         @PayloadParam("poolRecordType") @ParamParser(RoundRobinPoolRecordTypeToString.class) int rrType)
         throws ResourceAlreadyExistsException;

   /**
    * adds a new {@code A} record to the pool
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ipv4Address
    *           the ipv4 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addRecordToRRPool")
   @POST
   @XMLResponseParser(ElementTextHandler.Guid.class)
   @Payload("<v01:addRecordToRRPool><transactionID /><roundRobinRecord lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" ZoneName=\"{zoneName}\" Type=\"1\" TTL=\"{ttl}\"/></v01:addRecordToRRPool>")
   String addARecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID,
         @PayloadParam("address") String ipv4Address, @PayloadParam("ttl") int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * updates an existing A or AAAA record in the pool.
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    * @param address
    *           the ipv4 or ipv6 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * 
    * @throws ResourceNotFoundException
    *            if the guid doesn't exist
    */
   @Named("updateRecordOfRRPool")
   @POST
   @Payload("<v01:updateRecordOfRRPool><transactionID /><resourceRecord rrGuid=\"{guid}\" lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" TTL=\"{ttl}\"/></v01:updateRecordOfRRPool>")
   void updateRecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID, @PayloadParam("guid") String guid,
         @PayloadParam("address") String ipv4Address, @PayloadParam("ttl") int ttl) throws ResourceNotFoundException;

   /**
    * deletes a specific pooled resource record
    * 
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    */
   @Named("deleteRecordOfRRPool")
   @POST
   @Payload("<v01:deleteRecordOfRRPool><transactionID /><guid>{guid}</guid></v01:deleteRecordOfRRPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteRecord(@PayloadParam("guid") String guid);

   /**
    * adds a new {@code AAAA} record to the pool
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ipv6Address
    *           the ipv6 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addRecordToRRPool")
   @POST
   @XMLResponseParser(ElementTextHandler.Guid.class)
   @Payload("<v01:addRecordToRRPool><transactionID /><roundRobinRecord lbPoolID=\"{lbPoolID}\" info1Value=\"{address}\" ZoneName=\"{zoneName}\" Type=\"28\" TTL=\"{ttl}\"/></v01:addRecordToRRPool>")
   String addAAAARecordWithAddressAndTTL(@PayloadParam("lbPoolID") String lbPoolID,
         @PayloadParam("address") String ipv6Address, @PayloadParam("ttl") int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * removes a pool and all its records and probes
    * 
    * @param id
    *           the {@link RoundRobinPool#getId() id}
    */
   @Named("deleteLBPool")
   @POST
   @Payload("<v01:deleteLBPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll><retainRecordId /></v01:deleteLBPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PayloadParam("lbPoolID") String id);
}
