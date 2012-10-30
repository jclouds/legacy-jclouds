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
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Eliminates null values when deserializing Maps
 * <p/>
 * Treats {"a":null} as the empty map; {"a":1, "b":null} as {"a":1}; etc.
 * 
 * @author Adam Lowe
 */
public class IgnoreNullMapTypeAdapterFactory implements TypeAdapterFactory {
   @SuppressWarnings("unchecked")
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Type type = typeToken.getType();
      if (typeToken.getRawType() != Map.class || !(type instanceof ParameterizedType)) {
         return null;
      }

      Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
      Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
      TypeAdapter<?> keyAdapter = gson.getAdapter(TypeToken.get(keyType));
      TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
      return (TypeAdapter<T>) newMapAdapter(keyAdapter, valueAdapter);
   }

   protected <K,V> TypeAdapter<Map<K, V>> newMapAdapter(final TypeAdapter<K> keyAdapter, final TypeAdapter<V> valueAdapter) {
      return new TypeAdapter<Map<K, V>>() {
         public void write(JsonWriter out, Map<K, V> value) throws IOException {
            out.beginObject();
            for (Map.Entry<K, V> element : value.entrySet()) {
               out.name(String.valueOf(element.getKey()));
               valueAdapter.write(out, element.getValue());
            }
            out.endObject();
         }

         public Map<K, V> read(JsonReader in) throws IOException {
            Map<K, V> result = Maps.newLinkedHashMap();
            in.beginObject();
            while (in.hasNext()) {
               JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
               K name = keyAdapter.read(in);
               V value = valueAdapter.read(in);
               if (value != null) result.put(name, value);
            }
            in.endObject();
            return result;
         }
      }.nullSafe();
   }
}
