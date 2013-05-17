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
package org.jclouds.json.internal;

import static org.jclouds.reflect.Reflection2.method;

import java.lang.reflect.Type;

import com.google.common.reflect.Invokable;
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
            Invokable<?, Object> converter = method((Class<?>) classOfT, "fromValue", String.class);
            return (T) converter.invoke(null, json.getAsString());
         } catch (Exception e1) {
            throw e;
         }
      }
   }
}
