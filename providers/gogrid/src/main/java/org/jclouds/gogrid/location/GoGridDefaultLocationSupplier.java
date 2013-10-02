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
package org.jclouds.gogrid.location;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GoGridDefaultLocationSupplier implements ImplicitLocationSupplier {
   private final Supplier<Set<? extends Location>> locations;
   private final String defaultDC;

   @Inject
   GoGridDefaultLocationSupplier(@Memoized Supplier<Set<? extends Location>> locations, @Named(TEMPLATE) String template) {
      this.locations = locations;
      Map<String, String> map = Splitter.on(',').trimResults().withKeyValueSeparator("=").split(template);
      //TODO: move to real ImplicitLocationSupplier
      this.defaultDC = checkNotNull(map.get("locationId"), "locationId not in % value: %s", TEMPLATE, template);
   }

   @Override
   public Location get() {
      return Iterables.find(locations.get(), new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(defaultDC);
         }

      });
   }
}
