/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.vcloud.domain.NamedResource;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudLocation extends LocationImpl {

   private final NamedResource resource;

   public NamedResource getResource() {
      return resource;
   }

   public VCloudLocation(NamedResource resource, Location parent) {
      super(checkNotNull(resource, "resource").getType().endsWith("org+xml") ? LocationScope.REGION
            : LocationScope.ZONE, resource.getName(), resource.getName(), parent);
      this.resource = resource;
   }

   /**
    * 
    */
   private static final long serialVersionUID = -5052812549904524841L;

}