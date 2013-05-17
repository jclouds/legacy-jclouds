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
package org.jclouds.gogrid.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.location.Iso3166;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OptionToLocation implements Function<Option, Location> {
   private final Location provider;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;

   @Inject
   OptionToLocation(JustProvider justProvider,
            @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier) {
      this.provider = Iterables.getOnlyElement(justProvider.get());
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Location apply(Option from) {
      LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(from.getId() + "").description(
               from.getDescription()).parent(provider);
      Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();
      if (isoCodesById.containsKey(from.getId() + ""))
         builder.iso3166Codes(isoCodesById.get(from.getId() + "").get());
      return builder.build();
   }
}
