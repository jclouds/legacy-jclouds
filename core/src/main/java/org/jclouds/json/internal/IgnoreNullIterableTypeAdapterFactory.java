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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Eliminates null values when deserializing Iterables
 * <p/>
 * Treats [null] as the empty set; [A, null] as [A]; etc.
 * 
 * @author Adam Lowe
 */
public class IgnoreNullIterableTypeAdapterFactory implements TypeAdapterFactory {
   
   @SuppressWarnings("unchecked")
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Type type = typeToken.getType();
      if (typeToken.getRawType() != Iterable.class || !(type instanceof ParameterizedType)) {
         return null;
      }

      Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
      TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
      return (TypeAdapter<T>) newIterableAdapter(elementAdapter);
   }

   protected <E> TypeAdapter<Iterable<E>> newIterableAdapter(final TypeAdapter<E> elementAdapter) {
      return new TypeAdapter<Iterable<E>>() {
         public void write(JsonWriter out, Iterable<E> value) throws IOException {
            out.beginArray();
            for (E element : value) {
               elementAdapter.write(out, element);
            }
            out.endArray();
         }

         public Iterable<E> read(JsonReader in) throws IOException {
            in.beginArray();
            Builder<E> builder = ImmutableList.<E>builder();
            while (in.hasNext()) {
               E element = elementAdapter.read(in);
               if (element != null) builder.add(element);
            }
            in.endArray();
            return builder.build();
         }
      }.nullSafe();
   }
}
