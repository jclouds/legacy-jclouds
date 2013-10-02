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
package org.jclouds.openstack.keystone.v2_0.config;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Set;

import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.SetTypeAdapter;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.SetTypeAdapterFactory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;

/**
 * @author Adam Lowe
 */
public class KeystoneParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
      bind(SetTypeAdapterFactory.class).to(ValuesSetTypeAdapterFactory.class);
   }

   /**
    * Handles the goofy structures with "values" holder wrapping an array
    * http://docs.openstack.org/api/openstack-identity-service/2.0/content/Versions-d1e472.html
    * <p/>
    * Treats [A,B,C] and {"values"=[A,B,C], "someotherstuff"=...} as the same Set
    */
   public static class ValuesSetTypeAdapterFactory extends SetTypeAdapterFactory {

      @Override
      @SuppressWarnings("unchecked")
      protected <E, I> TypeAdapter<I> newAdapter(TypeAdapter<E> elementAdapter) {
         return (TypeAdapter<I>) new Adapter<E>(elementAdapter);
      }

      public static final class Adapter<E> extends TypeAdapter<Set<E>> {

         private final SetTypeAdapter<E> delegate;

         public Adapter(TypeAdapter<E> elementAdapter) {
            this.delegate = new SetTypeAdapter<E>(elementAdapter);
            nullSafe();
         }

         public void write(JsonWriter out, Set<E> value) throws IOException {
            this.delegate.write(out, value);
         }

         @Override
         public Set<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.BEGIN_OBJECT) {
               Builder<E> builder = ImmutableSet.<E>builder();
               boolean foundValues = false;
               in.beginObject();
               while (in.hasNext()) {
                  String name = in.nextName();
                  if (Objects.equal("values", name)) {
                     foundValues = true;
                     builder.addAll(delegate.read(in));
                  } else {
                     in.skipValue();
                  }
               }
               checkState(foundValues, "Expected BEGIN_ARRAY or the object to contain an array called 'values'");
               in.endObject();
               return builder.build();
            } else {
               return delegate.read(in);
            }
         }
      }
   }
}
