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
package org.jclouds.openstack.keystone.v2_0.suppliers;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToRegion;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToSupplierURI;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class RegionIdToURIFromAccessForTypeAndVersionSupplier implements RegionIdToURISupplier {
   private final Supplier<Access> access;
   private final EndpointToSupplierURI endpointToSupplierURI;
   private final EndpointToRegion endpointToRegion;
   private final String apiType;
   private final String apiVersion;

   @Inject
   public RegionIdToURIFromAccessForTypeAndVersionSupplier(Supplier<Access> access,
            EndpointToSupplierURI endpointToSupplierURI, EndpointToRegion endpointToRegion,
            @Assisted("apiType") String apiType, @Assisted("apiVersion") String apiVersion) {
      this.access = access;
      this.endpointToSupplierURI = endpointToSupplierURI;
      this.endpointToRegion = endpointToRegion;
      this.apiType = apiType;
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Access accessResponse = access.get();
      Service service = Iterables.find(accessResponse.getServiceCatalog(), new Predicate<Service>() {

         @Override
         public boolean apply(Service input) {
            return input.getType().equals(apiType);
         }

      });
      Map<String, Endpoint> regionIdToEndpoint = Maps.uniqueIndex(Iterables.filter(service.getEndpoints(),
               new Predicate<Endpoint>() {

                  @Override
                  public boolean apply(Endpoint input) {
                     return input.getVersionId().equals(apiVersion);
                  }

               }), endpointToRegion);
      return Maps.transformValues(regionIdToEndpoint, endpointToSupplierURI);
   }

   @Override
   public String toString() {
      return "regionIdToURIFromAccessForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}