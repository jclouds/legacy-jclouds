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
package org.jclouds.vcloud.location;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgAndVDCToLocationSupplier extends JustProvider implements LocationsSupplier {

   private final Supplier<Map<String, ReferenceType>> orgNameToResource;
   private final Supplier<Map<String, Org>> orgNameToVDCResource;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;

   @Inject
   OrgAndVDCToLocationSupplier(@Iso3166 Set<String> isoCodes, @Provider String providerName,
            @Provider Supplier<URI> endpoint,
            @org.jclouds.vcloud.endpoints.Org Supplier<Map<String, ReferenceType>> orgNameToResource,
            Supplier<Map<String, Org>> orgNameToVDCResource,
            @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier) {
      super(providerName, endpoint, isoCodes);
      this.orgNameToResource = checkNotNull(orgNameToResource, "orgNameToResource");
      this.orgNameToVDCResource = checkNotNull(orgNameToVDCResource, "orgNameToVDCResource");
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Set<Location> get() {
      return buildJustProviderOrVDCs().build();
   }

   protected Builder<Location> buildJustProviderOrVDCs() {
      Builder<Location> locations = ImmutableSet.builder();
      Location provider = Iterables.getOnlyElement(super.get());
      if (orgNameToResource.get().size() == 0)
         return locations.add(provider);
      Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();
      for (ReferenceType org : orgNameToResource.get().values()) {
         LocationBuilder builder = new LocationBuilder().scope(LocationScope.REGION).id(org.getHref().toASCIIString())
                  .description((org.getName())).parent(provider);
         if (isoCodesById.containsKey(org.getHref().toASCIIString()))
            builder.iso3166Codes(isoCodesById.get(org.getHref().toASCIIString()).get());
         Location orgL = builder.build();
         for (ReferenceType vdc : orgNameToVDCResource.get().get(org.getName()).getVDCs().values()) {
            builder = new LocationBuilder().scope(LocationScope.ZONE).id(vdc.getHref().toASCIIString()).description(
                     (vdc.getName())).parent(orgL);
            if (isoCodesById.containsKey(vdc.getHref().toASCIIString()))
               builder.iso3166Codes(isoCodesById.get(vdc.getHref().toASCIIString()).get());
            locations.add(builder.build());
         }
      }
      return locations;
   }

}
