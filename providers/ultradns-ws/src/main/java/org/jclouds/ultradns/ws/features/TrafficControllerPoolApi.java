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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.binders.UpdatePoolRecordToXML;
import org.jclouds.ultradns.ws.domain.PoolRecordSpec;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.UpdatePoolRecord;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.internal.TrafficControllerPoolRecordTypeToString;
import org.jclouds.ultradns.ws.xml.AttributeHandler;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;
import org.jclouds.ultradns.ws.xml.PoolRecordSpecHandler;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolListHandler;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolRecordDetailListHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface TrafficControllerPoolApi {

   /**
    * creates a traffic controller pool.
    * 
    * @param name
    *           {@link TrafficControllerPool#getName() name} of the TC pool
    * @param dname
    *           {@link TrafficControllerPool#getDName() dname} of the TC pool
    *           {ex. www.jclouds.org.}
    * @param rrType
    *           the {@link TrafficControllerPool.RecordType record type}
    *           supported.
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a pool already exists with the same attrs
    */
   @Named("addTCLBPool")
   @POST
   @XMLResponseParser(ElementTextHandler.TCPoolID.class)
   @Payload("<v01:addTCLBPool><transactionID /><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><description>{description}</description><poolRecordType>{poolRecordType}</poolRecordType><failOver>Enabled</failOver><probing>Enabled</probing><maxActive>0</maxActive><rrGUID /></v01:addTCLBPool>")
   String createForDNameAndType(@PayloadParam("description") String name, @PayloadParam("hostName") String dname,
         @PayloadParam("poolRecordType") @ParamParser(TrafficControllerPoolRecordTypeToString.class) int rrType)
         throws ResourceAlreadyExistsException;

   /**
    * Returns all traffic controller pools in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getLoadBalancingPoolsByZone")
   @POST
   @XMLResponseParser(TrafficControllerPoolListHandler.class)
   @Payload("<v01:getLoadBalancingPoolsByZone><zoneName>{zoneName}</zoneName><lbPoolType>TC</lbPoolType></v01:getLoadBalancingPoolsByZone>")
   FluentIterable<TrafficControllerPool> list() throws ResourceNotFoundException;

   /**
    * Returns all records in the traffic controller pool.
    * 
    * @throws ResourceNotFoundException
    *            if the pool doesn't exist
    */
   @Named("getPoolRecords")
   @POST
   @XMLResponseParser(TrafficControllerPoolRecordDetailListHandler.class)
   @Payload("<v01:getPoolRecords><poolId>{poolId}</poolId></v01:getPoolRecords>")
   FluentIterable<TrafficControllerPoolRecordDetail> listRecords(@PayloadParam("poolId") String poolId)
         throws ResourceNotFoundException;

   /**
    * Retrieves the name of the specified pool by dname.
    * 
    * @param dname
    *           {@see TrafficControllerPool#getDName()} ex. {@code jclouds.org.}
    * @return null if not found
    */
   @Nullable
   @Named("getPoolForPoolHostName>")
   @POST
   @Payload("<v01:getPoolForPoolHostName><hostName>{hostName}</hostName></v01:getPoolForPoolHostName>")
   @XMLResponseParser(AttributeHandler.PoolName.class)
   @Fallback(NullOnNotFoundOr404.class)
   String getNameByDName(@PayloadParam("hostName") String dname);

   /**
    * removes a pool and all its records and probes
    * 
    * @param id
    *           the {@link TrafficControllerPool#getId() id}
    */
   @Named("deleteLBPool")
   @POST
   @Payload("<v01:deleteLBPool><transactionID /><lbPoolID>{lbPoolID}</lbPoolID><DeleteAll>Yes</DeleteAll><retainRecordId /></v01:deleteLBPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PayloadParam("lbPoolID") String id);

   /**
    * adds a new record to the pool with default weight.
    * 
    * @param rdata
    *           the ipv4 address or hostname
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ttl
    *           the {@link PoolRecordSpec#getTTL ttl} of the record
    * @return the {@link TrafficControllerPoolRecordDetail#getId() id} of the new
    *         record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addPoolRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.PoolRecordID.class)
   @Payload("<v01:addPoolRecord><transactionID /><poolID>{poolID}</poolID><pointsTo>{pointsTo}</pointsTo><priority /><failOverDelay /><ttl>{ttl}</ttl><weight /><mode /><threshold /></v01:addPoolRecord>")
   String addRecordToPoolWithTTL(@PayloadParam("pointsTo") String rdata, @PayloadParam("poolID") String lbPoolID,
         @PayloadParam("ttl") int ttl) throws ResourceAlreadyExistsException;

   /**
    * adds a new record to the pool with a specified weight.
    * 
    * @param rdata
    *           the ipv4 address or hostname
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ttl
    *           the {@link PoolRecordSpec#getTTL ttl} of the record
    * @param weight
    *           the {@link PoolRecordSpec#getWeight() weight} of the record
    * @return the {@link TrafficControllerPoolRecordDetail#getId() id} of the new
    *         record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addPoolRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.PoolRecordID.class)
   @Payload("<v01:addPoolRecord><transactionID /><poolID>{poolID}</poolID><pointsTo>{pointsTo}</pointsTo><priority /><failOverDelay /><ttl>{ttl}</ttl><weight>{weight}</weight><mode /><threshold /></v01:addPoolRecord>")
   String addRecordToPoolWithTTLAndWeight(@PayloadParam("pointsTo") String rdata,
         @PayloadParam("poolID") String lbPoolID, @PayloadParam("ttl") int ttl, @PayloadParam("weight") int weight)
         throws ResourceAlreadyExistsException;

   /**
    * @param poolRecordID
    *           {@link TrafficControllerPoolRecordDetail#getId()}
    * @return null if not found
    */
   @Named("getPoolRecordSpec>")
   @POST
   @Payload("<v01:getPoolRecordSpec><poolRecordId>{poolRecordId}</poolRecordId></v01:getPoolRecordSpec>")
   @XMLResponseParser(PoolRecordSpecHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   PoolRecordSpec getRecordSpec(@PayloadParam("poolRecordId") String poolRecordID);

   /**
    * This request updates an existing pool record.
    * 
    * @param poolRecordID
    *           {@link TrafficControllerPoolRecordDetail#getId()}
    * @param update
    *           what to update, usually primed via
    *           {@link UpdatePoolRecord#pointingTo(PoolRecordSpec, String)} or
    *           {@link org.jclouds.ultradns.ws.domain.UpdatePoolRecord.Builder#from(PoolRecordSpec)}
    * @throws ResourceNotFoundException
    *            if the record doesn't exist
    */
   @Named("updatePoolRecord>")
   @POST
   @MapBinder(UpdatePoolRecordToXML.class)
   void updateRecord(@PayloadParam("poolRecordID") String poolRecordID, @PayloadParam("update") UpdatePoolRecord update)
         throws ResourceNotFoundException;

   /**
    * deletes a specific pooled resource record
    * 
    * @param poolRecordID
    *           {@see TrafficControllerPoolRecord#getId()}
    */
   @Named("deletePoolRecord")
   @POST
   @Payload("<v01:deletePoolRecord><transactionID /><poolRecordID>{poolRecordID}</poolRecordID><parentPoolId /><childPoolId /></v01:deletePoolRecord>")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteRecord(@PayloadParam("poolRecordID") String poolRecordID);

}
