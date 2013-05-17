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

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Multimaps.index;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.functions.EndpointToSupplierURI;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class LocationIdToURIFromAccessForTypeAndVersion implements Supplier<Map<String, Supplier<URI>>> {

   public static interface Factory {
      /**
       *
       * @param apiType
       *           type of the api, according to the provider. ex.
       *           {@code compute} {@code object-store}
       * @param apiVersion
       *           version of the api, or null if not available
       * @return locations mapped to default uri
       * @throws NoSuchElementException
       *            if the {@code apiType} is not present in the catalog
       */
      LocationIdToURIFromAccessForTypeAndVersion createForApiTypeAndVersion(@Assisted("apiType") String apiType,
            @Nullable @Assisted("apiVersion") String apiVersion) throws NoSuchElementException;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Supplier<Access> access;
   protected final EndpointToSupplierURI endpointToSupplierURI;
   protected final Function<Endpoint, String> endpointToLocationId;
   protected final String apiType;
   protected final String apiVersion;

   @Inject
   public LocationIdToURIFromAccessForTypeAndVersion(Supplier<Access> access,
         EndpointToSupplierURI endpointToSupplierURI, Function<Endpoint, String> endpointToLocationId,
         @Assisted("apiType") String apiType, @Nullable @Assisted("apiVersion") String apiVersion) {
      this.access = access;
      this.endpointToSupplierURI = endpointToSupplierURI;
      this.endpointToLocationId = endpointToLocationId;
      this.apiType = apiType;
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      FluentIterable<Service> services = FluentIterable.from(access.get()).filter(apiTypeEquals);
      if (services.toSet().size() == 0)
         throw new NoSuchElementException(String.format("apiType %s not found in catalog %s", apiType, services));

      Iterable<Endpoint> endpoints = concat(services);

      if (size(endpoints) == 0)
         throw new NoSuchElementException(
               String.format("no endpoints for apiType %s in services %s", apiType, services));

      boolean checkVersionId = any(endpoints, versionAware);

      Multimap<String, Endpoint> locationToEndpoints = index(endpoints, endpointToLocationId);
      Map<String, Endpoint> locationToEndpoint;
      if (checkVersionId && apiVersion != null) {
         locationToEndpoint = refineToVersionSpecificEndpoint(locationToEndpoints);
         if (locationToEndpoint.size() == 0)
            throw new NoSuchElementException(String.format(
                  "no endpoints for apiType %s are of version %s, or version agnostic: %s", apiType, apiVersion,
                  locationToEndpoints));
      } else {
         locationToEndpoint = firstEndpointInLocation(locationToEndpoints);
      }

      logger.debug("endpoints for apiType %s and version %s: %s", apiType, apiVersion, locationToEndpoints);
      return Maps.transformValues(locationToEndpoint, endpointToSupplierURI);
   }

   @VisibleForTesting
   Map<String, Endpoint> firstEndpointInLocation(Multimap<String, Endpoint> locationToEndpoints) {
      Builder<String, Endpoint> locationToEndpointBuilder = ImmutableMap.<String, Endpoint> builder();
      for (Map.Entry<String, Collection<Endpoint>> entry : locationToEndpoints.asMap().entrySet()) {
         String locationId = entry.getKey();
         Collection<Endpoint> endpoints = entry.getValue();
         switch (endpoints.size()) {
         case 0:
            logNoEndpointsInLocation(locationId);
            break;
         default:
            locationToEndpointBuilder.put(locationId, Iterables.get(endpoints, 0));
         }
      }
      return locationToEndpointBuilder.build();
   }

   @VisibleForTesting
   Map<String, Endpoint> refineToVersionSpecificEndpoint(Multimap<String, Endpoint> locationToEndpoints) {
      Builder<String, Endpoint> locationToEndpointBuilder = ImmutableMap.<String, Endpoint> builder();
      for (Map.Entry<String, Collection<Endpoint>> entry : locationToEndpoints.asMap().entrySet()) {
         String locationId = entry.getKey();
         Collection<Endpoint> endpoints = entry.getValue();
         switch (endpoints.size()) {
         case 0:
            logNoEndpointsInLocation(locationId);
            break;
         default:
            putIfPresent(locationId, strictMatchEndpointVersion(endpoints, locationId), locationToEndpointBuilder);
         }

      }
      return locationToEndpointBuilder.build();
   }

   /**
    * Prioritizes endpoint.versionId over endpoint.id when matching
    */
   private Optional<Endpoint> strictMatchEndpointVersion(Iterable<Endpoint> endpoints, String locationId) {
      Optional<Endpoint> endpointOfVersion = tryFind(endpoints, apiVersionEqualsVersionId);
      if (!endpointOfVersion.isPresent())
         logger.debug("no endpoints of apiType %s matched expected version %s in location %s: %s", apiType, apiVersion,
               locationId, endpoints);
      return endpointOfVersion;
   }

   private void logNoEndpointsInLocation(String locationId) {
      logger.debug("no endpoints found for apiType %s in location %s", apiType, locationId);
   }

   private final Predicate<Endpoint> apiVersionEqualsVersionId = new Predicate<Endpoint>() {

      @Override
      public boolean apply(Endpoint input) {
         return input.getVersionId().equals(apiVersion);
      }

   };

   private final Predicate<Endpoint> versionAware = new Predicate<Endpoint>() {

      @Override
      public boolean apply(Endpoint input) {
         return input.getVersionId() != null;
      }

   };

   private final Predicate<Service> apiTypeEquals = new Predicate<Service>() {

      @Override
      public boolean apply(Service input) {
         return input.getType().equals(apiType);
      }

   };

   private static <K, V> void putIfPresent(K key, Optional<V> value, Builder<K, V> builder) {
      if (value.isPresent())
         builder.put(key, value.get());
   }

   @Override
   public String toString() {
      return "locationIdToURIFromAccessForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}
