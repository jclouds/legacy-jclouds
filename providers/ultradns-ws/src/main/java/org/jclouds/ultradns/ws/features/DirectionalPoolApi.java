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

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.DirectionalGroupOverlapException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.binders.DirectionalRecordAndGeoGroupToXML;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.internal.DirectionalPoolRecordTypeToString;
import org.jclouds.ultradns.ws.xml.DirectionalPoolListHandler;
import org.jclouds.ultradns.ws.xml.DirectionalPoolRecordDetailListHandler;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface DirectionalPoolApi {

   /**
    * Returns all directional pools in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getDirectionalPoolsOfZone")
   @POST
   @XMLResponseParser(DirectionalPoolListHandler.class)
   @Payload("<v01:getDirectionalPoolsOfZone><zoneName>{zoneName}</zoneName></v01:getDirectionalPoolsOfZone>")
   FluentIterable<DirectionalPool> list() throws ResourceNotFoundException;

   /**
    * Returns all the directional pool records in the zone with the fully
    * qualified {@link hostName} and {@link rrType}
    * 
    * @param dname
    *           fully qualified hostname including the trailing dot.
    * @param rrType
    *           {@link RecordType type} value of the existing records.
    * @return empty if there are not pools for the specified host or no records
    *         exist for the type.
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    * @see RecordType#getCode()
    */
   @Named("getDirectionalDNSRecordsForHost")
   @POST
   @XMLResponseParser(DirectionalPoolRecordDetailListHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Payload("<v01:getDirectionalDNSRecordsForHost><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><poolRecordType>{poolRecordType}</poolRecordType></v01:getDirectionalDNSRecordsForHost>")
   FluentIterable<DirectionalPoolRecordDetail> listRecordsByDNameAndType(@PayloadParam("hostName") String dname,
         @PayloadParam("poolRecordType") int rrType) throws ResourceNotFoundException;

   /**
    * creates a directional pool for {@code A} and {@code CNAME} (ipv4) records
    * 
    * @param name
    *           {@link DirectionalPool#getName() description} of the Geo pool
    * @param dname
    *           {@link DirectionalPool#getDName() dname} of the Geo pool {ex.
    *           www.jclouds.org.}
    * @param rrType
    *           {@link RecordType type} value for the records added to this
    *           pool..
    * @return the {@link DirectionalPool#getId() id} of the new pool
    * @throws ResourceAlreadyExistsException
    *            if a pool already exists with the same attrs
    */
   @Named("addDirectionalPool")
   @POST
   @XMLResponseParser(ElementTextHandler.DirPoolID.class)
   @Payload("<v01:addDirectionalPool><transactionID /><AddDirectionalPoolData dirPoolType=\"GEOLOCATION\" poolRecordType=\"{poolRecordType}\" zoneName=\"{zoneName}\" hostName=\"{hostName}\" description=\"{description}\"/></v01:addDirectionalPool>")
   String createForDNameAndType(@PayloadParam("description") String name, @PayloadParam("hostName") String dname,
         @PayloadParam("poolRecordType") @ParamParser(DirectionalPoolRecordTypeToString.class) int rrType)
         throws ResourceAlreadyExistsException;

   /**
    * creates a resource record in the pool.
    * 
    * @param poolId
    *           pool to create the record in.
    * @param toCreate
    *           the new record to create.
    * @param group
    *           geo groups associated. Use the
    *           {@link UltraDNSWSApi#getRegionsByIdAndName()} to obtain the
    *           regionName and territoryNames. To specify all of a region’s
    *           territories, use
    *           {@link DirectionalGroup.Builder#mapRegion(String)}
    * @return the {@link DirectionalPoolRecordDetail#getId() id} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addDirectionalPoolRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.DirectionalPoolRecordID.class)
   @MapBinder(DirectionalRecordAndGeoGroupToXML.class)
   String addRecordIntoNewGroup(@PayloadParam("poolId") String poolId,
         @PayloadParam("record") DirectionalPoolRecord toCreate, @PayloadParam("group") DirectionalGroup group)
         throws ResourceAlreadyExistsException;

   /**
    * creates a resource record in the pool.
    * 
    * @param poolId
    *           pool to create the record in.
    * @param toCreate
    *           the new record to create.
    * @param groupId
    *           existing group from another record of the same dname and type.
    *           For example
    *           {@link DirectionalPoolRecordDetail#getGeolocationGroup()} or
    *           {@link DirectionalPoolRecordDetail#getGroup()}.
    * @return the {@link DirectionalPoolRecordDetail#getId() id} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addDirectionalPoolRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.DirectionalPoolRecordID.class)
   @MapBinder(DirectionalRecordAndGeoGroupToXML.class)
   String addRecordIntoExistingGroup(@PayloadParam("poolId") String poolId,
         @PayloadParam("record") DirectionalPoolRecord toCreate, @PayloadParam("groupId") String groupId)
         throws ResourceAlreadyExistsException;

   /**
    * creates a resource record in the pool, creating and assigning it to the
    * special "non configured group".
    * 
    * @param poolId
    *           pool to create the record in.
    * @param toCreate
    *           the new record to create.
    * @return the {@link DirectionalPoolRecordDetail#getId() id} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("addDirectionalPoolRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.DirectionalPoolRecordID.class)
   @MapBinder(DirectionalRecordAndGeoGroupToXML.class)
   String addFirstRecordInNonConfiguredGroup(@PayloadParam("poolId") String poolId,
         @PayloadParam("record") DirectionalPoolRecord toCreate) throws ResourceAlreadyExistsException;

   /**
    * updates such as ttl or rdata for an existing directional record.
    * 
    * @param recordId
    *           id of the record to update
    * @param update
    *           the updated record.
    * @throws ResourceNotFoundException
    *            if the record doesn't exist
    */
   @Named("updateDirectionalPoolRecord")
   @POST
   @MapBinder(DirectionalRecordAndGeoGroupToXML.class)
   void updateRecord(@PayloadParam("dirPoolRecordId") String recordId,
         @PayloadParam("record") DirectionalPoolRecord update) throws ResourceNotFoundException;

   /**
    * updates the geo groups of an existing directional record.
    * 
    * @param recordId
    *           id of the record to update
    * @param update
    *           the updated record.
    * @param group
    *           geo groups associated.
    * @throws ResourceNotFoundException
    *            if the record doesn't exist
    * @throws DirectionalGroupOverlapException
    *            if there's an overlap with another record in the pool. (ex.
    *            have the same territories)
    */
   @Named("updateDirectionalPoolRecord")
   @POST
   @MapBinder(DirectionalRecordAndGeoGroupToXML.class)
   void updateRecordAndGroup(@PayloadParam("dirPoolRecordId") String recordId,
         @PayloadParam("record") DirectionalPoolRecord update, @PayloadParam("group") DirectionalGroup group)
         throws ResourceNotFoundException, DirectionalGroupOverlapException;

   /**
    * deletes a specific directional pool record
    * 
    * @param id
    *           the {@link DirectionalPoolRecordDetail#getId() id} of the
    *           record.
    */
   @Named("deleteResourceRecord")
   @POST
   @Payload("<v01:deleteDirectionalPoolRecord><transactionID /><dirPoolRecordId>{dirPoolRecordId}</dirPoolRecordId></v01:deleteDirectionalPoolRecord>")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteRecord(@PayloadParam("dirPoolRecordId") String id);

   /**
    * removes a pool and all its records
    * 
    * @param id
    *           the {@link DirectionalPool#getId() id}
    */
   @Named("deleteDirectionalPool")
   @POST
   @Payload("<v01:deleteDirectionalPool><transactionID /><dirPoolID>{dirPoolID}</dirPoolID><retainRecordID /></v01:deleteDirectionalPool>")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PayloadParam("dirPoolID") String id);
}
