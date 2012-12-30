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
import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.cloudstack.options.CreateVlanIPRangeOptions;
import org.jclouds.cloudstack.options.ListVlanIPRangesOptions;

/**
 * Provides synchronous access to CloudStack VLAN features.
 * <p/>
 *
 * @see GlobalOfferingAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Richard Downer
 */
public interface GlobalVlanClient {

   /**
    * Get the details of an IP range by its id.
    * @param id the required IP range.
    * @return the requested IP range.
    */
   VlanIPRange getVlanIPRange(String id);

   /**
    * Lists all VLAN IP ranges.
    *
    * @param options optional arguments.
    * @return the list of IP ranges that match the criteria.
    */
   Set<VlanIPRange> listVlanIPRanges(ListVlanIPRangesOptions... options);

   /**
    * Creates a VLAN IP range.
    *
    * @param startIP the beginning IP address in the VLAN IP range
    * @param endIP the ending IP address in the VLAN IP range
    * @param options optional arguments
    * @return the newly-create IP range.
    */
   VlanIPRange createVlanIPRange(String startIP, String endIP, CreateVlanIPRangeOptions... options);

   /**
    * Deletes a VLAN IP range.
    * @param rangeId the id of the VLAN IP range
    */
   void deleteVlanIPRange(String rangeId);
}
