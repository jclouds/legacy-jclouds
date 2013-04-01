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
package org.jclouds.domain;

import org.jclouds.management.annotations.ManagedAttribute;
import org.jclouds.management.annotations.ManagedType;

import java.util.Map;
import java.util.Set;

/**
 * Description of where a resource is running. Note this can be physical or virtual.
 * 
 * @author Adrian Cole
 */
@ManagedType
public interface Location {

   /**
    * Scope of the location, ex. region, zone, host
    * 
    */
   LocationScope getScope();

   /**
    * Unique ID provided by the provider (us-standard, miami, etc)
    * 
    */
   @ManagedAttribute( description = "The id of the location")
   String getId();

   /**
    * Description of the location
    */
   @ManagedAttribute( description = "The Location description")
   String getDescription();

   /**
    * The parent, or null, if top-level
    */
   Location getParent();

   /**
    * The id of the parent location.
    * The method is used to avoid cycles cycles in the managed resource model.
    * @return
    */
   @ManagedAttribute( description = "The id of the parent")
   String getParentId();

   /**
    * @return immutable set of metadata relating to this location
    */
   Map<String, Object> getMetadata();

   /**
    * @return if known, the IS0 3166 or 3166-2 divisions where this service may run. ex. a set of
    *         strings like "US" or "US-CA"; otherwise returns an empty list.
    * @see <a
    *      href="http://www.iso.org/iso/country_codes/background_on_iso_3166/what_is_iso_3166.htm">3166</a>
    */
   Set<String> getIso3166Codes();
}
