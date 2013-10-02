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
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ZoneListHandler;
import org.jclouds.ultradns.ws.xml.ZonePropertiesHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface ZoneApi {

   /**
    * creates a primary zone and its supporting records (SOA, NS and A). The
    * user who issues this request becomes the owner of this zone.
    * 
    * @param name
    *           the fully qualified name of the new zone.
    * @param accountId
    *           the account to create the zone in
    */
   @Named("createPrimaryZone")
   @POST
   @Payload("<v01:createPrimaryZone><transactionID /><accountId>{accountId}</accountId><zoneName>{zoneName}</zoneName><forceImport>false</forceImport></v01:createPrimaryZone>")
   void createInAccount(@PayloadParam("zoneName") String name, @PayloadParam("accountId") String accountId)
         throws ResourceAlreadyExistsException;

   /**
    * @param name
    *           the fully-qualified name, including the trailing dot, of the
    *           zone to get information about.
    * @return null if not found
    */
   @Named("getGeneralPropertiesForZone")
   @POST
   @XMLResponseParser(ZonePropertiesHandler.class)
   @Payload("<v01:getGeneralPropertiesForZone><zoneName>{zoneName}</zoneName></v01:getGeneralPropertiesForZone>")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ZoneProperties get(@PayloadParam("zoneName") String name);

   /**
    * Lists all zones in the specified account.
    * 
    * @returns empty if no zones, or account doesn't exist
    */
   @Named("getZonesOfAccount")
   @POST
   @XMLResponseParser(ZoneListHandler.class)
   @Payload("<v01:getZonesOfAccount><accountId>{accountId}</accountId><zoneType>all</zoneType></v01:getZonesOfAccount>")
   FluentIterable<Zone> listByAccount(@PayloadParam("accountId") String accountId);

   /**
    * Lists all zones in the specified account of type
    * 
    * @throws ResourceNotFoundException
    *            if the account doesn't exist
    */
   @Named("getZonesOfAccount")
   @POST
   @XMLResponseParser(ZoneListHandler.class)
   @Payload("<v01:getZonesOfAccount><accountId>{accountId}</accountId><zoneType>{zoneType}</zoneType></v01:getZonesOfAccount>")
   FluentIterable<Zone> listByAccountAndType(@PayloadParam("accountId") String accountId,
         @PayloadParam("zoneType") Type type) throws ResourceNotFoundException;

   /**
    * deletes a zone and all its resource records
    * 
    * @param name
    *           the fully-qualified name, including the trailing dot, of the
    *           zone you want to delete.
    * @return null if not found
    */
   @Named("deleteZone")
   @POST
   @Payload("<v01:deleteZone><transactionID /><zoneName>{zoneName}</zoneName></v01:deleteZone>")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PayloadParam("zoneName") String name);
}
