/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import org.jclouds.dynect.v3.domain.CreatePrimaryZone;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.domain.Zone.SerialStyle;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.FluentIterable;

/**
 * @see ZoneAsyncApi
 * @author Adrian Cole
 */
public interface ZoneApi {
   /**
    * Lists all zone ids.
    */
   FluentIterable<String> list();

   /**
    * Creates a new primary zone.
    * 
    * @param zone
    *           required parameters to create the zone.
    * @return unpublished zone
    */
   Zone create(CreatePrimaryZone zone);

   /**
    * Creates a new primary zone with one hour default TTL and
    * {@link SerialStyle#INCREMENT}
    * 
    * @param fqdn
    *           fqdn of the zone to create {@ex. jclouds.org}
    * @param contact
    *           email address of the contact
    * @return unpublished zone
    */
   Zone createWithContact(String fqdn, String contact);

   /**
    * Retrieves information about the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to get information about. ex
    *           {@code jclouds.org}
    * @return null if not found
    */
   @Nullable
   Zone get(String fqdn);

   /**
    * deletes the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to delete ex {@code jclouds.org}
    * @return null if not found
    */
   @Nullable
   Job delete(String fqdn);

   /**
    * Publishes the current zone
    * 
    * @param fqdn
    *           fqdn of the zone to publish. ex
    *           {@code jclouds.org}
    */
   Zone publish(String fqdn);

   /**
    * freezes the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to freeze ex {@code jclouds.org}
    */
   Job freeze(String fqdn);
   
   /**
    * thaws the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to thaw ex {@code jclouds.org}
    */
   Job thaw(String fqdn);
}