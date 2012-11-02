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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Adrian Cole
 */
@SuppressWarnings("unchecked")
public class EnumTypeAdapterThatReturnsFromValue<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {
   public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
   }

   @SuppressWarnings("cast")
   public T deserialize(JsonElement json, Type classOfT, JsonDeserializationContext context) throws JsonParseException {
      try {
         return (T) Enum.valueOf((Class<T>) classOfT, json.getAsString());
      } catch (IllegalArgumentException e) {
         try {
            Method converter = classToConvert.get((Class<?>) classOfT);
            return (T) converter.invoke(null, json.getAsString());
         } catch (Exception e1) {
            throw e;
         }
      }
   }

   private static final LoadingCache<Class<?>, Method> classToConvert = CacheBuilder.newBuilder()
         .build(new CacheLoader<Class<?>, Method>() {

            @Override
            public Method load(Class<?> from) throws ExecutionException {
               try {
                  Method method = from.getMethod("fromValue", String.class);
                  method.setAccessible(true);
                  return method;
               } catch (Exception e) {
                  throw new ExecutionException(e);
               }
            }

         });

   @Override
   public String toString() {
      return EnumTypeAdapterThatReturnsFromValue.class.getSimpleName();
   }
}
