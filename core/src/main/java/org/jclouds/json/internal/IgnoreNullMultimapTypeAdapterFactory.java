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

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Parses Multimaps to/from json - strips out any null values when deserializing
 * 
 * @author Adam Lowe
 */
public class IgnoreNullMultimapTypeAdapterFactory implements TypeAdapterFactory {

   @SuppressWarnings("unchecked")
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Type type = typeToken.getType();
      if ((typeToken.getRawType() != Multimap.class) || !(type instanceof ParameterizedType)) {
         return null;
      }

      Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
      Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
      TypeAdapter<?> keyAdapter = gson.getAdapter(TypeToken.get(keyType));
      TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
      return (TypeAdapter<T>) newMultimapAdapter(keyAdapter, valueAdapter);
   }

   protected <K,V> TypeAdapter<Multimap<K, V>> newMultimapAdapter(final TypeAdapter<K> keyAdapter, final TypeAdapter<V> valueAdapter) {
      return new TypeAdapter<Multimap<K, V>>() {
         public void write(JsonWriter out, Multimap<K, V> map) throws IOException {
            out.beginObject();
            for (K key : map.keySet()) {
               out.name(String.valueOf(key));
               out.beginArray();
               for (V value : map.get(key)) {
                  valueAdapter.write(out, value);
               }
               out.endArray();
            }
            out.endObject();
         }

         public Multimap<K, V> read(JsonReader in) throws IOException {
            ImmutableMultimap.Builder<K, V> result = ImmutableListMultimap.builder();
            in.beginObject();
            while (in.hasNext()) {
               JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
               K name = keyAdapter.read(in);
               in.beginArray();
               while (in.hasNext()) {
                  V value = valueAdapter.read(in);
                  if (value != null) result.put(name, value);
               }
               in.endArray();
            }
            in.endObject();
            return result.build();
         }
      }.nullSafe();
   }
}
