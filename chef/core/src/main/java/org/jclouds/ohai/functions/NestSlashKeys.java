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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.TypeLiteral;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class NestSlashKeys implements Function<Multimap<String, Supplier<JsonBall>>, Map<String, JsonBall>> {

   private final Json json;

   @Inject
   NestSlashKeys(Json json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public Map<String, JsonBall> apply(Multimap<String, Supplier<JsonBall>> from) {

      Map<String, JsonBall> autoAttrs = mergeSameKeys(from);

      Map<String, JsonBall> modifiableFlatMap = Maps.newLinkedHashMap(Maps.filterKeys(autoAttrs,
            new Predicate<String>() {

               @Override
               public boolean apply(String input) {
                  return input.indexOf('/') == -1;
               }

            }));
      Map<String, JsonBall> withSlashesMap = Maps.difference(autoAttrs, modifiableFlatMap).entriesOnlyOnLeft();
      for (Entry<String, JsonBall> entry : withSlashesMap.entrySet()) {
         List<String> keyParts = Lists.newArrayList(Splitter.on('/').split(entry.getKey()));
         JsonBall toInsert = entry.getValue();
         try {
            putUnderContext(keyParts, toInsert, modifiableFlatMap);
         } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("error inserting value in entry: " + entry.getKey(), e);
         }
      }
      return modifiableFlatMap;
   }

   private Map<String, JsonBall> mergeSameKeys(Multimap<String, Supplier<JsonBall>> from) {
      Map<String, JsonBall> merged = Maps.newLinkedHashMap();
      for (Entry<String, Supplier<JsonBall>> entry : from.entries()) {
         if (merged.containsKey(entry.getKey())) {
            mergeAsPeer(entry.getKey(), entry.getValue().get(), merged);
         } else {
            merged.put(entry.getKey(), entry.getValue().get());
         }
      }
      return merged;
   }

   @VisibleForTesting
   void mergeAsPeer(String key, JsonBall value, Map<String, JsonBall> insertionContext) {
      Map<String, JsonBall> valueContext = json.fromJson(insertionContext.get(key).toString(), mapLiteral);
      Map<String, JsonBall> toPut = json.<Map<String, JsonBall>> fromJson(value.toString(), mapLiteral);
      Set<String> uniques = Sets.difference(toPut.keySet(), valueContext.keySet());
      for (String k : uniques)
         valueContext.put(k, toPut.get(k));
      Set<String> conflicts = Sets.difference(toPut.keySet(), uniques);
      for (String k : conflicts) {
         JsonBall v = toPut.get(k);
         if (v.toString().matches("^\\{.*\\}$")) {
            mergeAsPeer(k, v, valueContext);
         } else {
            // replace
            valueContext.put(k, v);
         }
      }
      insertionContext.put(key, new JsonBall(json.toJson(valueContext, mapLiteral)));
   }

   /**
    * @param keyParts
    * @param toInsert
    * @param destination
    * @throws IllegalArgumentException
    *            <p/>
    *            if destination.get(keyParts(0)) is not a map *
    *            <p/>
    *            keyParts is zero length
    */
   void putUnderContext(List<String> keyParts, JsonBall toInsert, Map<String, JsonBall> destination) {
      checkNotNull(keyParts, "keyParts");
      checkArgument(keyParts.size() >= 1, "keyParts must contain at least one element");

      checkNotNull(toInsert, "toInsert");
      checkNotNull(destination, "destination");

      String rootKey = keyParts.remove(0);
      String rootValue = destination.containsKey(rootKey) ? destination.get(rootKey).toString() : "{}";

      checkArgument(rootValue.matches("^\\{.*\\}$"), "value must be a hash: %s", rootValue);
      Map<String, JsonBall> insertionContext = json.fromJson(rootValue, mapLiteral);
      if (keyParts.size() == 1) {
         if (!insertionContext.containsKey(keyParts.get(0))) {
            insertionContext.put(keyParts.get(0), toInsert);
         } else {
            String key = keyParts.get(0);
            mergeAsPeer(key, toInsert, insertionContext);
         }
      } else {
         putUnderContext(keyParts, toInsert, insertionContext);
      }
      destination.put(rootKey, new JsonBall(json.toJson(insertionContext, mapLiteral)));
   }

   final Type mapLiteral = new TypeLiteral<Map<String, JsonBall>>() {
   }.getType();
   final Type listLiteral = new TypeLiteral<List<JsonBall>>() {
   }.getType();
}