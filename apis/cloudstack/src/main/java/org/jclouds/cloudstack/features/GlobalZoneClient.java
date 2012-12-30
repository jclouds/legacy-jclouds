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

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.CreateZoneOptions;
import org.jclouds.cloudstack.options.UpdateZoneOptions;

/**
 * Provides synchronous access to CloudStack Zone features available to Global
 * Admin users.
 * 
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface GlobalZoneClient extends ZoneClient {

   /**
    * Create a new Zone
    *
    * @param name
    *          the name of the Zone
    * @param networkType
    *          network type of the zone, can be Basic or Advanced
    * @param dns1
    *          the first DNS for the Zone
    * @param internalDns1
    *          the first internal DNS for the Zone
    * @param options
    *          optional arguments
    * @return
    *          zone instance or null
    */
   Zone createZone(String name, NetworkType networkType, String dns1,
      String internalDns1, CreateZoneOptions... options);

   /**
    * Update a zone
    *
    * @param id
    *          the ID of the Zone
    * @param options
    *          optional arguments
    * @return
    */
   Zone updateZone(String id, UpdateZoneOptions... options);

   /**
    * Delete a zone with a specific ID
    *
    * @param zoneId
    *          the ID of the Zone
    */
   Void deleteZone(String zoneId);

}
