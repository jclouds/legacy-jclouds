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

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalRecordDetail;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.DirectionalPoolListHandler;
import org.jclouds.ultradns.ws.xml.DirectionalRecordDetailListHandler;

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
    * @param hostName
    *           fully qualified hostname including the trailing dot.
    * @param rrType
    *           type value, with special casing: for {@code A} or {@code CNAME}
    *           of ipv4 hosts, this is {@code 1}; for {@code AAAA} or
    *           {@code CNAME} of ipv4 hosts, this is {@code 28}
    * @return empty if there are not pools for the specified host or no records
    *         exist for the type.
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("getDirectionalDNSRecordsForHost")
   @POST
   @XMLResponseParser(DirectionalRecordDetailListHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Payload("<v01:getDirectionalDNSRecordsForHost><zoneName>{zoneName}</zoneName><hostName>{hostName}</hostName><poolRecordType>{poolRecordType}</poolRecordType></v01:getDirectionalDNSRecordsForHost>")
   FluentIterable<DirectionalRecordDetail> listRecordsByNameAndType(
         @PayloadParam("hostName") String dname, @PayloadParam("poolRecordType") int type)
         throws ResourceNotFoundException;
}
