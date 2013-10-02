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
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.binders.ZoneAndResourceRecordToXML;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface ResourceRecordApi {

   /**
    * creates a resource record in the zone.
    * 
    * @param toCreate
    *           the new record to create.
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   @Named("createResourceRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.Guid.class)
   @MapBinder(ZoneAndResourceRecordToXML.class)
   String create(@PayloadParam("resourceRecord") ResourceRecord toCreate)
         throws ResourceAlreadyExistsException;

   /**
    * updates an existing resource record in the zone.
    * 
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    * @param updated
    *           the record to update.
    * @throws ResourceNotFoundException
    *            if the guid doesn't exist
    */
   @Named("updateResourceRecord")
   @POST
   @MapBinder(ZoneAndResourceRecordToXML.class)
   void update(@PayloadParam("guid") String guid,
         @PayloadParam("resourceRecord") ResourceRecord toCreate) throws ResourceNotFoundException;

   /**
    * Returns all the specified record types in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getResourceRecordsOfZone")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfZone><zoneName>{zoneName}</zoneName><rrType>0</rrType></v01:getResourceRecordsOfZone>")
   FluentIterable<ResourceRecordDetail> list() throws ResourceNotFoundException;

   /**
    * Returns all the specified record types in the zone with the fully
    * qualified {@link hostName}
    * 
    * @param hostName
    *           fully qualified hostname including the trailing dot.
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getResourceRecordsOfDNameByType")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfDNameByType><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><rrType>0</rrType></v01:getResourceRecordsOfDNameByType>")
   FluentIterable<ResourceRecordDetail> listByName(@PayloadParam("hostName") String hostName)
         throws ResourceNotFoundException;

   /**
    * Returns all the specified record types in the zone with the fully
    * qualified {@link hostName} and {@link rrType}
    * 
    * @param hostName
    *           fully qualified hostname including the trailing dot.
    * @param rrType
    *           type value (ex. for {@code A}, this is {@code 1}
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getResourceRecordsOfDNameByType")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfDNameByType><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><rrType>{rrType}</rrType></v01:getResourceRecordsOfDNameByType>")
   FluentIterable<ResourceRecordDetail> listByNameAndType(
         @PayloadParam("hostName") String hostName, @PayloadParam("rrType") int rrType)
         throws ResourceNotFoundException;

   /**
    * deletes a specific resource record
    * 
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    */
   @Named("deleteResourceRecord")
   @POST
   @Payload("<v01:deleteResourceRecord><transactionID /><guid>{guid}</guid></v01:deleteResourceRecord>")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PayloadParam("guid") String guid);
}
