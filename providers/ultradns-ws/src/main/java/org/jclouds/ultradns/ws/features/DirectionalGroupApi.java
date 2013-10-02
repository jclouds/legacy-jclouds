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
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.binders.DirectionalGroupCoordinatesToXML;
import org.jclouds.ultradns.ws.domain.AccountLevelGroup;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalGroupCoordinates;
import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.AccountLevelGroupsHandler;
import org.jclouds.ultradns.ws.xml.DirectionalGroupHandler;
import org.jclouds.ultradns.ws.xml.DirectionalPoolRecordDetailListHandler;
import org.jclouds.ultradns.ws.xml.ItemListHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface DirectionalGroupApi {

   /**
    * returns the regions and name of the specified directional group or null,
    * if not found.
    * 
    * @param groupId
    *           the {@link DirectionalGroup#getId() id} of the group
    */
   @Named("getDirectionalDNSGroupDetails")
   @POST
   @XMLResponseParser(DirectionalGroupHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Payload("<v01:getDirectionalDNSGroupDetails><GroupId>{GroupId}</GroupId></v01:getDirectionalDNSGroupDetails>")
   @Nullable
   DirectionalGroup get(@PayloadParam("GroupId") String groupId);

   /**
    * Returns all account-level groups.
    * 
    */
   @Named("getAccountLevelDirectionalGroupsOfZone")
   @POST
   @XMLResponseParser(AccountLevelGroupsHandler.class)
   @Payload("<v01:getAccountLevelDirectionalGroups><accountId>{accountId}</accountId><GroupType /></v01:getAccountLevelDirectionalGroups>")
   FluentIterable<AccountLevelGroup> listAccountLevelGroups();

   /**
    * Returns all the directional pool records in the account-level group.
    * 
    * @param groupId
    *           the id of the account-level group containing the records.
    * @throws ResourceNotFoundException
    *            if the group doesn't exist
    */
   @Named("getDirectionalDNSRecordsForAcctLvlGroup")
   @POST
   @XMLResponseParser(DirectionalPoolRecordDetailListHandler.class)
   @Payload("<v01:getDirectionalDNSRecordsForAcctLvlGroup><groupId>{groupId}</groupId></v01:getDirectionalDNSRecordsForAcctLvlGroup>")
   FluentIterable<DirectionalPoolRecordDetail> listRecordsByAccountLevelGroup(
         @PayloadParam("groupId") String groupId) throws ResourceNotFoundException;

   /**
    * Returns directional group names visible to the account for the fully
    * qualified {@link hostName} and {@link rrType}
    * 
    * @param accountId
    *           the account where the groups exist
    * @param hostName
    *           fully qualified hostname including the trailing dot.
    * @param rrType
    *           {@link RecordType type} value of the existing records.
    * @return empty if there are not groups for the specified host and type
    */
   @Named("getAvailableGroups")
   @POST
   @XMLResponseParser(ItemListHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Payload("<v01:getAvailableGroups><poolName>{hostName}</poolName><poolRecordType>{rrType}</poolRecordType><accountID>{accountId}</accountID><groupType /></v01:getAvailableGroups>")
   FluentIterable<String> listGroupNamesByDNameAndType(
         @PayloadParam("hostName") String hostName, @PayloadParam("rrType") int rrType);

   /**
    * Returns all the directional pool records in the pool-level group.
    * 
    * @param group
    *           the zone, record name, record type, and group name
    * @throws ResourceNotFoundException
    *            if the group doesn't exist
    */
   @Named("getDirectionalDNSRecordsForGroup")
   @POST
   @XMLResponseParser(DirectionalPoolRecordDetailListHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<DirectionalPoolRecordDetail> listRecordsByGroupCoordinates(
         @BinderParam(DirectionalGroupCoordinatesToXML.class) DirectionalGroupCoordinates group)
         throws ResourceNotFoundException;
}
