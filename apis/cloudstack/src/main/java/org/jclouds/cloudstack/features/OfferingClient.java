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
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.options.ListDiskOfferingsOptions;
import org.jclouds.cloudstack.options.ListNetworkOfferingsOptions;
import org.jclouds.cloudstack.options.ListServiceOfferingsOptions;

/**
 * Provides synchronous access to CloudStack zone features.
 * <p/>
 * 
 * @see OfferingAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface OfferingClient {
   /**
    * Lists service offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return service offerings matching query, or empty set, if no service
    *         offerings are found
    */
   Set<ServiceOffering> listServiceOfferings(ListServiceOfferingsOptions... options);

   /**
    * get a specific service offering by id
    * 
    * @param id
    *           offering to get
    * @return service offering or null if not found
    */
   ServiceOffering getServiceOffering(String id);

   /**
    * Lists disk offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return disk offerings matching query, or empty set, if no disk offerings
    *         are found
    */
   Set<DiskOffering> listDiskOfferings(ListDiskOfferingsOptions... options);

   /**
    * get a specific disk offering by id
    * 
    * @param id
    *           offering to get
    * @return disk offering or null if not found
    */
   DiskOffering getDiskOffering(String id);

   /**
    * Lists service offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return service offerings matching query, or empty set, if no service
    *         offerings are found
    */
   Set<NetworkOffering> listNetworkOfferings(ListNetworkOfferingsOptions... options);

   /**
    * get a specific service offering by id
    * 
    * @param id
    *           offering to get
    * @return service offering or null if not found
    */
   NetworkOffering getNetworkOffering(String id);
}
