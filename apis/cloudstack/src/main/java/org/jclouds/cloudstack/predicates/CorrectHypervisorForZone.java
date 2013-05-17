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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.Zone;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Templates can be present in a zone, and available, but not valid for launch
 * as their hypervisor isn't installed.
 * 
 * @author Adrian Cole
 */
@Singleton
public class CorrectHypervisorForZone implements Function<String, Predicate<Template>> {
   private final Supplier<Map<String, Set<String>>> hypervisorsSupplier;

   @Inject
   public CorrectHypervisorForZone(CloudStackClient client) {
      this(Suppliers.ofInstance(new CloudStackClientToZoneToHypervisors().apply(checkNotNull(client, "client"))));
   }

   public CorrectHypervisorForZone(Supplier<Map<String, Set<String>>> hypervisorsSupplier) {
      this.hypervisorsSupplier = checkNotNull(hypervisorsSupplier, "hypervisorsSupplier");
   }

   private static class CloudStackClientToZoneToHypervisors implements
         Function<CloudStackClient, Map<String, Set<String>>> {

      @Override
      public Map<String, Set<String>> apply(CloudStackClient client) {
         checkNotNull(client, "client");
         Builder<String, Set<String>> builder = ImmutableMap.builder();
         for (Zone zone : client.getZoneClient().listZones()) {
            builder.put(zone.getId(), client.getHypervisorClient().listHypervisorsInZone(zone.getId()));
         }
         return builder.build();
      }
   }

   @Override
   public Predicate<Template> apply(final String zoneId) {

      final Set<String> acceptableHypervisorsInZone;
      try {
         acceptableHypervisorsInZone = this.hypervisorsSupplier.get().get(zoneId);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException("unknown zone: " + zoneId);
      }
      if (acceptableHypervisorsInZone.size() == 0)
         return Predicates.alwaysFalse();
      return new Predicate<Template>() {

         @Override
         public boolean apply(Template input) {
            return Predicates.in(acceptableHypervisorsInZone).apply(input.getHypervisor());
         }

         @Override
         public String toString() {
            return "hypervisorsInZone(" + zoneId + ", " + acceptableHypervisorsInZone + ")";
         }
      };
   }
}
