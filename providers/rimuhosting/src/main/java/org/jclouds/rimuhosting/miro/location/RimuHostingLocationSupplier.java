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
package org.jclouds.rimuhosting.miro.location;

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
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.DataCenter;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingLocationSupplier extends JustProvider implements LocationsSupplier {

   private final RimuHostingClient sync;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;

   @Inject
   RimuHostingLocationSupplier(@Iso3166 Set<String> isoCodes, @Provider String providerName, @Provider Supplier<URI> endpoint,
            RimuHostingClient sync, @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier) {
      super(providerName, endpoint, isoCodes);
      this.sync = checkNotNull(sync, "sync");
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Set<? extends Location> get() {
      Builder<Location> locations = ImmutableSet.builder();
      Iterable<DataCenter> list = Iterables.filter(Iterables.transform(sync.getPricingPlanList(),
               new Function<PricingPlan, DataCenter>() {

                  @Override
                  public DataCenter apply(PricingPlan arg0) {
                     return arg0.getDataCenter();
                  }

               }), Predicates.<DataCenter>notNull());
      Location provider = Iterables.getOnlyElement(super.get());
      if (Iterables.size(list) == 0)
         locations.add(provider);
      else{
         Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();
         for (DataCenter from : list) {
            LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(from.getId()).description(
                     from.getName()).parent(provider);
            if (isoCodesById.containsKey(from.getId()))
               builder.iso3166Codes(isoCodesById.get(from.getId()).get());
            locations.add(builder.build());
         }
      }
      return locations.build();
   }
}
