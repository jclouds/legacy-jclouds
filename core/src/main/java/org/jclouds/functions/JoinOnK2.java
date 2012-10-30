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
package org.jclouds.functions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class JoinOnK2<K, K2, V> implements Function<Map<K, Supplier<Set<K2>>>, Map<K2, Supplier<V>>> {
   private final Supplier<Map<K2, Supplier<V>>> regionToEndpointSupplier;

   public JoinOnK2(Supplier<Map<K2, Supplier<V>>> regionToEndpointSupplier) {
      this.regionToEndpointSupplier = regionToEndpointSupplier;
   }

   @Override
   public Map<K2, Supplier<V>> apply(Map<K, Supplier<Set<K2>>> regionToZones) {
      Map<K2, Supplier<V>> regionToEndpoint = regionToEndpointSupplier.get();
      Builder<K2, Supplier<V>> builder = ImmutableMap.builder();
      for (Entry<K, Supplier<Set<K2>>> entry : regionToZones.entrySet()) {
         for (K2 zone : entry.getValue().get()) {
            builder.put(zone, regionToEndpoint.get(entry.getKey()));
         }
      }
      return builder.build();
   }
}
