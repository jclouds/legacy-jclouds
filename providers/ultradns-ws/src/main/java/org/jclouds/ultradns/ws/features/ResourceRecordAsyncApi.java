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
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.binders.ZoneAndResourceRecordToXML;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordMetadata;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see ResourceRecordApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface ResourceRecordAsyncApi {

   /**
    * @see ResourceRecordApi#create(ResourceRecordMetadata)
    */
   @Named("createResourceRecord")
   @POST
   @XMLResponseParser(ElementTextHandler.Guid.class)
   @MapBinder(ZoneAndResourceRecordToXML.class)
   ListenableFuture<String> create(@PayloadParam("resourceRecord") ResourceRecord toCreate)
         throws ResourceAlreadyExistsException;

   /**
    * @see ResourceRecordApi#update(String guid, BasicResourceRecord)
    */
   @Named("updateResourceRecord")
   @POST
   @MapBinder(ZoneAndResourceRecordToXML.class)
   ListenableFuture<Void> update(@PayloadParam("guid") String guid,
         @PayloadParam("resourceRecord") ResourceRecord toCreate) throws ResourceNotFoundException;

   /**
    * @see ResourceRecordApi#list()
    */
   @Named("getResourceRecordsOfZone")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfZone><zoneName>{zoneName}</zoneName><rrType>0</rrType></v01:getResourceRecordsOfZone>")
   ListenableFuture<FluentIterable<ResourceRecord>> list() throws ResourceNotFoundException;

   /**
    * @see ResourceRecordApi#listByName(String)
    */
   @Named("getResourceRecordsOfDNameByType")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfDNameByType><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><rrType>0</rrType></v01:getResourceRecordsOfDNameByType>")
   ListenableFuture<FluentIterable<ResourceRecordMetadata>> listByName(@PayloadParam("hostName") String hostName)
         throws ResourceNotFoundException;

   /**
    * @see ResourceRecordApi#listByNameAndType(String, int)
    */
   @Named("getResourceRecordsOfDNameByType")
   @POST
   @XMLResponseParser(ResourceRecordListHandler.class)
   @Payload("<v01:getResourceRecordsOfDNameByType><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><rrType>{rrType}</rrType></v01:getResourceRecordsOfDNameByType>")
   ListenableFuture<FluentIterable<ResourceRecordMetadata>> listByNameAndType(
         @PayloadParam("hostName") String hostName, @PayloadParam("rrType") int rrType)
         throws ResourceNotFoundException;

   /**
    * @see ResourceRecordApi#delete(String)
    */
   @Named("deleteResourceRecord")
   @POST
   @Payload("<v01:deleteResourceRecord><transactionID /><guid>{guid}</guid></v01:deleteResourceRecord>")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PayloadParam("guid") String guid);
}
