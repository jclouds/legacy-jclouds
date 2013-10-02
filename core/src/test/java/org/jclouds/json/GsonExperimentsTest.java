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
package org.jclouds.json;

import static com.google.common.base.Objects.equal;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Atomics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class GsonExperimentsTest {
   public static final String json = "['hello',5,{name:'GREETINGS',source:'guest'}]";

   static class Event {
      private String name;
      private String source;

      private Event(String name, String source) {
         this.name = name;
         this.source = source;
      }

      @Override
      public String toString() {
         return String.format("(name=%s, source=%s)", name, source);
      }
   }

   private Gson gson;
   private String json2;

   @BeforeTest
   void setupSource() {
      gson = new Gson();
      Collection<Object> collection = Lists.newArrayList();
      collection.add("hello");
      collection.add(5);
      collection.add(new Event("GREETINGS", "guest"));
      json2 = gson.toJson(collection);
      assertEquals(json2, "[\"hello\",5,{\"name\":\"GREETINGS\",\"source\":\"guest\"}]");
   }

   public class OptionalTypeAdapterFactory implements TypeAdapterFactory {
      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if (typeToken.getRawType() != Optional.class || !(type instanceof ParameterizedType)) {
            return null;
         }

         Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
         TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
         return (TypeAdapter<T>) newOptionalAdapter(elementAdapter);
      }

      private <E> TypeAdapter<Optional<E>> newOptionalAdapter(final TypeAdapter<E> elementAdapter) {
         return new TypeAdapter<Optional<E>>() {
            public void write(JsonWriter out, Optional<E> value) throws IOException {
               if (value == null || !value.isPresent()) {
                  out.nullValue();
                  return;
               }
               elementAdapter.write(out, value.get());
            }

            public Optional<E> read(JsonReader in) throws IOException {
               if (in.peek() == JsonToken.NULL) {
                  in.nextNull();
                  return Optional.absent();
               }
               return Optional.of(elementAdapter.read(in));
            }
         };
      }
   }

   static class OptionalType {
      Optional<String> present = Optional.of("hello");
      Optional<String> notPresent = Optional.absent();

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         }
         if (object instanceof OptionalType) {
            final OptionalType other = OptionalType.class.cast(object);
            return equal(present, other.present) && equal(notPresent, other.notPresent);
         } else {
            return false;
         }
      }
   }

   public void testPersistOptional() {
      Gson gson = new GsonBuilder().registerTypeAdapterFactory(new OptionalTypeAdapterFactory()).create();
      String json = gson.toJson(new OptionalType());
      assertEquals(json, "{\"present\":\"hello\"}");
      assertEquals(gson.fromJson(json, OptionalType.class), new OptionalType());
   }

   // inspired by
   // http://code.google.com/p/google-gson/source/browse/trunk/extras/src/main/java/com/google/gson/extras/examples/rawcollections/RawCollectionsExample.java
   public void testRawCollectionsWithParser() {
      JsonParser parser = new JsonParser();
      JsonArray array = parser.parse(json).getAsJsonArray();
      String message = gson.fromJson(array.get(0), String.class);
      int number = gson.fromJson(array.get(1), int.class);
      Event event = gson.fromJson(array.get(2), Event.class);
      assertEquals(message, "hello");
      assertEquals(number, 5);
      assertEquals(event.toString(), new Event("GREETINGS", "guest").toString());
   }

   private final String nested = "{ \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] }";
   private final String nestedFurther = "{ \"listaccountsresponse\" : { \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] } }";

   // inspired by http://sites.google.com/site/gson/streaming
   public void testParseNestedElements() throws IOException {
      JsonReader reader = new JsonReader(new StringReader(nested));
      List<Event> val = parseThingFromReaderOrNull("event", reader, new TypeLiteral<List<Event>>() {
      }.getType());
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   public void testParseNestedFurtherElements() throws IOException {
      JsonReader reader = new JsonReader(new StringReader(nestedFurther));
      List<Event> val = parseThingFromReaderOrNull("event", reader, new TypeLiteral<List<Event>>() {
      }.getType());
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   protected <T> T parseThingFromReaderOrNull(String toFind, JsonReader reader, Type type) throws IOException {
      AtomicReference<String> name = Atomics.newReference();
      JsonToken token = reader.peek();
      for (; token != JsonToken.END_DOCUMENT && nnn(toFind, reader, token, name); token = skipAndPeek(token, reader))
         ;
      T val = gson.<T> fromJson(reader, type);
      reader.close();
      return val;
   }

   protected boolean nnn(String toFind, JsonReader reader, JsonToken token, AtomicReference<String> name)
         throws IOException {
      if (token == JsonToken.NAME) {
         String name2 = reader.nextName();
         if (toFind.equals(name2)) {
            name.set(name2);
            return false;
         }
      }
      return true;

   }

   public JsonToken skipAndPeek(JsonToken token, JsonReader reader) throws IOException {
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
