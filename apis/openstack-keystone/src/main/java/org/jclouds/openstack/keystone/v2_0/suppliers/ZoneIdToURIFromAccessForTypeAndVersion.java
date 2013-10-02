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
package org.jclouds.openstack.keystone.v2_0.suppliers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToRegion;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToSupplierURI;

import com.google.common.base.Supplier;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class ZoneIdToURIFromAccessForTypeAndVersion extends LocationIdToURIFromAccessForTypeAndVersion implements
         ZoneIdToURISupplier {

   @Inject
   public ZoneIdToURIFromAccessForTypeAndVersion(
            Supplier<Access> access,
            // NOTE that in some services, the region is in fact the zone. temporarily, we need
            // to use the region field, in this case.
            EndpointToSupplierURI endpointToSupplierURI, EndpointToRegion endpointToZone,
            @Assisted("apiType") String apiType, @Nullable @Assisted("apiVersion") String apiVersion) {
      super(access, endpointToSupplierURI, endpointToZone, apiType, apiVersion);
   }

   @Override
   public String toString() {
      return "zoneIdToURIFromAccessForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}
