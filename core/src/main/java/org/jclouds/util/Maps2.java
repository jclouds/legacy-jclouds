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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * General utilities used in jclouds code for {@link Map Maps}.
 * 
 * @author Adrian Cole
 */
public class Maps2 {

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
   public static <K1, K2, V> Map<K2, V> transformKeys(Map<K1, V> in, Function<K1, K2> fn) {
      checkNotNull(in, "input map");
      checkNotNull(fn, "function");
      Builder<K2, V> returnVal = ImmutableMap.builder();
      for (Entry<K1, V> entry : in.entrySet())
         returnVal.put(fn.apply(entry.getKey()), entry.getValue());
      return returnVal.build();
   }
}
