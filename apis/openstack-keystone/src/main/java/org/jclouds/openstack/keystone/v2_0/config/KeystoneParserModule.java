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
package org.jclouds.openstack.keystone.v2_0.config;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * @author Adam Lowe
 */
public class KeystoneParserModule extends AbstractModule {

   
   @Override
   protected void configure() {
      bind(DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
      bind(new TypeLiteral<Set<TypeAdapterFactory>>() {
      }).toInstance(ImmutableSet.<TypeAdapterFactory>of(new SetTypeAdapterFactory()));
   }

   /**
    * Handles the goofy structures with "values" holder wrapping an array
    * http://docs.openstack.org/api/openstack-identity-service/2.0/content/Versions-d1e472.html
    * <p/>
    * Treats [A,B,C] and {"values"=[A,B,C], "someotherstuff"=...} as the same Set
    */
   public static class SetTypeAdapterFactory implements TypeAdapterFactory {
      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if (typeToken.getRawType() != Set.class || !(type instanceof ParameterizedType)) {
            return null;
         }

         Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
         TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
         return TypeAdapter.class.cast(newSetAdapter(elementAdapter));
      }

      private <E> TypeAdapter<Set<E>> newSetAdapter(final TypeAdapter<E> elementAdapter) {
         return new TypeAdapter<Set<E>>() {
            public void write(JsonWriter out, Set<E> value) throws IOException {
               out.beginArray();
               for (E element : value) {
                  elementAdapter.write(out, element);
               }
               out.endArray();
            }

            public Set<E> read(JsonReader in) throws IOException {
               Set<E> result = Sets.newLinkedHashSet();
               if (in.peek() == JsonToken.BEGIN_OBJECT) {
                  boolean foundValues = false;
                  in.beginObject();
                  while (in.hasNext()) {
                     String name = in.nextName();
                     if (Objects.equal("values", name)) {
                        foundValues = true;
                        readArray(in, result);
                     } else {
                        in.skipValue();
                     }
                  }
                  checkState(foundValues, "Expected BEGIN_ARRAY or the object to contain an array called 'values'");
                  in.endObject();
               } else {
                  readArray(in, result);
               }

               return result;
            }

            private void readArray(JsonReader in, Set<E> result) throws IOException {
               in.beginArray();
               while (in.hasNext()) {
                  E element = elementAdapter.read(in);
                  result.add(element);
               }
               in.endArray();
            }
         }.nullSafe();
      }
   }
}
