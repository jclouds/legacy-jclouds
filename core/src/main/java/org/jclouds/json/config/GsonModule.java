/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.json.config;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.DateService;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.internal.EnumTypeAdapterThatReturnsFromValue;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonLiteral;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.ObjectMapTypeAdapter;
import com.google.gson.reflect.TypeToken;
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
   Gson provideGson(JsonBallAdapter jsonAdapter, DateAdapter adapter, ByteListAdapter byteListAdapter,
            ByteArrayAdapter byteArrayAdapter, SerializePropertiesDefaults propertiesAdapter,
            JsonAdapterBindings bindings) throws ClassNotFoundException, Exception {
      GsonBuilder builder = new GsonBuilder();
      Logger.getLogger("com.google.gson.ParameterizedTypeHandlerMap").setLevel(Level.OFF);
      builder.registerTypeHierarchyAdapter(Enum.class, new EnumTypeAdapterThatReturnsFromValue());
      builder.registerTypeHierarchyAdapter(Map.class, new ObjectMapTypeAdapter());
      builder.registerTypeAdapter(JsonBall.class, jsonAdapter);
      builder.registerTypeAdapter(Date.class, adapter);
      builder.registerTypeAdapter(new TypeToken<List<Byte>>() {
      }.getType(), byteListAdapter);
      builder.registerTypeAdapter(byte[].class, byteArrayAdapter);
      for (Map.Entry<Type, Object> binding : bindings.getBindings().entrySet()) {
         builder.registerTypeAdapter(binding.getKey(), binding.getValue());
      }
      return builder.create();
   }

   // http://code.google.com/p/google-gson/issues/detail?id=326
   @ImplementedBy(JsonBallAdapterImpl.class)
   public static interface JsonBallAdapter extends JsonSerializer<JsonBall>, JsonDeserializer<JsonBall> {

   }

   @Singleton
   public static class JsonBallAdapterImpl implements JsonBallAdapter {

      public JsonElement serialize(JsonBall src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonLiteral(src);
      }

      public JsonBall deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return new JsonBall(json.toString());
      }

   }

   @ImplementedBy(CDateAdapter.class)
   public static interface DateAdapter extends JsonSerializer<Date>, JsonDeserializer<Date> {

   }

   @ImplementedBy(HexByteListAdapter.class)
   public static interface ByteListAdapter extends JsonSerializer<List<Byte>>, JsonDeserializer<List<Byte>> {

   }

   @ImplementedBy(HexByteArrayAdapter.class)
   public static interface ByteArrayAdapter extends JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

   }

   @Singleton
   public static class HexByteListAdapter implements ByteListAdapter {

      @Override
      public List<Byte> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return Bytes.asList(CryptoStreams.hex(json.getAsString()));
      }

      @Override
      public JsonElement serialize(List<Byte> src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(CryptoStreams.hex(Bytes.toArray(src)));
      }

   }

   @Singleton
   public static class HexByteArrayAdapter implements ByteArrayAdapter {

      @Override
      public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return CryptoStreams.hex(json.getAsString());
      }

      @Override
      public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(CryptoStreams.hex(src));
      }
   }

   @Singleton
   public static class Iso8601DateAdapter implements DateAdapter {
      private final DateService dateService;

      @Inject
      private Iso8601DateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(dateService.iso8601DateFormat(src));
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.getAsJsonPrimitive().getAsString();
         try {
            return dateService.iso8601DateParse(toParse);
         } catch (RuntimeException e) {
            return dateService.iso8601SecondsDateParse(toParse);
         }
      }

   }

   @Singleton
   public static class CDateAdapter implements DateAdapter {
      private final DateService dateService;

      @Inject
      private CDateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(dateService.cDateFormat(src));
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.getAsJsonPrimitive().getAsString();
         Date toReturn = dateService.cDateParse(toParse);
         return toReturn;
      }

   }

   @Singleton
   public static class LongDateAdapter implements DateAdapter {

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(src.getTime());
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         long toParse = json.getAsJsonPrimitive().getAsLong();
         if (toParse == -1)
            return null;
         Date toReturn = new Date(toParse);
         return toReturn;
      }
   }

   @Singleton
   public static class JsonAdapterBindings {
      private final Map<Type, Object> bindings = Maps.newHashMap();

      @com.google.inject.Inject(optional = true)
      public void setBindings(Map<Type, Object> bindings) {
         this.bindings.putAll(bindings);
      }

      public Map<Type, Object> getBindings() {
         return bindings;
      }
   }

   @Override
   protected void configure() {
      bind(Json.class).to(GsonWrapper.class);
   }
}
