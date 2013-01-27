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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ZoneListHandler;
import org.jclouds.ultradns.ws.xml.ZonePropertiesHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see ZoneApi
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface ZoneAsyncApi {

   /**
    * @see ZoneApi#get(String)
    */
   @Named("getGeneralPropertiesForZone")
   @POST
   @XMLResponseParser(ZonePropertiesHandler.class)
   @Payload("<v01:getGeneralPropertiesForZone><zoneName>{zoneName}</zoneName></v01:getGeneralPropertiesForZone>")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ZoneProperties> get(@PayloadParam("zoneName") String name);

   /**
    * @see ZoneApi#listByAccount(String)
    */
   @Named("getZonesOfAccount")
   @POST
   @XMLResponseParser(ZoneListHandler.class)
   @Payload("<v01:getZonesOfAccount><accountId>{accountId}</accountId><zoneType>all</zoneType></v01:getZonesOfAccount>")
   ListenableFuture<FluentIterable<Zone>> listByAccount(@PayloadParam("accountId") String accountId)
         throws ResourceNotFoundException;

   /**
    * @see ZoneApi#listByAccountAndType(String, Type)
    */
   @Named("getZonesOfAccount")
   @POST
   @XMLResponseParser(ZoneListHandler.class)
   @Payload("<v01:getZonesOfAccount><accountId>{accountId}</accountId><zoneType>{zoneType}</zoneType></v01:getZonesOfAccount>")
   ListenableFuture<FluentIterable<Zone>> listByAccountAndType(@PayloadParam("accountId") String accountId,
         @PayloadParam("zoneType") Type type) throws ResourceNotFoundException;
}
