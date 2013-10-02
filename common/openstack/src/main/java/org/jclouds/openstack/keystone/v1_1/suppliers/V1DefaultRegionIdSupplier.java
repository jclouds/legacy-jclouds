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
package org.jclouds.openstack.keystone.v1_1.suppliers;

import static com.google.common.collect.Iterables.tryFind;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.domain.Endpoint;
import org.jclouds.openstack.keystone.v1_1.functions.EndpointToRegion;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class V1DefaultRegionIdSupplier implements ImplicitRegionIdSupplier {

   public static interface Factory {
      /**
       * 
       * @param apiType
       *           type of the api, according to the provider. ex. {@code compute}
       *           {@code object-store}
       * @return region id
       * @throws NoSuchElementException
       *            if the {@code apiType} is not present in the catalog
       */
      ImplicitRegionIdSupplier createForApiType(@Assisted("apiType") String apiType) throws NoSuchElementException;
   }

   private final Supplier<Auth> auth;
   private final EndpointToRegion endpointToRegion;
   private final String apiType;

   @Inject
   public V1DefaultRegionIdSupplier(Supplier<Auth> auth, EndpointToRegion endpointToRegion,
            @Assisted("apiType") String apiType) {
      this.auth = auth;
      this.endpointToRegion = endpointToRegion;
      this.apiType = apiType;
   }

   /**
    * returns {@link Endpoint#isV1Default()} or first endpoint for service
    */
   @Override
   public String get() {
      Auth authResponse = auth.get();
      Iterable<Endpoint> endpointsForService = authResponse.getServiceCatalog().get(apiType);
      Optional<Endpoint> defaultEndpoint = tryFind(endpointsForService, new Predicate<Endpoint>() {
         @Override
         public boolean apply(Endpoint in) {
            return in.isV1Default();
         }
      });
      return endpointToRegion.apply(defaultEndpoint.or(Iterables.get(endpointsForService, 0)));
   }

   @Override
   public String toString() {
      return "defaultRegionIdFor(" + apiType + ")";
   }
}
