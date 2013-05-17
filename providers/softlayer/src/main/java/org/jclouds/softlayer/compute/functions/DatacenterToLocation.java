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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import javax.inject.Inject;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Converts an Datacenter into a Location.
 */
public class DatacenterToLocation implements Function<Datacenter,Location> {
   private final JustProvider provider;

   // allow us to lazy discover the provider of a resource
   @Inject
   public DatacenterToLocation(JustProvider provider) {
      this.provider = checkNotNull(provider, "provider");
   }
   
    @Override
    public Location apply(Datacenter datacenter) {
        return new LocationBuilder().scope(LocationScope.ZONE)
                                    .metadata(ImmutableMap.<String, Object>of())
                                    .description(datacenter.getLongName())
                                    .id(Long.toString(datacenter.getId()))
                                    .iso3166Codes(createIso3166Codes(datacenter.getLocationAddress()))
                                    .parent(Iterables.getOnlyElement(provider.get()))
                                    .build();
   }

   private Iterable<String> createIso3166Codes(Address address) {
      if (address== null) return ImmutableSet.<String> of();

      final String country = nullToEmpty(address.getCountry()).trim();
      if (country.isEmpty()) return ImmutableSet.<String> of();

      final String state = nullToEmpty(address.getState()).trim();
      if (state.isEmpty()) return ImmutableSet.<String> of(address.getCountry());

      return ImmutableSet.<String> of("" + country + "-" + state);

               
   }
}
