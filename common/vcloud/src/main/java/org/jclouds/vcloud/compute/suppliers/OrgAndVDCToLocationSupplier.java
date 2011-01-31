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

package org.jclouds.vcloud.compute.suppliers;

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
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
@Singleton
public class OrgAndVDCToLocationSupplier extends JustProvider {

   private final Supplier<Map<String, ReferenceType>> orgNameToResource;
   private final Supplier<Map<String, ? extends Org>> orgNameToVDCResource;
   private final Map<String, Set<String>> isoCodesById;

   @Inject
   OrgAndVDCToLocationSupplier(@Iso3166 Set<String> isoCodes, @Provider String providerName, @Provider URI endpoint,
            @org.jclouds.vcloud.endpoints.Org Supplier<Map<String, ReferenceType>> orgNameToResource,
            Supplier<Map<String, ? extends Org>> orgNameToVDCResource, @Iso3166 Map<String, Set<String>> isoCodesById) {
      super(isoCodes, providerName, endpoint);
      this.orgNameToResource = checkNotNull(orgNameToResource, "orgNameToResource");
      this.orgNameToVDCResource = checkNotNull(orgNameToVDCResource, "orgNameToVDCResource");
      this.isoCodesById = checkNotNull(isoCodesById, "isoCodesById");
   }

   @Override
   public Set<? extends Location> get() {
      return buildJustProviderOrVDCs().build();
   }

   protected Builder<Location> buildJustProviderOrVDCs() {
      Builder<Location> locations = ImmutableSet.builder();
      Location provider = Iterables.getOnlyElement(super.get());
      if (orgNameToResource.get().size() == 0)
         return locations.add(provider);
      else
         for (ReferenceType org : orgNameToResource.get().values()) {
            LocationBuilder builder = new LocationBuilder().scope(LocationScope.REGION).id(
                     org.getHref().toASCIIString()).description((org.getName())).parent(provider);
            if (isoCodesById.containsKey(org.getHref().toASCIIString()))
               builder.iso3166Codes(isoCodesById.get(org.getHref().toASCIIString()));
            Location orgL = builder.build();
            for (ReferenceType vdc : orgNameToVDCResource.get().get(org.getName()).getVDCs().values()) {
               builder = new LocationBuilder().scope(LocationScope.ZONE).id(vdc.getHref().toASCIIString()).description(
                        (vdc.getName())).parent(orgL);
               if (isoCodesById.containsKey(vdc.getHref().toASCIIString()))
                  builder.iso3166Codes(isoCodesById.get(vdc.getHref().toASCIIString()));
               locations.add(builder.build());
            }
         }
      return locations;
   }

}