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

package org.jclouds.ohai.functions;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapSetToMultimap<K, V> implements Function<Map<K, Set<V>>, Multimap<K, V>> {

   @Override
   public Multimap<K, V> apply(Map<K, Set<V>> from) {
      Multimap<K, V> returnV = LinkedHashMultimap.create();
      for (Entry<K, Set<V>> entry : from.entrySet()) {
         for (V value : entry.getValue())
            returnV.put(entry.getKey(), value);
      }
      return returnV;
   }

}