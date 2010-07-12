/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef.config;

import static org.jclouds.Constants.PROPERTY_GSON_ADAPTERS;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;

import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Chef connection.
 * 
 * @author Adrian Cole
 */

public class ChefTypeAdapterModule extends AbstractModule {

   @Override
   protected void configure() {

   }

   @Provides
   @Singleton
   @Named(PROPERTY_GSON_ADAPTERS)
   public Map<Type, Object> provideCustomAdapterBindings(HexByteListAdapter byteListAdapter,
            HexByteArrayAdapter byteArrayAdapter) {
      Map<Type, Object> bindings = Maps.newHashMap();
      bindings.put(new TypeToken<List<Byte>>() {
      }.getType(), byteListAdapter);
      bindings.put(byte[].class, byteArrayAdapter);
      return bindings;
   }

   @Singleton
   public static class HexByteListAdapter implements JsonDeserializer<List<Byte>>,
            JsonSerializer<List<Byte>> {
      private final EncryptionService encryptionService;

      @Inject
      HexByteListAdapter(EncryptionService encryptionService) {
         this.encryptionService = encryptionService;
      }

      @Override
      public List<Byte> deserialize(JsonElement json, Type typeOfT,
               JsonDeserializationContext context) throws JsonParseException {
         return Bytes.asList(encryptionService.fromHex(json.getAsString()));
      }

      @Override
      public JsonElement serialize(List<Byte> src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(encryptionService.hex(Bytes.toArray(src)));
      }

   }

   @Singleton
   public static class HexByteArrayAdapter implements JsonDeserializer<byte[]>,
            JsonSerializer<byte[]> {
      private final EncryptionService encryptionService;

      @Inject
      HexByteArrayAdapter(EncryptionService encryptionService) {
         this.encryptionService = encryptionService;
      }

      @Override
      public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return encryptionService.fromHex(json.getAsString());
      }

      @Override
      public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(encryptionService.hex(src));
      }
   }

}