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
package org.jclouds.openstack.nova.v1_1.util;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class NovaUtils {

   /**
    * The traditional way to represent a graph in Java is Map<V, Set<V>>, which is awkward in a
    * number of ways. Guava's Multimap framework makes it easy to handle a mapping from keys to
    * multiple values.
    * <p/>
    * Until we write or discover a gson Multimap deserializer, we may be stuck with this.
    * 
    * TODO: ask on stackoverflow and/or jesse wilson
    */
   @Deprecated
   public static <K, V> Map<K, Set<V>> toOldSchool(Multimap<K, V> in) {
      ImmutableMap.Builder<K, Set<V>> out = ImmutableMap.<K, Set<V>> builder();
      for (K type : in.keySet())
         out.put(type, ImmutableSet.copyOf(in.get(type)));
      return out.build();
   }

   /**
    * @see #toOldSchool
    */
   @Deprecated
   public static <K, V> ImmutableMultimap<K, V> fromOldSchool(Map<K, Set<V>> in) {
      Builder<K, V> out = ImmutableMultimap.<K, V> builder();
      for (K type : in.keySet())
         out.putAll(type, ImmutableSet.copyOf(in.get(type)));
      return out.build();
   }
}
