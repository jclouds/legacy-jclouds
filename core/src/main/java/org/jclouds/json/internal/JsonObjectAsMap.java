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

package org.jclouds.json.internal;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.base.Function;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Exposes the JsonObject as a map so that we can use gauva apis on it.
 * 
 * @author Adrian Cole
 */
public enum JsonObjectAsMap implements Function<JsonObject, Map<String, JsonElement>> {
   INSTANCE;

   private final Field members;

   JsonObjectAsMap() {
      try {
         members = JsonObject.class.getDeclaredField("members");
         members.setAccessible(true);
      } catch (NoSuchFieldException e) {
         throw new UnsupportedOperationException("cannot access gson internals", e);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Map<String, JsonElement> apply(JsonObject in) {
      try {
         return (Map<String, JsonElement>) members.get(in);
      } catch (IllegalArgumentException e) {
         throw new UnsupportedOperationException("cannot access gson internals", e);
      } catch (IllegalAccessException e) {
         throw new UnsupportedOperationException("cannot access gson internals", e);
      }
   }
}