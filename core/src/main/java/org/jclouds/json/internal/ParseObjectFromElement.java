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
package org.jclouds.json.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * This is a class that helps the default {@link MapTypeAdapter} make a sane object graph when the
 * value is set to {@code Object}
 * http://code.google.com/p/google-gson/issues/detail?id=325
 * @author Adrian Cole
 */
public enum ParseObjectFromElement implements Function<JsonElement, Object> {
   SINGLETON;
   public Object apply(JsonElement input) {
      Object value = null;
      if (input == null || input.isJsonNull()) {
         value = null;
      } else if (input.isJsonPrimitive()) {
         JsonPrimitive primitive = input.getAsJsonPrimitive();
         if (primitive.isNumber()) {
            value = primitive.getAsNumber();
         } else if (primitive.isBoolean()) {
            value = primitive.getAsBoolean();
         } else {
            value = primitive.getAsString();
         }
      } else if (input.isJsonArray()) {
         value = Lists.newArrayList(Iterables.transform(input.getAsJsonArray(), this));
      } else if (input.isJsonObject()) {
         value = Maps.<String,Object>newLinkedHashMap(Maps.transformValues(JsonObjectAsMap.INSTANCE.apply(input.getAsJsonObject()),
               this));
      }
      return value;
   }
}