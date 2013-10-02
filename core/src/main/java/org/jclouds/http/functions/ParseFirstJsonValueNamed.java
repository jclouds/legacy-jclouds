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
package org.jclouds.http.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.jclouds.http.HttpResponse;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.Atomics;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public class ParseFirstJsonValueNamed<T> implements Function<HttpResponse, T> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final GsonWrapper json;
   private final TypeLiteral<T> type;
   private final ImmutableSet<String> nameChoices;

   /**
    * @param nameChoices
    *           tried in order, first match wins
    */
   public ParseFirstJsonValueNamed(GsonWrapper json, TypeLiteral<T> type, String... nameChoices) {
      this.json = checkNotNull(json, "json");
      this.type = checkNotNull(type, "type");
      this.nameChoices = ImmutableSet.copyOf(checkNotNull(nameChoices, "nameChoices"));
   }

   @Override
   public T apply(HttpResponse arg0) {
      if (arg0.getPayload() == null)
         return nothing();
      JsonReader reader = null;
      try {
         reader = new JsonReader(new InputStreamReader(arg0.getPayload().getInput()));
         // in case keys are not in quotes
         reader.setLenient(true);
         AtomicReference<String> name = Atomics.newReference();
         JsonToken token = reader.peek();
         for (; token != JsonToken.END_DOCUMENT && nnn(reader, token, name); token = skipAndPeek(token, reader)) {
         }
         if (name.get() == null) {
            logger.trace("did not object named %s in json from response %s", nameChoices, arg0);
            return nothing();
         } else if (nameChoices.contains(name.get())) {
            return json.delegate().<T> fromJson(reader, type.getType());
         } else {
            return nothing();
         }
      } catch (IOException e) {
         throw new RuntimeException(String.format(
               "error reading from stream, parsing object named %s from http response %s", nameChoices, arg0), e);
      } finally {
         Closeables.closeQuietly(reader);
         arg0.getPayload().release();
      }
   }

   @SuppressWarnings("unchecked")
   private T nothing() {
      if (type.getRawType().isAssignableFrom(Set.class))
         return (T) ImmutableSet.of();
      else if (type.getRawType().isAssignableFrom(List.class))
         return (T) ImmutableList.of();
      else if (type.getRawType().isAssignableFrom(Map.class))
         return (T) ImmutableMap.of();
      return null;
   }

   private boolean nnn(JsonReader reader, JsonToken token, AtomicReference<String> name) throws IOException {
      if (token == JsonToken.NAME) {
         String name2 = reader.nextName();
         if (nameChoices.contains(name2)) {
            name.set(name2);
            return false;
         }
      }
      return true;

   }

   private JsonToken skipAndPeek(JsonToken token, JsonReader reader) throws IOException {
      switch (token) {
      case BEGIN_ARRAY:
         reader.beginArray();
         break;
      case END_ARRAY:
         reader.endArray();
         break;
      case BEGIN_OBJECT:
         reader.beginObject();
         break;
      case END_OBJECT:
         reader.endObject();
         break;
      case NAME:
         // NOTE that we have already advanced on NAME in the eval block;
         break;
      case STRING:
         reader.nextString();
         break;
      case NUMBER:
         reader.nextString();
         break;
      case BOOLEAN:
         reader.nextBoolean();
         break;
      case NULL:
         reader.nextNull();
         break;
      case END_DOCUMENT:
         break;
      }
      return reader.peek();
   }
}
