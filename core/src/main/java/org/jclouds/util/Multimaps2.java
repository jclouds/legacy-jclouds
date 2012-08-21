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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 */
public class Multimaps2 {

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
      ImmutableMap.Builder<K, Set<V>> out = ImmutableMap.builder();
      for (K type : in.keySet())
         out.put(type, ImmutableSet.copyOf(in.get(type)));
      return out.build();
   }

   /**
    * @see #toOldSchool
    */
   @Deprecated
   public static <K, V> ImmutableMultimap<K, V> fromOldSchool(Map<K, Set<V>> in) {
      Builder<K, V> out = ImmutableMultimap.builder();
      for (K type : in.keySet())
         out.putAll(type, ImmutableSet.copyOf(in.get(type)));
      return out.build();
   }

   public static <K, V> Multimap<K, V> replaceValue(Multimap<K, V> fromMultimap, final K key, final V value) {
      checkNotNull(fromMultimap, "input multimap");
      checkNotNull(key, "key");
      checkNotNull(value, "value");
      return ImmutableMultimap.<K, V>builder()
                              .putAll(withoutKey(fromMultimap, key))
                              .put(key, value).build();
   }
   
   public static <K, V> Multimap<K, V> replaceEntries(Multimap<K, V> fromMultimap, Multimap<K, V> updates) {
      checkNotNull(fromMultimap, "input multimap");
      checkNotNull(updates, "updates");
      return ImmutableMultimap.<K, V>builder()
                              .putAll(withoutKeys(fromMultimap, updates.keySet()))
                              .putAll(updates).build();
   }
   
   public static <K, V> Multimap<K, V> withoutKey(Multimap<K, V> fromMultimap, K key) {
      return Multimaps.<K, V> filterKeys(fromMultimap, Predicates.not(Predicates.equalTo(key)));
   }
   
   public static <K, V> Multimap<K, V> withoutKeys(Multimap<K, V> fromMultimap, Set<K> keys) {
      return Multimaps.<K, V> filterKeys(fromMultimap, Predicates.not(Predicates.in(keys)));
   }
   
   /**
    * change the keys but keep the values in-tact.
    * 
    * @param <K1>
    *           input key type
    * @param <K2>
    *           output key type
    * @param <V>
    *           value type
    * @param in
    *           input map to transform
    * @param fn
    *           how to transform the values
    * @return immutableMap with the new keys.
    */
   public static <K1, K2, V> Multimap<K2, V> transformKeys(Multimap<K1, V> in, Function<K1, K2> fn) {
      checkNotNull(in, "input map");
      checkNotNull(fn, "function");
      Builder<K2, V> returnVal = ImmutableMultimap.builder();
      for (Entry<K1, V> entry : in.entries())
         returnVal.put(fn.apply(entry.getKey()), entry.getValue());
      return returnVal.build();
   }
}
