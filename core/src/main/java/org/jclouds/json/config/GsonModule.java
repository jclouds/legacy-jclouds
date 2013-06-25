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
package org.jclouds.json.config;

import static com.google.common.io.BaseEncoding.base16;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.internal.DeserializationConstructorAndReflectiveTypeAdapterFactory;
import org.jclouds.json.internal.EnumTypeAdapterThatReturnsFromValue;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.json.internal.NamingStrategies.AnnotationConstructorNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.AnnotationOrNameFieldNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.ExtractNamed;
import org.jclouds.json.internal.NamingStrategies.ExtractSerializedName;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.CollectionTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.FluentIterableTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ImmutableListTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ImmutableSetTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.IterableTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ListTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.MapTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.MultimapTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.SetTypeAdapterFactory;
import org.jclouds.json.internal.NullHackJsonLiteralAdapter;
import org.jclouds.json.internal.OptionalTypeAdapterFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;

/**
 * Contains logic for parsing objects from Strings.
 * 
 * @author Adrian Cole
 */
public class GsonModule extends AbstractModule {

   @SuppressWarnings("rawtypes")
   @Provides
   @Singleton
   Gson provideGson(TypeAdapter<JsonBall> jsonAdapter, DateAdapter adapter, ByteListAdapter byteListAdapter,
         ByteArrayAdapter byteArrayAdapter, PropertiesAdapter propertiesAdapter, JsonAdapterBindings bindings,
         OptionalTypeAdapterFactory optional, SetTypeAdapterFactory set, ImmutableSetTypeAdapterFactory immutableSet,
         MapTypeAdapterFactory map, MultimapTypeAdapterFactory multimap, IterableTypeAdapterFactory iterable,
         CollectionTypeAdapterFactory collection, ListTypeAdapterFactory list,
         ImmutableListTypeAdapterFactory immutableList, FluentIterableTypeAdapterFactory fluentIterable,
         DefaultExclusionStrategy exclusionStrategy) {

      FieldNamingStrategy serializationPolicy = new AnnotationOrNameFieldNamingStrategy(ImmutableSet.of(
            new ExtractSerializedName(), new ExtractNamed()));

      GsonBuilder builder = new GsonBuilder().setFieldNamingStrategy(serializationPolicy)
                                             .setExclusionStrategies(exclusionStrategy);

      // simple (type adapters)
      builder.registerTypeAdapter(Properties.class, propertiesAdapter.nullSafe());
      builder.registerTypeAdapter(Date.class, adapter.nullSafe());
      builder.registerTypeAdapter(byte[].class, byteArrayAdapter.nullSafe());
      builder.registerTypeAdapter(JsonBall.class, jsonAdapter.nullSafe());
      builder.registerTypeAdapterFactory(optional);
      builder.registerTypeAdapterFactory(iterable);
      builder.registerTypeAdapterFactory(collection);
      builder.registerTypeAdapterFactory(list);
      builder.registerTypeAdapter(new TypeToken<List<Byte>>() {
      }.getType(), byteListAdapter.nullSafe());
      builder.registerTypeAdapterFactory(immutableList);
      builder.registerTypeAdapterFactory(set);
      builder.registerTypeAdapterFactory(immutableSet);
      builder.registerTypeAdapterFactory(map);
      builder.registerTypeAdapterFactory(multimap);
      builder.registerTypeAdapterFactory(fluentIterable);

      AnnotationConstructorNamingStrategy deserializationPolicy = new AnnotationConstructorNamingStrategy(
            ImmutableSet.of(ConstructorProperties.class, Inject.class), ImmutableSet.of(new ExtractNamed()));

      builder.registerTypeAdapterFactory(new DeserializationConstructorAndReflectiveTypeAdapterFactory(
            new ConstructorConstructor(ImmutableMap.<Type, InstanceCreator<?>>of()), serializationPolicy, Excluder.DEFAULT, deserializationPolicy));

      // complicated (serializers/deserializers as they need context to operate)
      builder.registerTypeHierarchyAdapter(Enum.class, new EnumTypeAdapterThatReturnsFromValue());

      for (Map.Entry<Type, Object> binding : bindings.getBindings().entrySet()) {
         builder.registerTypeAdapter(binding.getKey(), binding.getValue());
      }

      for (TypeAdapterFactory factory : bindings.getFactories()) {
         builder.registerTypeAdapterFactory(factory);
      }

      return builder.create();
   }

   @ImplementedBy(NoExclusions.class)
   public static interface DefaultExclusionStrategy extends ExclusionStrategy {
   }

   public static class NoExclusions implements DefaultExclusionStrategy {
      public boolean shouldSkipField(FieldAttributes f) {
         return false;
      }

      public boolean shouldSkipClass(Class<?> clazz) {
         return false;
      }
   }
   
   @ImplementedBy(CDateAdapter.class)
   public abstract static class DateAdapter extends TypeAdapter<Date> {

   }

   @Provides
   @Singleton
   protected TypeAdapter<JsonBall> provideJsonBallAdapter(NullHackJsonBallAdapter in) {
      return in;
   }

   public static class NullHackJsonBallAdapter extends NullHackJsonLiteralAdapter<JsonBall> {

      @Override
      protected JsonBall createJsonLiteralFromRawJson(String json) {
         return new JsonBall(json);
      }

   }

   @ImplementedBy(HexByteListAdapter.class)
   public abstract static class ByteListAdapter extends TypeAdapter<List<Byte>> {

   }

   @ImplementedBy(HexByteArrayAdapter.class)
   public abstract static class ByteArrayAdapter extends TypeAdapter<byte[]> {

   }

   @Singleton
   public static class HexByteListAdapter extends ByteListAdapter {

      @Override
      public void write(JsonWriter writer, List<Byte> value) throws IOException {
         writer.value(base16().lowerCase().encode(Bytes.toArray(value)));
      }

      @Override
      public List<Byte> read(JsonReader reader) throws IOException {
         return Bytes.asList(base16().lowerCase().decode(reader.nextString()));
      }

   }

   @Singleton
   public static class HexByteArrayAdapter extends ByteArrayAdapter {

      @Override
      public void write(JsonWriter writer, byte[] value) throws IOException {
         writer.value(base16().lowerCase().encode(value));
      }

      @Override
      public byte[] read(JsonReader reader) throws IOException {
         return base16().lowerCase().decode(reader.nextString());
      }
   }

   @Singleton
   public static class Iso8601DateAdapter extends DateAdapter {
      private final DateService dateService;

      @Inject
      public Iso8601DateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public void write(JsonWriter writer, Date value) throws IOException {
         writer.value(dateService.iso8601DateFormat(value));
      }

      public Date read(JsonReader reader) throws IOException {
         return parseDate(reader.nextString());
      }

      protected Date parseDate(String toParse) {
         try {
            return dateService.iso8601DateParse(toParse);
         } catch (RuntimeException e) {
            return dateService.iso8601SecondsDateParse(toParse);
         }
      }

   }

   @Singleton
   public static class PropertiesAdapter extends TypeAdapter<Properties> {
      private final Provider<Gson> gson;
      private final TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
      };

      @Inject
      public PropertiesAdapter(Provider<Gson> gson) {
         this.gson = gson;
      }

      @Override
      public void write(JsonWriter out, Properties value) throws IOException {
         Builder<String, String> srcMap = ImmutableMap.builder();
         for (Enumeration<?> propNames = value.propertyNames(); propNames.hasMoreElements();) {
            String propName = (String) propNames.nextElement();
            srcMap.put(propName, value.getProperty(propName));
         }
         gson.get().getAdapter(mapType).write(out, srcMap.build());
      }

      @Override
      public Properties read(JsonReader in) throws IOException {
         Properties props = new Properties();
         in.beginObject();
         while (in.hasNext()) {
            JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
            props.setProperty(in.nextString(), in.nextString());
         }
         in.endObject();
         return props;
      }

   }

   @Singleton
   public static class CDateAdapter extends DateAdapter {
      private final DateService dateService;

      @Inject
      public CDateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public void write(JsonWriter writer, Date value) throws IOException {
         writer.value(dateService.cDateFormat(value));
      }

      public Date read(JsonReader reader) throws IOException {
         return dateService.cDateParse(reader.nextString());
      }

   }

   @Singleton
   public static class LongDateAdapter extends DateAdapter {

      public void write(JsonWriter writer, Date value) throws IOException {
         writer.value(value.getTime());
      }

      public Date read(JsonReader reader) throws IOException {
         long toParse = reader.nextLong();
         if (toParse == -1)
            return null;
         return new Date(toParse);
      }
   }

   @Singleton
   public static class JsonAdapterBindings {
      private final Map<Type, Object> bindings = Maps.newHashMap();
      private final Set<TypeAdapterFactory> factories = Sets.newHashSet();

      @com.google.inject.Inject(optional = true)
      public void setBindings(Map<Type, Object> bindings) {
         this.bindings.putAll(bindings);
      }

      @com.google.inject.Inject(optional = true)
      public void setFactories(Set<TypeAdapterFactory> factories) {
         this.factories.addAll(factories);
      }

      public Map<Type, Object> getBindings() {
         return bindings;
      }

      public Set<TypeAdapterFactory> getFactories() {
         return factories;
      }
   }

   @Override
   protected void configure() {
      bind(Json.class).to(GsonWrapper.class);
   }
}
