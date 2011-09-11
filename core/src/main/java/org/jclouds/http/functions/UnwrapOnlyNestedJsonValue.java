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
package org.jclouds.http.functions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class UnwrapOnlyNestedJsonValue<T> implements Function<HttpResponse, T> {

   private final ParseJson<Map<String, Map<String, T>>> json;
   private final TypeLiteral<T> type;

   @Inject
   UnwrapOnlyNestedJsonValue(ParseJson<Map<String, Map<String, T>>> json, TypeLiteral<T> type) {
      this.json = json;
      this.type = type;
   }

   @SuppressWarnings("unchecked")
   @Override
   public T apply(HttpResponse arg0) {
      Map<String, Map<String, T>> map = json.apply(arg0);
      if (map == null || map.size() == 0)
         return null;
      Map<String, T> map1 = Iterables.getOnlyElement(map.values());
      if (map1 == null || map1.size() == 0) {
         if (type.getRawType().isAssignableFrom(Set.class))
            return (T) ImmutableSet.of();
         else if (type.getRawType().isAssignableFrom(List.class))
            return (T) ImmutableList.of();
         else if (type.getRawType().isAssignableFrom(Map.class))
            return (T) ImmutableMap.of();
         return null;
      }
      return Iterables.getOnlyElement(map1.values());
   }
}