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
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToSupplierURI;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class LocationIdToURIFromAccessForTypeAndVersion implements Supplier<Map<String, Supplier<URI>>> {
   protected final Supplier<Access> access;
   protected final EndpointToSupplierURI endpointToSupplierURI;
   protected final Function<Endpoint, String> endpointToLocationId;
   protected final String apiType;
   protected final String apiVersion;

   @Inject
   public LocationIdToURIFromAccessForTypeAndVersion(Supplier<Access> access,
            EndpointToSupplierURI endpointToSupplierURI, Function<Endpoint, String> endpointToLocationId,
            @Assisted("apiType") String apiType, @Assisted("apiVersion") String apiVersion) {
      this.access = access;
      this.endpointToSupplierURI = endpointToSupplierURI;
      this.endpointToLocationId = endpointToLocationId;
      this.apiType = apiType;
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Access accessResponse = access.get();
      Set<Service> services = Sets.filter(accessResponse.getServiceCatalog(), new Predicate<Service>() {

         @Override
         public boolean apply(Service input) {
            return input.getType().equals(apiType);
         }

      });
      if (services.size() == 0)
         throw new NoSuchElementException(String.format("apiType %s not found in catalog %s", apiType,
                  accessResponse.getServiceCatalog()));

      Iterable<Endpoint> endpoints = Iterables.filter(Iterables.concat(services), new Predicate<Endpoint>() {

         @Override
         public boolean apply(Endpoint input) {
            if (input.getVersionId() == null) {
               return true;
            }
            return input.getVersionId().equals(apiVersion);
         }

      });

      if (Iterables.size(endpoints) == 0)
         throw new NoSuchElementException(String.format(
                  "no endpoints for apiType %s are of version %s, or version agnostic: %s", apiType, apiVersion,
                  services));

      Map<String, Endpoint> locationIdToEndpoint = Maps.uniqueIndex(endpoints, endpointToLocationId);
      return Maps.transformValues(locationIdToEndpoint, endpointToSupplierURI);
   }

   @Override
   public String toString() {
      return "locationIdToURIFromAccessForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}