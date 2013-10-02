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
package org.jclouds.cloudstack.config;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.IterableTypeAdapter;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.IterableTypeAdapterFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;

/**
 * @author Adrian Cole
 */
public class CloudStackParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(CloudStackDateAdapter.class);
      bind(IterableTypeAdapterFactory.class).to(CommaDelimitedOKIterableTypeAdapterFactory.class);
   }

   /**
    * Data adapter for the date formats used by CloudStack.
    * 
    * Essentially this is a workaround for the CloudStack getUsage() API call returning a corrupted form of ISO-8601
    * dates, which have an unexpected pair of apostrophes, like 2011-12-12'T'00:00:00+00:00
    * 
    * @author Richard Downer
    */
   public static class CloudStackDateAdapter extends Iso8601DateAdapter {

      @Inject
      private CloudStackDateAdapter(DateService dateService) {
         super(dateService);
      }

      public Date read(JsonReader reader) throws IOException {
         return parseDate(reader.nextString().replaceAll("'T'", "T"));
      }

   }

   /**
    * Handles types that were previously strings and now arrays (ex. tags)
    * 
    * @author Adrian Cole
    */
   public static class CommaDelimitedOKIterableTypeAdapterFactory extends IterableTypeAdapterFactory {

      @Override
      @SuppressWarnings("unchecked")
      protected <E, I> TypeAdapter<I> newAdapter(TypeAdapter<E> elementAdapter) {
         return (TypeAdapter<I>) new Adapter<E>(elementAdapter);
      }

      public static final class Adapter<E> extends TypeAdapter<Iterable<E>> {

         private final IterableTypeAdapter<E> delegate;

         public Adapter(TypeAdapter<E> elementAdapter) {
            this.delegate = new IterableTypeAdapter<E>(elementAdapter);
            nullSafe();
         }

         public void write(JsonWriter out, Iterable<E> value) throws IOException {
            this.delegate.write(out, value);
         }

         @SuppressWarnings("unchecked")
         @Override
         public Iterable<E> read(JsonReader in) throws IOException {
            // HACK as cloudstack changed a field from String to Set!
            if (in.peek() == JsonToken.STRING) {
               String val = Strings.emptyToNull(in.nextString());
               return (Iterable<E>) (val != null ? Splitter.on(',').split(val) : ImmutableSet.of());
            } else {
               return delegate.read(in);
            }
         }
      }
   }
}
