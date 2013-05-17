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
package org.jclouds.ultradns.ws;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.features.DirectionalGroupApi;
import org.jclouds.ultradns.ws.features.DirectionalPoolApi;
import org.jclouds.ultradns.ws.features.ResourceRecordApi;
import org.jclouds.ultradns.ws.features.RoundRobinPoolApi;
import org.jclouds.ultradns.ws.features.TaskApi;
import org.jclouds.ultradns.ws.features.TrafficControllerPoolApi;
import org.jclouds.ultradns.ws.features.ZoneApi;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.AccountHandler;
import org.jclouds.ultradns.ws.xml.RegionListHandler;

import com.google.common.collect.Multimap;

/**
 * Provides access to Neustar UltraDNS via the SOAP API
 * <p/>
 * 
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface UltraDNSWSApi extends Closeable {
   /**
    * Returns the account of the current user.
    */
   @Named("getAccountsListOfUser")
   @POST
   @XMLResponseParser(AccountHandler.class)
   @Payload("<v01:getAccountsListOfUser/>")
   IdAndName getCurrentAccount();

   /**
    * Lists the directional regions available in the account.
    */
   @Named("getAvailableRegions")
   @POST
   @XMLResponseParser(RegionListHandler.class)
   @Payload("<v01:getAvailableRegions/>")
   Multimap<IdAndName, String> getRegionsByIdAndName();

   /**
    * Provides access to Zone features.
    */
   @Delegate
   ZoneApi getZoneApi();

   /**
    * Provides access to Resource Record features.
    * 
    * @param zoneName
    *           zoneName including a trailing dot
    */
   @Delegate
   ResourceRecordApi getResourceRecordApiForZone(@PayloadParam("zoneName") String zoneName);

   /**
    * Provides access to Round Robin Pool features.
    * 
    * @param zoneName
    *           zoneName including a trailing dot
    */
   @Delegate
   RoundRobinPoolApi getRoundRobinPoolApiForZone(@PayloadParam("zoneName") String zoneName);

   /**
    * Provides access to Traffic Controller Pool features.
    * 
    * @param zoneName
    *           zoneName including a trailing dot
    */
   @Delegate
   TrafficControllerPoolApi getTrafficControllerPoolApiForZone(@PayloadParam("zoneName") String zoneName);

   /**
    * Provides access to Account-Level Directional Group features.
    * 
    * @param accountId
    *           id of the account where the groups live.
    */
   @Delegate
   DirectionalGroupApi getDirectionalGroupApiForAccount(@PayloadParam("accountId") String accountId);

   /**
    * Provides access to Directional Pool features.
    * 
    * @param zoneName
    *           zoneName including a trailing dot
    */
   @Delegate
   DirectionalPoolApi getDirectionalPoolApiForZone(@PayloadParam("zoneName") String zoneName);

   /**
    * Provides access to Task features.
    */
   @Delegate
   TaskApi getTaskApi();
}
