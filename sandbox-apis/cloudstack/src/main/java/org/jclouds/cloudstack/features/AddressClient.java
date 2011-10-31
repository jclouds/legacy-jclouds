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
package org.jclouds.cloudstack.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.options.AssociateIPAddressOptions;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudStack IPAddress features.
 * <p/>
 * 
 * @see IPAddressAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/TOC_User.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface AddressClient {
   /**
    * Lists IPAddresses
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return IPAddresses matching query, or empty set, if no IPAddresses are
    *         found
    */
   Set<PublicIPAddress> listPublicIPAddresses(ListPublicIPAddressesOptions... options);

   /**
    * get a specific IPAddress by id
    * 
    * @param id
    *           IPAddress to get
    * @return IPAddress or null if not found
    */
   PublicIPAddress getPublicIPAddress(long id);

   /**
    * Acquires and associates a public IP to an account.
    * 
    * @param zoneId
    *           the ID of the availability zone you want to acquire an public IP
    *           address from
    * @return IPAddress
    */
   AsyncCreateResponse associateIPAddressInZone(long zoneId, AssociateIPAddressOptions... options);

   /**
    * Disassociates an ip address from the account.
    * 
    * @param id
    *           the id of the public ip address to disassociate
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   void disassociateIPAddress(long id);
}
