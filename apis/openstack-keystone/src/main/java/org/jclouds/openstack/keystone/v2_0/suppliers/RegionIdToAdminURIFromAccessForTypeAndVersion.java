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
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToRegion;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToSupplierAdminURI;

import com.google.common.base.Supplier;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class RegionIdToAdminURIFromAccessForTypeAndVersion extends LocationIdToURIFromAccessForTypeAndVersion implements
         RegionIdToAdminURISupplier {
   @Inject
   public RegionIdToAdminURIFromAccessForTypeAndVersion(Supplier<Access> access,
            EndpointToSupplierAdminURI endpointToSupplierURI, EndpointToRegion endpointToRegion,
            @Assisted("apiType") String apiType, @Nullable @Assisted("apiVersion") String apiVersion) {
      super(access, endpointToSupplierURI, endpointToRegion, apiType, apiVersion);
   }

   @Override
   public String toString() {
      return "regionIdToAdminURIFromAccessForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}
